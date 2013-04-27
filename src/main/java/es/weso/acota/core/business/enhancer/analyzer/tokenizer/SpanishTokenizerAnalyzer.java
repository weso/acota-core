package es.weso.acota.core.business.enhancer.analyzer.tokenizer;

import java.io.IOException;

import opennlp.tools.lang.spanish.PosTagger;
import opennlp.tools.lang.spanish.SentenceDetector;
import opennlp.tools.lang.spanish.Tokenizer;

import es.weso.acota.core.CoreConfiguration;
import es.weso.acota.core.exceptions.AcotaConfigurationException;


/**
 * Analyzer specialized in perform Spanish auxiliary operations such as NLP (Natural 
 * Language Processing) operations for Spanish or perform regular expression matching.
 * @author César Luis Alvargonzález
 */
public class SpanishTokenizerAnalyzer extends TokenizerAnalyzerAdapter implements TokenizerAnalyzer{
	
	/**
	 * Default Constructor
	 * @param configuration Acota's Configuration Object
	 * @throws AcotaConfigurationException An exception that occurs 
	 * while installing and configuration Acota
	 */
	public SpanishTokenizerAnalyzer(CoreConfiguration configuration)
			throws AcotaConfigurationException {
		loadConfiguration(configuration);
	}
	
	/**
	 * @see es.weso.acota.core.business.enhancer.Configurable#loadConfiguration(CoreConfiguration)
	 */
	@Override
	public void loadConfiguration(CoreConfiguration configuration){
		setOpenNlpPosBin(configuration.getOpenNlpEsPosBin());
		setOpenNlpSentBin(configuration.getOpenNlpEsSentBin());
		setOpenNlpTokBin(configuration.getOpenNlpEsTokBin());
		setTokenizerPattern(configuration.getTokenizerEsPattern());
		setTokenizerTokens(configuration.getTokenizerEsTokens());
	}

	@Override
	protected void lazyOpenNlpInitialization() throws AcotaConfigurationException {
		try{
			this.sentenceDetector = new SentenceDetector(openNlpSentBin);
			this.posTagger = new PosTagger(openNlpPosBin);
			this.tokenizer = new Tokenizer(openNlpTokBin);
		} catch (IOException e) {
			throw new AcotaConfigurationException(e);
		}
		
	}
}