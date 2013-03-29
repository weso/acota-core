package es.weso.acota.core.business.enhancer.analyzer.tokenizer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import opennlp.tools.postag.POSTagger;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.tokenize.Tokenizer;

import es.weso.acota.core.CoreConfiguration;
import es.weso.acota.core.exceptions.AcotaConfigurationException;

/**
 * This class adapts the interface TokenizerAnalyzer, implementing common
 * operations for derived classes
 * @author César Luis Alvargonzález
 *
 */
public abstract class TokenizerAnalyzerAdapter implements TokenizerAnalyzer{
	
	protected Pattern pattern;
	protected SentenceDetector sentenceDetector;
	protected Tokenizer tokenizer;
	protected POSTagger posTagger;
	protected Set<String> tokens;
	
	protected String tokenizerPattern;
	protected List<String> tokenizerTokens;
	protected String openNlpPosBin;
	protected String openNlpSentBin;
	protected String openNlpTokBin;
	
	private boolean isModified;
	
	/**
	 * @see es.weso.acota.core.business.enhancer.Configurable#loadConfiguration(CoreConfiguration)
	 */
	@Override
	public abstract void loadConfiguration(CoreConfiguration configuration) throws AcotaConfigurationException;
	
	/**
	 * @throws AcotaConfigurationException 
	 * @see TokenizerAnalyzer#match(java.lang.String)
	 */
	@Override
	public boolean match(String text) throws AcotaConfigurationException {
		lazyInitialization();
		return pattern.matcher(text).find();
	}
	
	/**
	 * @throws AcotaConfigurationException 
	 * @see TokenizerAnalyzer#containsTag(String)
	 */
	@Override
	public boolean containsTag(String tag) throws AcotaConfigurationException {
		lazyInitialization();
		return tokens.contains(tag);
	};
	
	/**
	 * @throws AcotaConfigurationException 
	 * @see TokenizerAnalyzer#tag(java.lang.String[])
	 */
	@Override
	public String[] tag(String[] text) throws AcotaConfigurationException  {
		lazyInitialization();
		return posTagger.tag(text);
	}

	/**
	 * @throws AcotaConfigurationException 
	 * @see TokenizerAnalyzer#tokenize(java.lang.String)
	 */
	@Override
	public String[] tokenize(String text) throws AcotaConfigurationException {
		lazyInitialization();
		return tokenizer.tokenize(text);
	}

	/**
	 * @throws AcotaConfigurationException 
	 * @see TokenizerAnalyzer#sentDetect(java.lang.String)
	 */
	@Override
	public String[] sentDetect(String text) throws AcotaConfigurationException {
		lazyInitialization();
		return sentenceDetector.sentDetect(text);
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
	
	protected void setTokenizerPattern(String value){
		if(tokenizerPattern==null || !tokenizerPattern.equals(value)){
			this.tokenizerPattern = value;
			this.isModified = true;
		}
	}
	
	protected void setTokenizerTokens(List<String> value){
		if(tokenizerTokens==null || !tokenizerTokens.equals(value)){
			this.tokenizerTokens = value;
			this.isModified = true;
		}
	}
	
	/**
	 * Lazy Load all OpenNLP Files required by the Analyzer
	 */
	protected void lazyInitialization() throws AcotaConfigurationException {
		if(isModified){
			lazyOpenNlpInitialization();
			this.pattern = Pattern.compile(tokenizerPattern);
			this.tokens = new HashSet<String>(tokenizerTokens);
		}
	}
	
	/**
	 * 
	 */
	protected abstract void lazyOpenNlpInitialization() throws AcotaConfigurationException;
}
