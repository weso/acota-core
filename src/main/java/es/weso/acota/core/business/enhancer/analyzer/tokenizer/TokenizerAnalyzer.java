package es.weso.acota.core.business.enhancer.analyzer.tokenizer;

import es.weso.acota.core.business.enhancer.Configurable;
import es.weso.acota.core.exceptions.AcotaConfigurationException;

/**
 * Analyzer interface, a class that implements OpenNLPAnalyzer
 * is ready to work as analyzer of TokenizerEnhancer
 * @author César Luis Alvargonzález
 */
public interface TokenizerAnalyzer extends Configurable{
	
	/**
	 * Check whether the text matches against the regular expression
	 * @param text The text to be matched
	 * @return true if the text matches
	 * @return false if the text does not match
	 */
	public boolean match(String text) throws AcotaConfigurationException;
	
	/**
	 * Check whether the tag matches against the tag set
	 * @param tag Tag to be matched
	 * @return true if the tag matches
	 * @return false if the tag does not match
	 */
	public boolean containsTag(String tag) throws AcotaConfigurationException;
	
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
	
}
