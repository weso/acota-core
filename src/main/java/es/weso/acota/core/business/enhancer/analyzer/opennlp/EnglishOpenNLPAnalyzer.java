package es.weso.acota.core.business.enhancer.analyzer.opennlp;

import java.io.IOException;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.lang.english.PosTagger;
import opennlp.tools.lang.english.SentenceDetector;
import opennlp.tools.lang.english.Tokenizer;
import es.weso.acota.core.CoreConfiguration;
import es.weso.acota.core.exceptions.AcotaConfigurationException;

/**
 * Analyzer specialized in perform NLP (Natural Language Processing)
 * operations for English text
 * @author César Luis Alvargonzález
 */
public class EnglishOpenNLPAnalyzer extends OpenNLPAnalyzerAdapter implements OpenNLPAnalyzer{
	
	/**
	 * Default Constructor
	 * @param configuration Acota's Configuration Object
	 * @throws AcotaConfigurationException An exception that occurs 
	 * while installing and configuration Acota
	 */
	public EnglishOpenNLPAnalyzer(CoreConfiguration configuration) throws AcotaConfigurationException {
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
		setOpenNlpTokens(configuration.getOpenNlpEnTokens());
		setOpenNlpNouns(configuration.getOpenNlpEnNouns());
		setOpenNlpVerbs(configuration.getOpenNlpEnVerbs());
		setOpenNlpAdjectives(configuration.getOpenNlpEnAdjectives());
	}

	/**
	 * @see es.weso.acota.core.business.enhancer.analyzer.opennlp.OpenNLPAnalyzerAdapter#lazyOpenNlpInitialization()
	 */
	@Override
	protected void lazyOpenNlpInitialization() throws AcotaConfigurationException {
		try{
			this.sentenceDetector = new SentenceDetector(openNlpSentBin);
			this.posTagger = new PosTagger(openNlpPosBin,new Dictionary());
			this.tokenizer = new Tokenizer(openNlpTokBin);
		} catch (IOException e) {
			throw new AcotaConfigurationException(e);
		}
		
	}
}
