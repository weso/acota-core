package es.weso.acota.core.business.enhancer;

import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import es.weso.acota.core.entity.ProviderTO;
import es.weso.acota.core.entity.RequestSuggestionTO;
import es.weso.acota.core.entity.SuggestionTO;
import es.weso.acota.core.entity.TagTO;
import es.weso.acota.core.exceptions.AcotaModelException;

/**
 * Abstract class that implements {@link Enhacer} interface, it implements
 * the chain-of-responsibility design pattern
 * 
 * @author César Luis Alvargonzález
 */
public abstract class EnhancerAdapter implements Enhancer {

	protected static Logger logger;
	
	protected static ProviderTO provider;
	
	protected Map<String, TagTO> tags;
	
	protected RequestSuggestionTO request;
	protected SuggestionTO suggest;
	protected Enhancer successor;
	
	/**
	 * Zero-argument default constructor.
	 *
	 * @throws ConfigurationException Any exception that occurs while initializing a Configuration
	 *             object
	 */
	public EnhancerAdapter(){
		EnhancerAdapter.logger = Logger.getLogger(EnhancerAdapter.class);
	}
	
	/**
	 * @return The current {@link ProviderTO}
	 */
	public ProviderTO getProvider() {
		return provider;
	}
	
	@Override
	public SuggestionTO enhance(RequestSuggestionTO request) {
		try{
			this.request = request;
			preExecute();
			execute();
			postExecute();
			if(successor != null){
				return successor.enhance(this.request);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception enhancing request.", e);
			throw new AcotaModelException(e,"Exception enhancing request.");
		}
		return suggest;
	}

	@Override
	public void setSuccessor(Enhancer successor) {
		logger.debug("Set succesor of class");
		this.successor = successor;
	}	
	
	/**
	 * Returns the current {@link SuggestionTO}, if the {@link SuggestionTO} does not exist,
	 * it creates a new one and returns it.
	 * @return Current SuggestionTO
	 */
	public SuggestionTO getSuggest(){
		if(this.suggest==null){
			logger.debug("New instance singleton of suggestions");
			return (suggest = new SuggestionTO());
		}else{
			logger.debug("Get instance singleton of suggestions");
			return this.suggest;
		}
	}
	
	/**
	 * Executes the main task of the {@link Enhancer}
	 * @throws Exception Exception Any Exception that occurs during the main execution
	 */
	protected abstract void execute() throws Exception;
	
	/**
	 * Executes previous tasks to get the {@link Enhancer} ready to execute its main task.
	 * @throws Exception Exception Any Exception that occurs previous the execution
	 */
	protected abstract void preExecute() throws Exception;
	
	/**
	 * Cleans the house after the {@link Enhancer} execution
	 * @throws Exception Any Exception that occurs during the post execution
	 */
	protected abstract void postExecute() throws Exception;
	
	/**
	 * Adds some weight to a specific {@link TagTO}
	 * @param tags {@link TagTO}'s name
	 * @param weight Weight to add to the {@link TagTO}'s weight
	 */
	protected void fillSuggestions(TagTO tag, double weight) {
		TagTO current = tags.get(tag.getLabel());
		if(current==null)
			current = tag;
		current.addValue(weight);
		tags.put(current.getLabel(), current);
	}

}
