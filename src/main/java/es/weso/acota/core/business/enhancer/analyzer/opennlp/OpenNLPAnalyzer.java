package es.weso.acota.core.business.enhancer.analyzer.opennlp;

import es.weso.acota.core.business.enhancer.Configurable;
import es.weso.acota.core.exceptions.AcotaConfigurationException;

/**
 * Analyzer interface, a class that implements OpenNLPAnalyzer
 * is ready to perform natural language processing (NLP) operations
 * @author César Luis Alvargonzález
 */
public interface OpenNLPAnalyzer extends Configurable{
	/**
	 * Tags a text with Morphosyntactic Tags
	 * @param text The string to be tagged.
	 * @return Split The String[] with the individual tags
	 */
	String[] tag(String[] text) throws AcotaConfigurationException;
	/**
	 * Splits a string into its atomic parts
	 * @param text The string to be tokenized.
	 * @return The String[] with the individual tokens as the array elements
	 */
	String[] tokenize(String text) throws AcotaConfigurationException;
	/**
	 * Splits a string into sentences
	 * @param text The string to be extracted
	 * @return The String[] with the detected sentences
	 */
	String[] sentDetect(String text) throws AcotaConfigurationException;
	
	/**
	 * Checks whether the supplied tag is a dispensable tag
	 * @param tag OpenNLP Tag to check
	 * @return true If the supplied tag is a dispensable tag
	 * @return false If the supplied tag is not a dispensable tag
	 */
	boolean isDispenasble(String tag) throws AcotaConfigurationException;
	
	/**
	 * Checks whether the supplied tag is a noun tag
	 * @param tag OpenNLP Tag to check
	 * @return true If the supplied tag is a noun tag
	 * @return false If the supplied tag is not a noun tag
	 */
	boolean isNoun(String tag) throws AcotaConfigurationException;
	
	/**
	 * Checks whether the supplied tag is a number tag
	 * @param tag OpenNLP Tag to check
	 * @return true If the supplied tag is a number tag
	 * @return false If the supplied tag is not a number tag
	 */
	boolean isNumber(String tag) throws AcotaConfigurationException;
	
	/**
	 * Checks whether the supplied tag is a verb tag
	 * @param tag OpenNLP Tag to check
	 * @return true If the supplied tag is a verb tag
	 * @return false If the supplied tag is not a verb tag
	 */
	boolean isVerb(String tag) throws AcotaConfigurationException;
}
