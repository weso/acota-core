package es.weso.acota.core.business.enhancer;

import es.weso.acota.core.CoreConfiguration;
import es.weso.acota.core.exceptions.AcotaConfigurationException;

/**
 * Configurable Interface, this interface allows configure the classes that
 * implements this interface.
 * 
 * @author César Luis Alvargonzález
 * 
 */
public interface Configurable {
	/**
	 * Loads the configuration into the class that implements this interface
	 * @param configuration acota-core's configuration class
	 * @throws AcotaConfigurationException Any exception that occurs while 
	 * initializing a Acota's Configuration object
	 */
	public void loadConfiguration(CoreConfiguration configuration) throws AcotaConfigurationException;
}
