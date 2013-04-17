package es.weso.acota.core.business.enhancer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import com.sun.org.apache.xpath.internal.XPathAPI;

import es.weso.acota.core.CoreConfiguration;
import es.weso.acota.core.business.enhancer.EnhancerAdapter;
import es.weso.acota.core.entity.ProviderTO;
import es.weso.acota.core.entity.TagTO;
import es.weso.acota.core.exceptions.AcotaConfigurationException;
import es.weso.acota.core.exceptions.DocumentBuilderException;
import es.weso.acota.core.utils.DocumentBuilderHelper;
import es.weso.acota.core.utils.LanguageUtil;
import es.weso.acota.core.utils.MemcachedRESTClient;

/**
 * GoogleEnhancer is an {@link Enhancer} specialized in making calls to Google 
 * Autocomplete's API with the different {@link TagTO}'s labels, enriching
 * the set of {@link TagTO}s
 * 
 * @author Jose María Álvarez
 * @author César Luis Alvargonzález
 */
public class GoogleEnhancer extends EnhancerAdapter implements Configurable {

	protected static Logger logger = Logger.getLogger(GoogleEnhancer.class);
	
	protected String googleUrl;
	protected String googleEncoding;
	
	protected double googleRelevance;
	
	protected MemcachedRESTClient restClient;
	
	protected CoreConfiguration configuration;
	
	/**
	 * Zero-argument default constructor
	 * @throws AcotaConfigurationException Any exception that occurs 
	 * while initializing Configuration object
	 */
	public GoogleEnhancer() throws AcotaConfigurationException{
		super();
		GoogleEnhancer.provider = new ProviderTO("Google Enhancer");
		loadConfiguration(configuration);
	}

	/**
	 * Zero-argument default constructor
	 * @param configuration acota-core's configuration class
	 * @throws AcotaConfigurationException Any exception that occurs 
	 * while initializing Configuration object
	 */
	public GoogleEnhancer(CoreConfiguration core) throws AcotaConfigurationException{
		super();
		GoogleEnhancer.provider = new ProviderTO("Google Enhancer");
		loadConfiguration(configuration);
	}
	
	@Override
	public void loadConfiguration(CoreConfiguration configuration) throws AcotaConfigurationException{
		if(configuration==null)
			configuration = new CoreConfiguration();
		this.configuration = configuration;
		this.googleUrl = configuration.getGoogleUrl();
		this.googleEncoding = configuration.getGoogleEncoding();
		this.googleRelevance = configuration.getGoogleRelevance();
		this.restClient = new MemcachedRESTClient(configuration);
	}
	
	@Override
	protected void execute() throws Exception {	
		Set<Entry<String, TagTO>> backupSet = new HashSet<Entry<String, TagTO>>();
		for (Entry<String, TagTO> label : tags.entrySet()) {
			backupSet.add(label);
		}
		for (Entry<String, TagTO> label : backupSet) {
			String result = restClient.execute(generateURL(label.getKey()), 
					MemcachedRESTClient.APPLICATION_XML, googleEncoding);
			Document document = processResponse(result);
			processDocument(document);
		}
	}

	/**
	 * Generates the Google (Auto)Complete URL
	 * @param label Label to generate the Google (Auto)Complete URL
	 * @return URL of the Google (Auto)Complete related to the label
	 * @throws UnsupportedEncodingException The Character Encoding is not supported.
	 * @throws AcotaConfigurationException Any exception that occurs 
	 * while initializing Configuration object
	 */
	private String generateURL(String label)
			throws UnsupportedEncodingException, AcotaConfigurationException {
		StringBuilder url = new StringBuilder(googleUrl)
			.append(URLEncoder.encode(label, "utf8"));
		String language = LanguageUtil.detect(label);
		if(language.equals(LanguageUtil.ISO_639_UNDEFINED)){
			url.append("&hl=").append(LanguageUtil.detect(label));
		}
		return url.toString();
	}

	@Override
	protected void preExecute() throws Exception {
		this.suggest = request.getSuggestions();
		this.tags = suggest.getTags();
		suggest.setResource(request.getResource());
	}

	@Override
	protected void postExecute() throws Exception {
		this.request.getTargetProviders().add(provider);
		this.request.setSuggestions(suggest);
	}

	/**
	 * Transforms the Google Autocomplete REST call return to an XML Document
	 * @param response Rest call response on RAW
	 * @return XML Document returned by the rest call
	 * @throws DocumentBuilderException Any exception that occurs during the DOM creation.
	 */
	protected Document processResponse(String response) throws DocumentBuilderException{
		return DocumentBuilderHelper.getDocumentFromString(response);
	}

	/**
	 * Loads Google Autocomplete's XML result document into the tags map
	 * @param result Google Autocomplete's XML result document
	 * @throws TransformerException If happens an exceptional condition that
	 * occurred during the transformation process.
	 */
	protected void processDocument(Document result) throws TransformerException {
		NodeIterator it = XPathAPI.selectNodeIterator(result, "//suggestion/@data");
		Node node = null;

		while ((node = it.nextNode()) != null) {
			TagTO tag = new TagTO(node.getNodeValue().trim(), provider,
					request.getResource());
			fillSuggestions(tag, googleRelevance);
		}
	}

}