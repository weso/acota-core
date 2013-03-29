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
	
	protected void setOpenNlpPosBin(String value){
		if(openNlpPosBin==null || !openNlpPosBin.equals(value)){
			this.openNlpPosBin = value;
			this.isModified = true;
		}
	}
	
	protected void setOpenNlpSentBin(String value){
		if(openNlpSentBin==null || !openNlpSentBin.equals(value)){
			this.openNlpSentBin = value;
			this.isModified = true;
		}
	}
	
	protected void setOpenNlpTokBin(String value){
		if(openNlpTokBin==null || !openNlpTokBin.equals(value)){
			this.openNlpTokBin = value;
			this.isModified = true;
		}
	}
	
	protected void setOpenNlpTokens(List<String> value){
		if(openNlpTokens==null || !openNlpTokens.equals(value)){
			this.openNlpTokens = value;
			this.isModified = true;
		}
	}
	
	protected void setOpenNlpNouns(List<String> value){
		if(openNlpNouns==null || !openNlpNouns.equals(value)){
			this.openNlpNouns = value;
			this.isModified = true;
		}
	}
	
	protected void setOpenNlpVerbs(List<String> value){
		if(openNlpVerbs==null || !openNlpVerbs.equals(value)){
			this.openNlpVerbs = value;
			this.isModified = true;
		}
	}
	
	protected void setOpenNlpNumbers(List<String> value){
		if(openNlpNumbers==null || !openNlpNumbers.equals(value)){
			this.openNlpNumbers = value;
			this.isModified = true;
		}
	}
	
	/**
	 * Lazy Load all OpenNLP Files required by the Analyzer
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
	 * 
	 */
	protected abstract void lazyOpenNlpInitialization() throws AcotaConfigurationException;
}
