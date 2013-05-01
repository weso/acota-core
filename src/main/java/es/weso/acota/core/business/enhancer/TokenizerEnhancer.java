package es.weso.acota.core.business.enhancer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;

import es.weso.acota.core.CoreConfiguration;
import es.weso.acota.core.business.enhancer.analyzer.tokenizer.EnglishTokenizerAnalyzer;
import es.weso.acota.core.business.enhancer.analyzer.tokenizer.SpanishTokenizerAnalyzer;
import es.weso.acota.core.business.enhancer.analyzer.tokenizer.TokenizerAnalyzer;
import es.weso.acota.core.entity.ProviderTO;
import es.weso.acota.core.entity.TagTO;
import es.weso.acota.core.exceptions.AcotaConfigurationException;
import es.weso.acota.core.utils.lang.LanguageDetector;

import static es.weso.acota.core.utils.lang.LanguageDetector.ISO_639_SPANISH;

/**
 * TokenizerEnhancer is an {@link Enhancer} specialized in tokenizing, removing stop-words and
 * cleaning the input data, producing a set of k-word {@link TagTO}s, k is configurable and
 * supplied by {@link CoreConfiguration}.
 * 
 * @author César Luis Alvargonzález
 */
public class TokenizerEnhancer extends EnhancerAdapter implements Configurable {
	
	protected static final String DESCIPTION = "description";
	protected static final String LABEL = "label";

	protected double tokenizerRelevanceLabel;
	protected double tokenizerRelevanceTerm;

	protected int k;
	
	protected Map<StringArrayWrapper, Double> auxiliar;

	protected EnglishTokenizerAnalyzer englishTokenizerAnalyzer;
	protected SpanishTokenizerAnalyzer spanishTokenizerAnalyzer;
	
	protected TokenizerAnalyzer currentTokenizerAnalyzer;
	protected LanguageDetector languageUtil;
	
	protected CoreConfiguration configuration;

	/**
	 * Inner Class StringArrayWrapper is a wrap of a String[], so
	 * it could be used as key in Maps or similar
	 * data structures.
	 * @author César Luis Alvargonzález
	 *
	 */
	public final class StringArrayWrapper {
		protected final String[] data;

		/**
		 * One-argument default constructor.
		 * @param data String[] to store
		 */
		public StringArrayWrapper(String[] data) {
			if (data == null) {
				throw new NullPointerException();
			}
			this.data = data;
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof StringArrayWrapper)) {
				return false;
			}
			return Arrays.equals(data, ((StringArrayWrapper) other).data);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(data);
		}

		@Override
		public String toString() {
			return Arrays.toString(data);
		}

	}

	/**
	 * Zero-argument default constructor
	 * @throws IOException If there is some issue reading OpenNLP's files
	 * @throws AcotaConfigurationException Any exception that occurs while initializing 
	 * a Configuration object
	 */
	public TokenizerEnhancer() throws IOException, AcotaConfigurationException {
		super();
		this.auxiliar = new HashMap<StringArrayWrapper, Double>();
		loadConfiguration(configuration);
		LuceneEnhancer.provider = new ProviderTO("Tokenizer Enhancer");
	}
	
	/**
	 * One-argument constructor
	 * @param configuration acota-core's configuration class
	 * @throws IOException If there is some issue reading OpenNLP's files
	 * @throws AcotaConfigurationException Any exception that occurs while initializing 
	 * a Configuration object
	 */
	public TokenizerEnhancer(CoreConfiguration configuration) throws IOException, AcotaConfigurationException {
		super();
		this.auxiliar = new HashMap<StringArrayWrapper, Double>();
		loadConfiguration(configuration);
		LuceneEnhancer.provider = new ProviderTO("Tokenizer Enhancer");
	}

	/**
	 * @see Configurable#loadConfiguration(CoreConfiguration)
	 * @throws AcotaConfigurationException Any exception that occurs while initializing 
	 * a Configuration object
	 */
	@Override
	public void loadConfiguration(CoreConfiguration configuration) throws AcotaConfigurationException{
		if (configuration == null)
			configuration = new CoreConfiguration();
		this.configuration = configuration;
		this.k = configuration.getTokenizerK();
		this.tokenizerRelevanceLabel = configuration.getTokenizerLabelRelevance();
		this.tokenizerRelevanceTerm = configuration.getTokenizerTermRelevance();
		if(spanishTokenizerAnalyzer==null)
			this.spanishTokenizerAnalyzer = new SpanishTokenizerAnalyzer(configuration);
		else
			spanishTokenizerAnalyzer.loadConfiguration(configuration);
		if(englishTokenizerAnalyzer==null)
			this.englishTokenizerAnalyzer = new EnglishTokenizerAnalyzer(configuration);
		else
			this.englishTokenizerAnalyzer.loadConfiguration(configuration);
		this.languageUtil = LanguageDetector.getInstance(configuration);
	}

	/**
	 * @see EnhancerAdapter#execute()
	 */
	@Override
	protected void execute() throws Exception {
		extractLabelTerms();
		extractDescriptionTerms();
	}

	/**
	 * @see EnhancerAdapter#preExecute()
	 */
	@Override
	protected void preExecute() throws Exception {
		this.suggest = request.getSuggestions();
		this.tags = suggest.getTags();
		suggest.setResource(request.getResource());
	}

	/**
	 * @see EnhancerAdapter#postExecute()
	 */
	@Override
	protected void postExecute() throws Exception {
		logger.debug("Add providers to request");
		this.request.getTargetProviders().add(provider);
		logger.debug("Add suggestons to request");
		this.request.setSuggestions(suggest);
	}

	/**
	 * Extracts Label terms
	 * @throws IOException Signals that an I/O exception of some sort has occurred
	 * @throws AcotaConfigurationException Any exception that occurs while initializing 
	 * a Configuration object
	 */
	protected void extractLabelTerms() throws IOException, AcotaConfigurationException {
		extractTerms(LABEL, request.getResource().getLabel(),
				tokenizerRelevanceLabel);
	}
	
	/**
	 * Extracts Description terms
	 * @throws IOException Signals that an I/O exception of some sort has occurred
	 * @throws AcotaConfigurationException Any exception that occurs while initializing 
	 * a Configuration object
	 */
	protected void extractDescriptionTerms() throws IOException, AcotaConfigurationException {
		extractTerms(DESCIPTION, request.getResource().getDescription(),
				tokenizerRelevanceTerm);
	}

	/**
	 * Tokenizes and removes stop-words (Spanish and English) from the supplied text
	 * @param title FieldName (description, label)
	 * @param text Text to extract the terms
	 * @param relevance Weight which is incremented each matched term
	 * @throws IOException Signals that an I/O exception of some sort has occurred
	 * @throws AcotaConfigurationException Any exception that occurs while initializing 
	 * a Configuration object
	 */
	protected void extractTerms(String title, String text, double relevance)
			throws IOException, AcotaConfigurationException {
		String language = languageUtil.detect(text);
		this.currentTokenizerAnalyzer = loadAnalyzer(language);
		
		String[] sentences = currentTokenizerAnalyzer.sentDetect(text);
		auxiliar.clear();
		for (String sentence : sentences) {
			loadChunks(currentTokenizerAnalyzer.tokenize(sentence), relevance);
		}
		analysisOfTerms(relevance);

	}

	/**
	 * Generates the k-words saving them into a auxiliary map
	 * @param tokenizedText Tokenized Text
	 * @param relevance Weight which is incremented each matched term
	 * @throws AcotaConfigurationException Any exception that occurs 
	 */
	protected void loadChunks(String[] tokenizedText, double relevance) throws AcotaConfigurationException {
		for (int i = 1; i <= k; i++) {
			for (int j = 0; j + i <= tokenizedText.length; j++) {
				addString(
						cleanChunks(Arrays.copyOfRange(tokenizedText, j, i + j )),
						relevance);
			}
		}
	}

	/**
	 * Trims any word of the edges that not fits with:
	 * 	<ul>
	 * 		<li>Characters</li>
	 * 		<li>Numbers</li>
	 * 		<li>Length (of the word) bigger than 2</li>
	 * 	</ul>
	 * 
	 * @param chunk
	 * @return The supplied chunk, cleaned
	 * @throws AcotaConfigurationException Any exception that occurs 
	 */
	protected String[] cleanChunks(String[] chunk) throws AcotaConfigurationException {
		List<String> auxList = new LinkedList<String>();
		for (int i = 0; i < chunk.length; i++) {
			if (!currentTokenizerAnalyzer.match(chunk[i])) {
				if (!((i == 0 || i == chunk.length - 1) && chunk[i].length() <= 2))
					auxList.add(chunk[i]);
			}
		}
		String[] finalChunk = new String[auxList.size()];
		return auxList.toArray(finalChunk);

	}

	/**
	 * Adds or Increases (If currently exists) the label to the auxiliary labels map 
	 * @param label Label's name
	 * @param relevance Weight which is incremented each matched term
	 */
	protected void addString(String[] label, double relevance) {
		StringArrayWrapper stringArrayWraper = new StringArrayWrapper(label);
		Double value = auxiliar.get(stringArrayWraper);
		if (value == null)
			value = 0d;
		value += relevance;
		auxiliar.put(stringArrayWraper, value);
	}

	/**
	 * Performs a terms analysis
	 * @param relevance Weight which is incremented each matched term
	 * @throws IOException Signals that an I/O exception of some sort has occurred
	 * @throws AcotaConfigurationException Any exception that occurs 
	 */
	protected void analysisOfTerms(double relevance) throws IOException, AcotaConfigurationException {
		for (Entry<StringArrayWrapper, Double> value : auxiliar.entrySet()) {
			processSetence(currentTokenizerAnalyzer.tag(value.getKey().data),
					value.getKey().data, relevance);
		}
	}

	/**
	 * Trims the labels removing non meaningful words from the edges of the sentence
	 * @param tags OpenNLP Tags related to the Tokenized Text
	 * @param tokenizedText Tokenized Text
	 * @param relevance Weight which is incremented each matched term
	 * @throws AcotaConfigurationException Any exception that occurs 
	 */
	protected void processSetence(String[] tags, String[] tokenizedText,
			double relevance) throws AcotaConfigurationException {
		int min = calculateMin(tags);
		int max = calculateMax(tags);
		if (min <= max && min >= 0 && max >= 0) {
			String label = StringUtils.join(
					Arrays.copyOfRange(tokenizedText, min, max + 1), " ");
			TagTO tag = new TagTO(label,
					languageUtil.detect(label),
					provider, request.getResource());
			fillSuggestions(tag, relevance);
		}
	}

	/**
	 * Calculates the bigger valid index of the array
	 * @param tags Array of OpenNLP tags
	 * @return The bigger valid index of the array,
	 * -1 If there is no valid tags
	 * @throws AcotaConfigurationException Any exception that occurs 
	 */
	protected int calculateMax(String[] tags) throws AcotaConfigurationException {
		for (int i = tags.length - 1; i >= 0; i--) {
			if (currentTokenizerAnalyzer.containsTag(tags[i]))
				return i;
		}
		return -1;
	}

	/**
	 * Calculates the lower valid index of the array
	 * @param tags Array of OpenNLP tags
	 * @return The lower valid index of the array, 
	 * -1 If there is no valid tags
	 * @throws AcotaConfigurationException Any exception that occurs 
	 */
	protected int calculateMin(String[] tags) throws AcotaConfigurationException {
		for (int i = 0; i < tags.length; i++) {
			if (currentTokenizerAnalyzer.containsTag(tags[i]))
				return i;
		}
		return -1;
	}

	/**
	 * Loads a language analyzer (English or Spanish)
	 * @param language Language of the analyzer to load
	 * @return Tokenizer's {@link Analyzer}
	 * @throws AcotaConfigurationException Any exception that occurs while initializing 
	 * a Configuration object 
	 */
	protected TokenizerAnalyzer loadAnalyzer(String language) throws AcotaConfigurationException {
		TokenizerAnalyzer analyzer = null;
		if (language.equals(ISO_639_SPANISH)) {
			if(spanishTokenizerAnalyzer==null)
				this.spanishTokenizerAnalyzer = new SpanishTokenizerAnalyzer(configuration);
			analyzer = spanishTokenizerAnalyzer;
		} else {
			if(englishTokenizerAnalyzer==null)
				this.englishTokenizerAnalyzer = new EnglishTokenizerAnalyzer(configuration);
			analyzer = englishTokenizerAnalyzer;
		}
		return analyzer;
	}
	
}