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
	
	private boolean modified;
	
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
	
	/**
	 * Sets the OpenNLP POS.bin path and the modified flag to true
	 * @param openNlpPosBin OpenNLP POS.bin path
	 */
	protected void setOpenNlpPosBin(String openNlpPosBin){
		if(this.openNlpPosBin==null || !this.openNlpPosBin.equals(openNlpPosBin)){
			this.openNlpPosBin = openNlpPosBin;
			this.modified = true;
		}
	}
	
	/**
	 * Sets the OpenNLP SENT.bin path and the modified flag to true
	 * @param openNlpSentBin OpenNLP SENT.bin path
	 */
	protected void setOpenNlpSentBin(String openNlpSentBin){
		if(this.openNlpSentBin==null || !this.openNlpSentBin.equals(openNlpSentBin)){
			this.openNlpSentBin = openNlpSentBin;
			this.modified = true;
		}
	}
	
	/**
	 * Sets the OpenNLP TOK.bin path and the modified flag to true
	 * @param openNlpTokBin OpenNLP TOK.bin path
	 */
	protected void setOpenNlpTokBin(String openNlpTokBin){
		if(this.openNlpTokBin==null || !this.openNlpTokBin.equals(openNlpTokBin)){
			this.openNlpTokBin = openNlpTokBin;
			this.modified = true;
		}
	}
	
	/**
	 * Sets the Tokenizer Patter (RegEx.) and the modified flag to true
	 * @param tokenizerPattern Tokenizer Pattern 
	 */
	protected void setTokenizerPattern(String tokenizerPattern){
		if(this.tokenizerPattern==null || !this.tokenizerPattern.equals(tokenizerPattern)){
			this.tokenizerPattern = tokenizerPattern;
			this.modified = true;
		}
	}
	
	/**
	 * Sets the Tokenizer Tokens and the modified flag to true
	 * @param tokenizerTokens Tokenizer Tokens
	 */
	protected void setTokenizerTokens(List<String> tokenizerTokens){
		if(this.tokenizerTokens==null || !this.tokenizerTokens.equals(tokenizerTokens)){
			this.tokenizerTokens = tokenizerTokens;
			this.modified = true;
		}
	}
	
	/**
	 * Lazy Load the configuration required by the Analyzer
	 * @throws AcotaConfigurationException Any exception that occurs 
	 */
	protected void lazyInitialization() throws AcotaConfigurationException {
		if(modified){
			lazyOpenNlpInitialization();
			this.pattern = Pattern.compile(tokenizerPattern);
			this.tokens = new HashSet<String>(tokenizerTokens);
			this.modified = false;
		}
	}
	
	/**
	 * Lazy Load all OpenNLP Files required by the Analyzer
	 * @throws AcotaConfigurationException Any exception that occurs
	 */
	protected abstract void lazyOpenNlpInitialization() throws AcotaConfigurationException;
}
