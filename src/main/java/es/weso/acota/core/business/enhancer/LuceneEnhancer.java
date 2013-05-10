package es.weso.acota.core.business.enhancer;

import java.io.IOException;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import es.weso.acota.core.CoreConfiguration;
import es.weso.acota.core.business.enhancer.EnhancerAdapter;
import es.weso.acota.core.business.enhancer.analyzer.lucene.DefaultStopAnalyzer;
import es.weso.acota.core.business.enhancer.analyzer.lucene.EnglishStopAnalyzer;
import es.weso.acota.core.business.enhancer.analyzer.lucene.SpanishStopAnalyzer;
import es.weso.acota.core.entity.ProviderTO;
import es.weso.acota.core.entity.TagTO;
import es.weso.acota.core.exceptions.AcotaConfigurationException;
import es.weso.acota.core.utils.lang.LanguageDetector;

import static es.weso.acota.core.utils.lang.LanguageDetector.ISO_639_ENGLISH;
import static es.weso.acota.core.utils.lang.LanguageDetector.ISO_639_SPANISH;


/**
 * LuceneEnhancer is an {@link Enhancer} specialized in tokenizing, removing stop-words and
 * cleaning the input data, producing a set of single-word {@link TagTO}s
 * 
 * @author Jose María Álvarez
 * @author César Luis Alvargonzález
 */
public class LuceneEnhancer extends EnhancerAdapter implements Configurable {

	protected static Logger logger = Logger.getLogger(LuceneEnhancer.class);
	
	protected static final String DESCIPTION = "description";
	protected static final String LABEL = "label";
	
	protected double luceneLabelRelevance;
	protected double luceneTermRelevance;

	protected LanguageDetector languageDetector;
	
	protected CoreConfiguration configuration;
	
	/**
	 * Zero-argument default constructor
	 * @throws AcotaConfigurationException Any exception that occurs while initializing 
	 * a Configuration object
	 */
	public LuceneEnhancer() throws AcotaConfigurationException{
		super();
		LuceneEnhancer.provider = new ProviderTO("Lucene Enhancer");
		loadConfiguration(configuration);
	}
	
	/**
	 * One-argument constructor
	 * @param configuration acota-core's configuration class
	 * @throws AcotaConfigurationException Any exception that occurs while initializing 
	 * a Configuration object
	 */
	public LuceneEnhancer(CoreConfiguration configuration) throws AcotaConfigurationException{
		super();
		LuceneEnhancer.provider = new ProviderTO("Lucene Enhancer");
		loadConfiguration(configuration);
	}
	
	@Override
	public void loadConfiguration(CoreConfiguration configuration) throws AcotaConfigurationException{
		if(configuration==null)
			configuration = new CoreConfiguration();
		this.configuration = configuration;
		
		this.luceneLabelRelevance = configuration.getLuceneLabelRelevance();
		this.luceneTermRelevance = configuration.getLuceneTermRelevance();
		
		this.languageDetector = LanguageDetector.getInstance(configuration);
	}
	
	@Override
	protected void preExecute() throws Exception {
		this.suggest = request.getSuggestions();
		this.tags = suggest.getTags();
		suggest.setResource(request.getResource());
	}
	
	@Override
	protected void execute() throws Exception {
		extractLabelTerms();
		extractDescriptionTerms();
	}

	@Override
	protected void postExecute() throws Exception {
		logger.debug("Add providers to request");
		this.request.getTargetProviders().add(provider);
		logger.debug("Add suggestons to request");
		this.request.setSuggestions(suggest);
	}

	/**
	 * Extracts Description Terms
	 * @throws IOException Any exception that occurs while reading Lucene's TokenStream
	 * @throws AcotaConfigurationException 
	 */
	protected void extractDescriptionTerms() throws IOException, AcotaConfigurationException {
		extractTerms(DESCIPTION, request.getResource().getDescription(),
			luceneTermRelevance);
	}

	/**
	 * Extracts Label Terms
	 * @throws IOException Any exception that occurs while reading Lucene's TokenStream
	 * @throws AcotaConfigurationException 
	 */
	protected void extractLabelTerms() throws IOException, AcotaConfigurationException {
		extractTerms(LABEL, request.getResource().getLabel(),
					luceneLabelRelevance);
	}

	/**
	 * Tokenizes and removes stop-words (Spanish and English) from the supplied text
	 * @param title FieldName (description, label)
	 * @param text Text to extract the terms
	 * @param relevance Weight which is incremented each matched term
	 * @throws IOException Any exception that occurs while reading Lucene's TokenStream
	 * @throws AcotaConfigurationException 
	 */
	protected void extractTerms(String title, String text, double relevance)
			throws IOException, AcotaConfigurationException {

		String language = languageDetector.detect(text);
		Analyzer analyzer = loadAnalyzer(language);

		logger.debug("Get tokens of texts");
		TokenStream stream = analyzer.tokenStream(title, new StringReader(
				text));
		CharTermAttribute termAttribute = stream
				.getAttribute(CharTermAttribute.class);

		TagTO tag = null;
		logger.debug("Add tag to suggestions");
		while (stream.incrementToken()) {
			tag = createTag(termAttribute, language);
			fillSuggestions(tag, relevance);
		}
	}

	/**
	 * Creates and adds a new tag to the suggest tags set
	 * @param attribute Label's attribute
	 * @param language Tag Language
	 * @return Created tag
	 */
	protected TagTO createTag(CharTermAttribute attribute, String language) {
		TagTO tag = new TagTO();
		tag.setLabel(attribute.toString());
		tag.setProvider(provider);
		tag.setTagged(request.getResource());
		tag.setLang(language);
		return tag;
	}

	/**
	 * Loads a language analyzer (English, Spanish or Default)
	 * @param language Language of the analyzer to load
	 * @return Lucene's {@link Analyzer}
	 * @throws AcotaConfigurationException 
	 */
	protected Analyzer loadAnalyzer(String language) throws AcotaConfigurationException {
		Analyzer analyzer = null;
		if (language.equals(ISO_639_SPANISH)) {
			analyzer = SpanishStopAnalyzer.getInstance();
		} else if (language.equals(ISO_639_ENGLISH)) {
			analyzer = EnglishStopAnalyzer.getInstance();
		} else {
			analyzer = DefaultStopAnalyzer.getInstance();
		}
		return analyzer;
	}

}
