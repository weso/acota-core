package es.weso.acota.core.utils;

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
import es.weso.acota.core.exceptions.RESTException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

public class MemcachedRESTClient implements Configurable{
	
	public static final String TEXT_XML = "text/xml";
	public static final String APPLICATION_XML = "application/xml";
	
	private static Logger log = Logger.getLogger(MemcachedRESTClient.class);
	
	private MemcachedClientBuilder builder;
	private MemcachedClient memcachedClient;
	
	private String memcachedUrls;
	private int memcachedExpireTime;
	private boolean memcachedEnabled;
	
	/**
	 * Default Constructor
	 * @param configuration
	 * @throws AcotaConfigurationException
	 */
	public MemcachedRESTClient(CoreConfiguration configuration) throws AcotaConfigurationException{
		loadConfiguration(configuration);
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
				if(memcachedClient!=null){
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
	 * @param url
	 * @param accept
	 * @param encoding
	 * @return
	 * @throws IOException
	 * @throws RESTException
	 */
	public String execute(String url, String accept,  String encoding) throws IOException, RESTException{
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
	 * 
	 * @param url
	 * @param accept
	 * @param encoding
	 * @return
	 * @throws TimeoutException
	 * @throws InterruptedException
	 * @throws MemcachedException
	 * @throws RESTException
	 */
	private String getResultFromCache(String url, String accept, String encoding) throws TimeoutException,
			InterruptedException, MemcachedException, RESTException {
		String key = "rest_" + url.hashCode();
		String response = memcachedClient.get(key);
		if (response == null) {
			response = queryEndpointAndStore(key, url, accept, encoding);
		}
		return response;
	}

	/**
	 * 
	 * @param url
	 * @param accept
	 * @param encoding
	 * @param e
	 * @return
	 * @throws IOException
	 */
	private String treatCacheException(String url, String accept, String encoding, Exception e) throws IOException {
		log.error("An error occured querying the cache. "
				+ "Querying SPARQL endpoint", e);
		return makeRESTCall(url, accept, encoding);
	}
	
	/**
	 * 
	 * @param key
	 * @param url
	 * @param accept
	 * @param encoding
	 * @return
	 * @throws TimeoutException
	 * @throws InterruptedException
	 * @throws MemcachedException
	 * @throws RESTException
	 */
	private String queryEndpointAndStore(String key, String url, String accept, String encoding)
			throws TimeoutException, InterruptedException, MemcachedException, RESTException {
		String response = "";
		try {
			response = makeRESTCall(url, accept, encoding);
			log.info("Catching: "+key);
			memcachedClient.set(key, memcachedExpireTime, response);
		} catch (IOException e) {
			log.error("An error ocurred execution the REST call. "
					+ "The results will not be stored in the cache.", e);
			throw new RESTException("An error ocurred execution the REST call. "
					+ "The results will not be stored in the cache.", e);
		}
		return response;
	}
	
	/**
	 * 
	 * @param url
	 * @param accept
	 * @param encoding
	 * @return
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
	 * @return true If the response is "HTTP/1.0 200 OK"
	 * @return false In other case
	 * @throws IOException if an error occurred connecting to the server.
	 */
	protected boolean isValidResponse(HttpURLConnection connection)
			throws IOException {
		return connection.getResponseCode() == HttpURLConnection.HTTP_OK
				&& connection.getContentType().contains(TEXT_XML);
	}
	
}
