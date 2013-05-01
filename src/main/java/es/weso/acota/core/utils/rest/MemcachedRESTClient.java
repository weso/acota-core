package es.weso.acota.core.utils.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import es.weso.acota.core.CoreConfiguration;
import es.weso.acota.core.business.enhancer.Configurable;
import es.weso.acota.core.exceptions.AcotaConfigurationException;
import es.weso.acota.core.exceptions.AcotaRESTException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

/**
 * 
 * @author César Luis Alvargonzález
 * @since 0.3.8
 */
public class MemcachedRESTClient implements Configurable{
	
	public static final String TEXT_XML = "text/xml";
	public static final String APPLICATION_XML = "application/xml";
	
	protected static Logger log;
	private static MemcachedRESTClient MEMCACHED_REST_CLIENT;
	
	protected MemcachedClientBuilder builder;
	protected MemcachedClient memcachedClient;
	
	protected String memcachedUrls;
	protected int memcachedExpireTime;
	protected boolean memcachedEnabled;
	
	/**
	 * Default Constructor
	 * @param configuration acota-core's configuration class
	 * @throws AcotaConfigurationException Any exception that occurs while initializing 
	 * a Configuration object
	 */
	private MemcachedRESTClient(CoreConfiguration configuration) throws AcotaConfigurationException{
		MemcachedRESTClient.log = Logger.getLogger(MemcachedRESTClient.class);
		loadConfiguration(configuration);
	}
	
	@Override
	protected void finalize() throws Throwable {
		try{
			if(memcachedClient!= null && memcachedClient.isShutdown()){
				memcachedClient.shutdown();
			}
		}catch(Throwable T){
			throw T;
		}finally{
			super.finalize();
		}
	}
	
	/**
	 * Gets an instance of {@link MemcachedRESTClient}, 
	 * in the case it does not exists, it will create one,
	 * @param configuration acota-core's configuration class
	 * @return The {@link MemcachedRESTClient}'s instance
	 * @throws AcotaConfigurationException Any exception that occurs while initializing 
	 * a Configuration object
	 */
	public static MemcachedRESTClient getInstance(CoreConfiguration configuration) throws AcotaConfigurationException{
		if(MEMCACHED_REST_CLIENT==null){
			MemcachedRESTClient.MEMCACHED_REST_CLIENT = new MemcachedRESTClient(configuration);
		}
		return MEMCACHED_REST_CLIENT;
	}

	@Override
	public void loadConfiguration(CoreConfiguration configuration)
			throws AcotaConfigurationException {
		this.memcachedUrls = configuration.getMemcachedUrls();
		this.memcachedExpireTime = configuration.getMemcachedExpireTime();
		this.memcachedEnabled = configuration.getMemcachedEnabled();
		if(memcachedEnabled){
			try{
				this.builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(memcachedUrls));
				if(memcachedClient!=null && memcachedClient.isShutdown()){
					memcachedClient.shutdown();
				}
				this.memcachedClient = builder.build();
			}catch(IOException e){
				throw new AcotaConfigurationException("Fail Startging Memcached", e);
			}
		}
		
	}
	
	/**
	 * 
	 * @param url URL of the REST Call
	 * @param accept Format Expected  by the REST CALL
	 * @param encoding Encoding Expected by the REST CALL
	 * @return Response as plain text (RAW)
	 * @throws IOException
	 * @throws AcotaRESTException
	 */
	public String execute(String url, String accept,  String encoding) throws IOException, AcotaRESTException{
		String result;
		if(memcachedEnabled){
			try {
				result = getResultFromCache(url, accept, encoding);
			} catch (TimeoutException e) {
				result = treatCacheException(url, accept, encoding, e);
			} catch (InterruptedException e) {
				result = treatCacheException(url, accept, encoding, e);
			} catch (MemcachedException e) {
				result = treatCacheException(url, accept, encoding, e);
			}
		}else{
			result = makeRESTCall(url, accept, encoding);
		}
		return result;
	}
	
	/**
	 * Retrieves the results from the cache, if the file is not available,
	 * it will perform the REST call
	 * @param url URL of the REST Call
	 * @param accept Format Expected  by the REST CALL
	 * @param encoding Encoding Expected by the REST Call
	 * @return Response as plain text (RAW)
	 * @throws TimeoutException
	 * @throws InterruptedException
	 * @throws MemcachedException
	 * @throws AcotaRESTException
	 */
	protected String getResultFromCache(String url, String accept, String encoding) throws TimeoutException,
			InterruptedException, MemcachedException, AcotaRESTException {
		String key = "rest_" + url.hashCode();
		String response = memcachedClient.get(key);
		if (response == null) {
			response = queryEndpointAndStore(key, url, accept, encoding);
		}
		return response;
	}

	/**
	 * An issue occurs with Memcached, so it will perform the REST Call
	 * @param url URL of the REST Call
	 * @param accept Format Expected  by the REST CALL
	 * @param encoding Encoding Expected by the REST CALL
	 * @param e
	 * @return Response as plain text (RAW)
	 * @throws IOException
	 */
	protected String treatCacheException(String url, String accept, String encoding, Exception e) throws IOException {
		log.error("An error occured querying the cache. "
				+ "Querying SPARQL endpoint", e);
		return makeRESTCall(url, accept, encoding);
	}
	
	/**
	 * Perform the Rest Call and stores it in the cache
	 * @param key Cache key to be stored
	 * @param url URL of the REST Call
	 * @param accept Format Expected  by the REST CALL
	 * @param encoding Encoding Expected by the REST CALL
	 * @return Response as plain text (RAW)
	 * @throws TimeoutException
	 * @throws InterruptedException
	 * @throws MemcachedException
	 * @throws AcotaRESTException
	 */
	protected String queryEndpointAndStore(String key, String url, String accept, String encoding)
			throws TimeoutException, InterruptedException, MemcachedException, AcotaRESTException {
		String response = "";
		try {
			response = makeRESTCall(url, accept, encoding);
			log.info("Catching: "+key);
			memcachedClient.set(key, memcachedExpireTime, response);
		} catch (IOException e) {
			log.error("An error ocurred execution the REST call. "
					+ "The results will not be stored in the cache.", e);
			throw new AcotaRESTException("An error ocurred execution the REST call. "
					+ "The results will not be stored in the cache.", e);
		}
		return response;
	}
	
	/**
	 * Performs the REST Call
	 * @param url URL of the REST Call
	 * @param accept Format Expected  by the REST CALL
	 * @param encoding Encoding Expected by the REST CALL
	 * @return Response as plain text (RAW)
	 * @throws IOException
	 */
	protected String makeRESTCall(String url, String accept, String encoding) throws IOException{
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Accept", accept);
			try{
				connection.connect();
			}catch(Exception e){
				connection.connect();
			}
			if (isValidResponse(connection)) {
				
				BufferedReader br = new BufferedReader(new InputStreamReader(
						connection.getInputStream(), encoding));
				
				StringBuilder response = new StringBuilder();
				String output;
				while ((output = br.readLine()) != null) {
					response.append(output);
				}
				return response.toString();
			}
		} finally {
			if (null != connection) {
				connection.disconnect();
			}
		}
		throw new IOException("Bad Request");
	}
	

	/**
	 * Checks if a HTTP connection has a valid response (200 OK) 
	 * @param connection HTTP Connection
	 * @return true If the response is "HTTP/1.0 200 OK", false In other case
	 * @throws IOException if an error occurred connecting to the server.
	 */
	protected boolean isValidResponse(HttpURLConnection connection)
			throws IOException {
		return connection.getResponseCode() == HttpURLConnection.HTTP_OK
				&& connection.getContentType().contains(TEXT_XML);
	}
	
}
