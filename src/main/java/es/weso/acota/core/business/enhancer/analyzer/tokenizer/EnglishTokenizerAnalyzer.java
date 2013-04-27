package es.weso.acota.core.business.enhancer.analyzer.tokenizer;

import java.io.IOException;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.lang.english.PosTagger;
import opennlp.tools.lang.english.SentenceDetector;
import opennlp.tools.lang.english.Tokenizer;

import es.weso.acota.core.CoreConfiguration;
import es.weso.acota.core.exceptions.AcotaConfigurationException;


/**
 * Analyzer specialized in perform English auxiliary operations such as NLP (Natural 
 * Language Processing) operations for Spanish or perform regular expression matching.
 * @author César Luis Alvargonzález
 */
public class EnglishTokenizerAnalyzer extends TokenizerAnalyzerAdapter implements TokenizerAnalyzer{
	
	/**
	 * Default Constructor
	 * @param configuration Acota's Configuration Object
	 * @throws AcotaConfigurationException An exception that occurs 
	 * while installing and configuration Acota
	 */
	public EnglishTokenizerAnalyzer(CoreConfiguration configuration)
			throws AcotaConfigurationException {
		loadConfiguration(configuration);
	}
	
	/**
	 * @see es.weso.acota.core.business.enhancer.Configurable#loadConfiguration(CoreConfiguration)
	 */
	@Override
	public void loadConfiguration(CoreConfiguration configuration){
		setOpenNlpPosBin(configuration.getOpenNlpEnPosBin());
		setOpenNlpSentBin(configuration.getOpenNlpEnSentBin());
		setOpenNlpTokBin(configuration.getOpenNlpEnTokBin());
		setTokenizerPattern(configuration.getTokenizerEnPattern());
		setTokenizerTokens(configuration.getTokenizerEnTokens());
	}

	@Override
	protected void lazyOpenNlpInitialization() throws AcotaConfigurationException {
		try{
			this.sentenceDetector = new SentenceDetector(openNlpSentBin);
			this.posTagger = new PosTagger(openNlpPosBin, new Dictionary());
			this.tokenizer = new Tokenizer(openNlpTokBin);
		} catch (IOException e) {
			throw new AcotaConfigurationException(e);
		}
		
	}

}