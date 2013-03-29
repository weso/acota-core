package es.weso.acota.core.business.enhancer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import es.weso.acota.core.business.enhancer.GoogleEnhancer;
import es.weso.acota.core.entity.RequestSuggestionTO;
import es.weso.acota.core.entity.ResourceTO;
import es.weso.acota.core.entity.SuggestionTO;
import es.weso.acota.core.entity.TagTO;
import es.weso.acota.core.exceptions.AcotaConfigurationException;

public class GoogleEnhancerTest {
protected GoogleEnhancer googleEnhancer;
	
	@Before
	public void setUp() throws AcotaConfigurationException {
		this.googleEnhancer = new GoogleEnhancer();
	}
	
	@Test
	public void getSuggestEmpty(){
		assertEquals(new SuggestionTO(),googleEnhancer.getSuggest());
	}
	
	@Test
	public void getSuggestTest(){
		SuggestionTO s = new SuggestionTO(Collections.<String, TagTO> emptyMap(), new ResourceTO());
		googleEnhancer.suggest = s;
		assertEquals(s,googleEnhancer.getSuggest());
	}
	
	@Test
	public void setAdapterTest() throws AcotaConfigurationException {
		GoogleEnhancer successor = new GoogleEnhancer();
		googleEnhancer.setSuccessor(successor);
		assertEquals(successor,googleEnhancer.successor);
	}
	
	@Test
	public void getProviderTest(){
		assertEquals(GoogleEnhancer.provider, googleEnhancer.getProvider());
	}

	@Test
	public void preExecuteEmptyRequestTest() throws Exception {
		ResourceTO resource = new ResourceTO();
		SuggestionTO suggestion = new SuggestionTO();

		RequestSuggestionTO request = mock(RequestSuggestionTO.class);
		when(request.getResource()).thenReturn(resource);
		when(request.getSuggestions()).thenReturn(suggestion);

		
		googleEnhancer.request = request;
		googleEnhancer.preExecute();
		
		assertEquals(resource, googleEnhancer.suggest.getResource());
	}
}
