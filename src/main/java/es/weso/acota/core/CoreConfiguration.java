package es.weso.acota.core;

import java.util.Collections;
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import es.weso.acota.core.exceptions.AcotaConfigurationException;
import es.weso.acota.core.utils.ExternalizableConfiguration;
/**
 * The main task of this class is to load the core configuration properties of
 * ACOTA, this properties could be set programmatically or by a configuration
 * file called acota.properties
 * 
 * @author César Luis Alvargonzález
 */
public class CoreConfiguration implements Configuration {

	protected static final String INTERNAL_ACOTA_PROPERTIES_PATH = "/resources/inner.acota.properties";

	protected static Logger LOGGER;
	protected static CompositeConfiguration CONFIG;
	protected String googleEncoding;

	protected Double googleRelevance;
	protected String googleUrl;
	protected Double luceneLabelRelevance;

	protected Double luceneTermRelevance;
	
	protected String openNlpEnPosBin;
	protected String openNlpEnSentBin;
	protected String openNlpEnTokBin;
	protected List<String> openNlpEnTokens;
	protected List<String> openNlpEnNouns;
	protected List<String> openNlpEnVerbs;
	protected List<String> openNlpEnNumbers;

	protected String openNlpEsPosBin;
	protected String openNlpEsSentBin;
	protected String openNlpEsTokBin;
	protected List<String> openNlpEsTokens;
	protected List<String> openNlpEsNouns;
	protected List<String> openNlpEsVerbs;
	protected List<String> openNlpEsNumbers;

	protected String tokenizerEnPattern;
	protected List<String> tokenizerEnTokens;

	protected String tokenizerEsPattern;
	protected List<String> tokenizerEsTokens;

	protected Integer tokenizerK;

	protected Double tokenizerLabelRelevance;
	protected Double tokenizerTermRelevance;

	protected String wordnetEnDict;

	protected Double wordnetRelevance;

	/**
	 * Zero-argument default constructor.
	 * 
	 * @throws AcotaConfigurationException
	 *             Any exception that occurs while initializing a Acota's
	 *             Configuration object
	 */
	public CoreConfiguration() throws AcotaConfigurationException {
		CoreConfiguration.LOGGER = Logger.getLogger(CoreConfiguration.class);

		if (CONFIG == null) {
			loadsConfiguration();
		}

		loadLuceneEnhancerConfig();
		loadTokenizerEnhancerConfig();
		loadOpenNlpEnhancerConfig();
		loadWordnetEnhancerConfig();
		loadGoogleEnhancerConfig();
	}

	public String getGoogleEncoding() {
		return googleEncoding;
	}

	public Double getGoogleRelevance() {
		return googleRelevance;
	}

	public String getGoogleUrl() {
		return googleUrl;
	}

	public Double getLuceneLabelRelevance() {
		return luceneLabelRelevance;
	}

	public Double getLuceneTermRelevance() {
		return luceneTermRelevance;
	}

	public String getOpenNlpEnPosBin() {
		return openNlpEnPosBin;
	}

	public String getOpenNlpEnSentBin() {
		return openNlpEnSentBin;
	}

	public String getOpenNlpEnTokBin() {
		return openNlpEnTokBin;
	}

	public String getOpenNlpEsPosBin() {
		return openNlpEsPosBin;
	}

	public String getOpenNlpEsSentBin() {
		return openNlpEsSentBin;
	}

	public String getOpenNlpEsTokBin() {
		return openNlpEsTokBin;
	}

	public String getTokenizerEnPattern() {
		return tokenizerEnPattern;
	}

	public List<String> getTokenizerEnTokens() {
		return tokenizerEnTokens;
	}

	public String getTokenizerEsPattern() {
		return tokenizerEsPattern;
	}

	public List<String> getTokenizerEsTokens() {
		return tokenizerEsTokens;
	}

	public Integer getTokenizerK() {
		return tokenizerK;
	}

	public Double getTokenizerLabelRelevance() {
		return tokenizerLabelRelevance;
	}

	public Double getTokenizerTermRelevance() {
		return tokenizerTermRelevance;
	}

	public String getWordnetEnDict() {
		return wordnetEnDict;
	}

	public Double getWordnetRelevance() {
		return wordnetRelevance;
	}

	public void setGoogleEncoding(String googleEncoding) {
		this.googleEncoding = googleEncoding;
	}

	public void setGoogleRelevance(Double googleRelevance) {
		this.googleRelevance = googleRelevance;
	}

	public void setGoogleUrl(String googleUrl) {
		this.googleUrl = googleUrl;
	}

	public void setLuceneLabelRelevance(Double luceneLabelRelevance) {
		this.luceneLabelRelevance = luceneLabelRelevance;
	}

	public void setLuceneTermRelevance(Double luceneTermRelevance) {
		this.luceneTermRelevance = luceneTermRelevance;
	}

	public void setOpenNlpEnPosBin(String openNlpEnPosBin) {
		this.openNlpEnPosBin = openNlpEnPosBin;
	}

	public void setOpenNlpEnSentBin(String openNlpEnSentBin) {
		this.openNlpEnSentBin = openNlpEnSentBin;
	}

	public void setOpenNlpEnTokBin(String openNlpEnTokBin) {
		this.openNlpEnTokBin = openNlpEnTokBin;
	}

	public void setOpenNlpEsPosBin(String openNlpEsPosBin) {
		this.openNlpEsPosBin = openNlpEsPosBin;
	}

	public void setOpenNlpEsSentBin(String openNlpEsSentBin) {
		this.openNlpEsSentBin = openNlpEsSentBin;
	}

	public void setOpenNlpEsTokBin(String openNlpEsTokBin) {
		this.openNlpEsTokBin = openNlpEsTokBin;
	}

	public List<String> getOpenNlpEnTokens() {
		return openNlpEnTokens;
	}

	public List<String> getOpenNlpEnNouns() {
		return openNlpEnNouns;
	}

	public List<String> getOpenNlpEnVerbs() {
		return openNlpEnVerbs;
	}

	public List<String> getOpenNlpEnNumbers() {
		return openNlpEnNumbers;
	}

	public List<String> getOpenNlpEsTokens() {
		return openNlpEsTokens;
	}

	public List<String> getOpenNlpEsNouns() {
		return openNlpEsNouns;
	}

	public List<String> getOpenNlpEsVerbs() {
		return openNlpEsVerbs;
	}

	public List<String> getOpenNlpEsNumbers() {
		return openNlpEsNumbers;
	}

	public void setOpenNlpEnTokens(List<String> openNlpEnTokens) {
		this.openNlpEnTokens = openNlpEnTokens;
	}

	public void setOpenNlpEnNouns(List<String> openNlpEnNouns) {
		this.openNlpEnNouns = openNlpEnNouns;
	}

	public void setOpenNlpEnVerbs(List<String> openNlpEnVerbs) {
		this.openNlpEnVerbs = openNlpEnVerbs;
	}

	public void setOpenNlpEnNumbers(List<String> openNlpEnNumbers) {
		this.openNlpEnNumbers = openNlpEnNumbers;
	}

	public void setOpenNlpEsTokens(List<String> openNlpEsTokens) {
		this.openNlpEsTokens = openNlpEsTokens;
	}

	public void setOpenNlpEsNouns(List<String> openNlpEsNouns) {
		this.openNlpEsNouns = openNlpEsNouns;
	}

	public void setOpenNlpEsVerbs(List<String> openNlpEsVerbs) {
		this.openNlpEsVerbs = openNlpEsVerbs;
	}

	public void setOpenNlpEsNumbers(List<String> openNlpEsNumbers) {
		this.openNlpEsNumbers = openNlpEsNumbers;
	}

	public void setTokenizerEnPattern(String tokenizerEnPattern) {
		this.tokenizerEnPattern = tokenizerEnPattern;
	}

	public void setTokenizerEnTokens(List<String> tokenizerEnTokens) {
		this.tokenizerEnTokens = tokenizerEnTokens;
	}

	public void setTokenizerEsPattern(String tokenizerEsPattern) {
		this.tokenizerEsPattern = tokenizerEsPattern;
	}

	public void setTokenizerEsTokens(List<String> tokenizerEsTokens) {
		this.tokenizerEsTokens = tokenizerEsTokens;
	}

	public void setTokenizerK(Integer tokenizerK) {
		this.tokenizerK = tokenizerK;
	}

	public void setTokenizerLabelRelevance(Double tokenizerLabelRelevance) {
		this.tokenizerLabelRelevance = tokenizerLabelRelevance;
	}

	public void setTokenizerTermRelevance(Double tokenizerTermRelevance) {
		this.tokenizerTermRelevance = tokenizerTermRelevance;
	}

	public void setWordnetEnDict(String wordnetEnDict) {
		this.wordnetEnDict = wordnetEnDict;
	}

	public void setWordnetRelevance(Double wordnetRelevance) {
		this.wordnetRelevance = wordnetRelevance;
	}

	/**
	 * @see es.weso.acota.core.Configuration#loadsConfiguration()
	 */
	public void loadsConfiguration() throws AcotaConfigurationException {
		CoreConfiguration.CONFIG = new CompositeConfiguration();
		
		try {
			CONFIG.addConfiguration(new PropertiesConfiguration(
					"acota.properties"));
		} catch (Exception e) {
			LOGGER.warn("acota.properties not found, Using default values.");
		}
		
		try {
			Class<?> resourceLoader = Class
					.forName("es.weso.acota.core.utils.ResourceLoader");
			ExternalizableConfiguration rlInstance = (ExternalizableConfiguration) resourceLoader
					.newInstance();
			CONFIG.addConfiguration(rlInstance.getConfiguration());
		} catch (Exception e) {
			LOGGER.warn("acota-utils jar not found.");
		}

		try {
			CONFIG.addConfiguration(new PropertiesConfiguration(this.getClass()
					.getResource(INTERNAL_ACOTA_PROPERTIES_PATH)));
		} catch (ConfigurationException e) {
			throw new AcotaConfigurationException(e);
		}
	}

	/**
	 * Loads {@linked es.weso.acota.core.business.enhancer.GoogleEnhancer}'s
	 * Configuration
	 */
	private void loadGoogleEnhancerConfig() {
		this.setGoogleUrl(CONFIG.getString("google.url"));
		this.setGoogleEncoding(CONFIG.getString("google.encoding"));
		this.setGoogleRelevance(CONFIG.getDouble("google.relevance"));
	}

	/**
	 * Loads {@linked es.weso.acota.core.business.enhancer.LuceneEnhancer}'s
	 * Configuration
	 */
	private void loadLuceneEnhancerConfig() {
		this.setLuceneTermRelevance(CONFIG.getDouble("lucene.term.relevance"));
		this.setLuceneLabelRelevance(CONFIG.getDouble("lucene.label.relevance"));
	}

	/**
	 * Loads {@linked es.weso.acota.core.business.enhancer.OpenNlpEnhancer}'s
	 * Configuration
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadOpenNlpEnhancerConfig() {
		this.setOpenNlpEsPosBin(CONFIG.getString("opennlp.es.pos"));
		this.setOpenNlpEsSentBin(CONFIG.getString("opennlp.es.sent"));
		this.setOpenNlpEsTokBin(CONFIG.getString("opennlp.es.tok"));
		this.setOpenNlpEsTokens((List) CONFIG.getList("opennlp.es.tokens",
				Collections.EMPTY_LIST));
		this.setOpenNlpEsNouns((List) CONFIG.getList("opennlp.es.nouns",
				Collections.EMPTY_LIST));
		this.setOpenNlpEsVerbs((List) CONFIG.getList("opennlp.es.verbs",
				Collections.EMPTY_LIST));
		this.setOpenNlpEsNumbers((List) CONFIG.getList("opennlp.es.numbers",
				Collections.EMPTY_LIST));

		this.setOpenNlpEnPosBin(CONFIG.getString("opennlp.en.pos"));
		this.setOpenNlpEnSentBin(CONFIG.getString("opennlp.en.sent"));
		this.setOpenNlpEnTokBin(CONFIG.getString("opennlp.en.tok"));
		this.setOpenNlpEnTokens((List) CONFIG.getList("opennlp.en.tokens",
				Collections.EMPTY_LIST));
		this.setOpenNlpEnNouns((List) CONFIG.getList("opennlp.en.nouns",
				Collections.EMPTY_LIST));
		this.setOpenNlpEnVerbs((List) CONFIG.getList("opennlp.en.verbs",
				Collections.EMPTY_LIST));
		this.setOpenNlpEnNumbers((List) CONFIG.getList("opennlp.en.numbers",
				Collections.EMPTY_LIST));
	}

	/**
	 * Loads {@linked es.weso.acota.core.business.enhancer.TokenizerEnhancer}'s
	 * Configuration
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadTokenizerEnhancerConfig() {

		this.setTokenizerK(CONFIG.getInteger("tokenizer.k", 1));
		this.setTokenizerLabelRelevance(CONFIG
				.getDouble("tokenizer.label.relevance"));
		this.setTokenizerTermRelevance(CONFIG
				.getDouble("tokenizer.term.relevance"));
		this.setTokenizerEnPattern(CONFIG.getString("tokenizer.en.pattern"));
		this.setTokenizerEnTokens((List) CONFIG.getList("tokenizer.en.tokens",
				Collections.EMPTY_LIST));
		this.setTokenizerEsPattern(CONFIG.getString("tokenizer.es.pattern"));
		this.setTokenizerEsTokens((List) CONFIG.getList("tokenizer.es.tokens",
				Collections.EMPTY_LIST));
	}

	/**
	 * Loads {@linked es.weso.acota.core.business.enhancer.WordnetEnhancer}'s
	 * Configuration
	 */
	private void loadWordnetEnhancerConfig() {
		this.setWordnetEnDict(CONFIG.getString("wordnet.en.dict"));
		this.setWordnetRelevance(CONFIG.getDouble("wordnet.relevance"));
	}

}