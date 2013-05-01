package es.weso.acota.core.business.enhancer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
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
import es.weso.acota.core.exceptions.AcotaDocumentBuilderException;
import es.weso.acota.core.exceptions.AcotaRESTException;
import es.weso.acota.core.utils.AcotaUtil;
import es.weso.acota.core.utils.documents.DocumentBuilderHelper;
import es.weso.acota.core.utils.lang.LanguageDetector;
import es.weso.acota.core.utils.rest.MemcachedRESTClient;

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
	
	protected int googleLimit;
	protected int googlePercentile ;
	protected double googleRelevance;
	
	protected MemcachedRESTClient restClient;
	protected LanguageDetector languageDetector;
	
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
	public GoogleEnhancer(CoreConfiguration configuration) throws AcotaConfigurationException{
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
		this.googlePercentile = configuration.getGooglePercentile();
		this.googleLimit = configuration.getGoogleLimit();
		this.restClient = MemcachedRESTClient.getInstance(configuration);
		this.languageDetector = LanguageDetector.getInstance(configuration);
	}
	
	@Override
	protected void execute() throws Exception {	
		List<Entry<String, TagTO>> sortedTags = 
				AcotaUtil.sortTags(AcotaUtil.backupTags(tags));
		
		long percentileLimit = Math.round(sortedTags.size() * (googlePercentile/100d));
		long currentLimit = googleLimit < percentileLimit ? googleLimit : percentileLimit;
		
		for (int i = 0; i < currentLimit; i++) {
			enrich(sortedTags, i);
		}
	}

	/**
	 * Performs the enrichment
	 * @param sortedTags List of Sorted Tags
	 * @param i Position to enrich
	 * @throws AcotaConfigurationException Any exception that occurs 
	 * while initializing Configuration object
	 * @throws IOException
	 * @throws AcotaRESTException
	 * @throws UnsupportedEncodingException The Character Encoding is not supported.
	 * @throws AcotaDocumentBuilderException
	 * @throws TransformerException If happens an exceptional condition that
	 * occurred during the transformation process.
	 */
	private void enrich(List<Entry<String, TagTO>> sortedTags, int i)
			throws AcotaConfigurationException, IOException, AcotaRESTException,
			UnsupportedEncodingException, AcotaDocumentBuilderException,
			TransformerException {
		String label = sortedTags.get(i).getKey();
		String language = languageDetector.detect(label);
		String result = restClient.execute(generateURL(label, language), 
				MemcachedRESTClient.APPLICATION_XML, googleEncoding);
		Document document = processResponse(result);
		processDocument(document, language);
	}

	/**
	 * Generates the Google (Auto)Complete URL
	 * @param label Label to generate the Google (Auto)Complete URL
	 * @param language Label language
	 * @return URL of the Google (Auto)Complete related to the label
	 * @throws UnsupportedEncodingException The Character Encoding is not supported.
	 * @throws AcotaConfigurationException Any exception that occurs 
	 * while initializing Configuration object
	 */
	private String generateURL(String label, String language)
			throws UnsupportedEncodingException, AcotaConfigurationException {
		StringBuilder url = new StringBuilder(googleUrl)
			.append(URLEncoder.encode(label, "utf8"));
		if(language.equals(LanguageDetector.ISO_639_UNDEFINED)){
			url.append("&hl=").append(languageDetector.detect(label));
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
	 * @throws AcotaDocumentBuilderException Any exception that occurs during the DOM creation.
	 */
	protected Document processResponse(String response) throws AcotaDocumentBuilderException{
		return DocumentBuilderHelper.getDocumentFromString(response);
	}

	/**
	 * Loads Google Autocomplete's XML result document into the tags map
	 * @param result Google Autocomplete's XML result document
	 * @param language Language of the document
	 * @throws TransformerException If happens an exceptional condition that
	 * occurred during the transformation process.
	 */
	protected void processDocument(Document result, String language) throws TransformerException {
		NodeIterator it = XPathAPI.selectNodeIterator(result, "//suggestion/@data");
		Node node = null;

		while ((node = it.nextNode()) != null) {
			TagTO tag = new TagTO(node.getNodeValue().trim(), language, provider,
					request.getResource());
			fillSuggestions(tag, googleRelevance);
		}
	}

}