package es.weso.acota.core.business.enhancer.analyzer.opennlp;

import java.io.IOException;

import opennlp.tools.lang.spanish.PosTagger;
import opennlp.tools.lang.spanish.SentenceDetector;
import opennlp.tools.lang.spanish.Tokenizer;
import es.weso.acota.core.CoreConfiguration;
import es.weso.acota.core.exceptions.AcotaConfigurationException;

/**
 * Analyzer specialized in perform NLP (Natural Language Processing)
 * operations for Spanish text
 * @author César Luis Alvargonzález
 */
public class SpanishOpenNLPAnalyzer extends OpenNLPAnalyzerAdapter implements
		OpenNLPAnalyzer {

	/**
	 * Default Constructor
	 * @param configuration Acota's Configuration Object
	 * @throws AcotaConfigurationException An exception that occurs 
	 * while installing and configuration Acota
	 */
	public SpanishOpenNLPAnalyzer(CoreConfiguration configuration)
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
		setOpenNlpTokens(configuration.getOpenNlpEsTokens());
		setOpenNlpNouns(configuration.getOpenNlpEsNouns());
		setOpenNlpVerbs(configuration.getOpenNlpEsVerbs());
		setOpenNlpAdjectives(configuration.getOpenNlpEsAdjectives());
	}

	/**
	 * @see es.weso.acota.core.business.enhancer.analyzer.opennlp.OpenNLPAnalyzerAdapter#lazyOpenNlpInitialization()
	 */
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
