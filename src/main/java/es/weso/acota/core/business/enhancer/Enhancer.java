package es.weso.acota.core.business.enhancer;

import es.weso.acota.core.entity.RequestSuggestionTO;
import es.weso.acota.core.entity.SuggestionTO;

/**
 * Enhancer Interface, implements Chain of Responsibility design pattern
 * 
 * @author César Luis Alvargonzález
 */
public interface Enhancer {

	/**
	 * @param successor The next Enhancer to be executed
	 */
	public void setSuccessor(Enhancer successor);
	
	/**
	 * Enhance the results using several techniques
	 * @param request {@link RequestSuggestionTO} object
	 * @return The results of the Enhancement
	 */
	public SuggestionTO enhance(RequestSuggestionTO request);
	
}
