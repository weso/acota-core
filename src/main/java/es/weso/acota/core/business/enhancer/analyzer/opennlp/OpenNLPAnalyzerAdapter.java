package es.weso.acota.core.business.enhancer.analyzer.opennlp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.tools.postag.POSTagger;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.tokenize.Tokenizer;

import es.weso.acota.core.CoreConfiguration;
import es.weso.acota.core.exceptions.AcotaConfigurationException;

/**
 * This class adapts the interface OpenNLPAnalyzer, implementing common
 * operations for derived classes
 * @author César Luis Alvargonzález
 */
public abstract class OpenNLPAnalyzerAdapter implements OpenNLPAnalyzer{

	protected SentenceDetector sentenceDetector;
	protected Tokenizer tokenizer;
	protected POSTagger posTagger;
	
	protected Set<String> tokens;
	protected Set<String> nouns;
	protected Set<String> verbs;
	protected Set<String> numbers;
	
	protected String openNlpPosBin;
	protected String openNlpSentBin;
	protected String openNlpTokBin;
	protected List<String> openNlpTokens;
	protected List<String> openNlpNouns;
	protected List<String> openNlpVerbs;
	protected List<String> openNlpNumbers;
	
	private boolean isModified;
	
	/**
	 * @see es.weso.acota.core.business.enhancer.Configurable#loadConfiguration(CoreConfiguration)
	 */
	@Override
	public abstract void loadConfiguration(CoreConfiguration configuration) throws AcotaConfigurationException;

	/**
	 * @see OpenNLPAnalyzer#tag(java.lang.String[])
	 */
	@Override
	public String[] tag(String[] text) throws AcotaConfigurationException {
		lazyInitialization();
		return posTagger.tag(text);
	}

	/**
	 * @see OpenNLPAnalyzer#tokenize(java.lang.String)
	 */
	@Override
	public String[] tokenize(String text) throws AcotaConfigurationException {
		lazyInitialization();
		return tokenizer.tokenize(text);
	}

	/**
	 * @see OpenNLPAnalyzer#sentDetect(java.lang.String)
	 */
	@Override
	public String[] sentDetect(String text) throws AcotaConfigurationException {
		lazyInitialization();
		return sentenceDetector.sentDetect(text);
	}
	
	/**
	 * @see OpenNLPAnalyzer#isDispenasble(java.lang.String)
	 */
	@Override
	public boolean isDispenasble(String tag) throws AcotaConfigurationException {
		lazyInitialization();
		return tokens.contains(tag);
	}

	/**
	 * @see OpenNLPAnalyzer#isNoun(java.lang.String)
	 */
	@Override
	public boolean isNoun(String tag) throws AcotaConfigurationException {
		lazyInitialization();
		return nouns.contains(tag);
	}

	/**
	 * @see OpenNLPAnalyzer#isVerb(java.lang.String)
	 */
	@Override
	public boolean isVerb(String tag) throws AcotaConfigurationException {
		lazyInitialization();
		return verbs.contains(tag);
	}
	
	/**
	 * @see OpenNLPAnalyzer#isNumber(java.lang.String)
	 */
	@Override
	public boolean isNumber(String tag) throws AcotaConfigurationException {
		lazyInitialization();
		return numbers.contains(tag);
	}
	
	/**
	 * Sets the OpenNLP POS.bin path and the modified flag to true
	 * @param openNlpPosBin OpenNLP POS.bin path
	 */
	protected void setOpenNlpPosBin(String openNlpPosBin){
		if(this.openNlpPosBin==null || !this.openNlpPosBin.equals(openNlpPosBin)){
			this.openNlpPosBin = openNlpPosBin;
			this.isModified = true;
		}
	}
	
	/**
	 * Sets the OpenNLP SENT.bin path and the modified flag to true
	 * @param openNlpSentBin OpenNLP SENT.bin path
	 */
	protected void setOpenNlpSentBin(String openNlpSentBin){
		if(this.openNlpSentBin==null || !this.openNlpSentBin.equals(openNlpSentBin)){
			this.openNlpSentBin = openNlpSentBin;
			this.isModified = true;
		}
	}
	
	/**
	 * Sets the OpenNLP TOK.bin path and the modified flag to true
	 * @param openNlpTokBin OpenNLP TOK.bin path
	 */
	protected void setOpenNlpTokBin(String openNlpTokBin){
		if(this.openNlpTokBin==null || !this.openNlpTokBin.equals(openNlpTokBin)){
			this.openNlpTokBin = openNlpTokBin;
			this.isModified = true;
		}
	}
	
	/**
	 * Sets the OpenNLP Tokens and the modified flag to true
	 * @param openNlpTokens OpenNlp Tokens
	 */
	protected void setOpenNlpTokens(List<String> openNlpTokens){
		if(this.openNlpTokens==null || !this.openNlpTokens.equals(openNlpTokens)){
			this.openNlpTokens = openNlpTokens;
			this.isModified = true;
		}
	}
	
	/**
	 * Sets the OpenNLP Nouns and the modified flag to true
	 * @param openNlpNouns OpenNlp Nouns
	 */
	protected void setOpenNlpNouns(List<String> openNlpNouns){
		if(this.openNlpNouns==null || !this.openNlpNouns.equals(openNlpNouns)){
			this.openNlpNouns = openNlpNouns;
			this.isModified = true;
		}
	}
	
	/**
	 * Sets the OpenNLP Verbs and the modified flag to true
	 * @param openNlpVerbs OpenNlp Verbs
	 */
	protected void setOpenNlpVerbs(List<String> openNlpVerbs){
		if(this.openNlpVerbs==null || !this.openNlpVerbs.equals(openNlpVerbs)){
			this.openNlpVerbs = openNlpVerbs;
			this.isModified = true;
		}
	}
	
	/**
	 * Sets the OpenNLP Numbers and the modified flag to true
	 * @param openNlpNumbers OpenNlp Numbers
	 */
	protected void setOpenNlpNumbers(List<String> openNlpNumbers){
		if(this.openNlpNumbers==null || !this.openNlpNumbers.equals(openNlpNumbers)){
			this.openNlpNumbers = openNlpNumbers;
			this.isModified = true;
		}
	}
	
	/**
	 * Lazy Load the configuration required by the Analyzer
	 */
	protected final void lazyInitialization() throws AcotaConfigurationException {
		if(isModified){
			lazyOpenNlpInitialization();
			this.tokens = new HashSet<String>(openNlpTokens);
			this.nouns = new HashSet<String>(openNlpNouns);
			this.verbs = new HashSet<String>(openNlpVerbs);
			this.numbers = new HashSet<String>(openNlpNumbers);
		}
	}
	
	/**
	 * Loads OpenNLP required files
	 */
	protected abstract void lazyOpenNlpInitialization() throws AcotaConfigurationException;
}
