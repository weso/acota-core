package es.weso.acota.core.business.enhancer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import es.weso.acota.core.business.enhancer.LuceneEnhancer;
import es.weso.acota.core.business.enhancer.WordnetEnhancer;
import es.weso.acota.core.entity.ProviderTO;
import es.weso.acota.core.entity.RequestSuggestionTO;
import es.weso.acota.core.entity.ResourceTO;
import es.weso.acota.core.entity.SuggestionTO;
import es.weso.acota.core.entity.TagTO;
import es.weso.acota.core.exceptions.AcotaConfigurationException;
import es.weso.acota.core.utils.lang.LanguageDetector;

public class WordnetEnhancerTest {
	
	private WordnetEnhancer wordnetEnhancer;
	
	@Before
	public void startTest() throws AcotaConfigurationException, IOException {
		this.wordnetEnhancer = new WordnetEnhancer();
	}
	
	@Test
	public void getProviderTest(){
		assertEquals(WordnetEnhancer.provider, wordnetEnhancer.getProvider());
	}
	
	@Test
	public void preExecuteEmptyRequestTest() throws Exception {
		ResourceTO resource = new ResourceTO();
		SuggestionTO suggestion = new SuggestionTO();

		RequestSuggestionTO request = mock(RequestSuggestionTO.class);
		when(request.getResource()).thenReturn(resource);
		when(request.getSuggestions()).thenReturn(suggestion);

		
		wordnetEnhancer.request = request;
		wordnetEnhancer.preExecute();
		
		assertEquals(resource, wordnetEnhancer.suggest.getResource());
	}
	
	@Test
	public void postExecuteTargetTermns() throws Exception{
		List<ProviderTO> providers = new LinkedList<ProviderTO>();
		
		RequestSuggestionTO request = mock(RequestSuggestionTO.class);
		when(request.getTargetProviders()).thenReturn(providers);
		
		wordnetEnhancer.request = request;
		
		wordnetEnhancer.postExecute();
		assertTrue(wordnetEnhancer.request.getTargetProviders().contains(WordnetEnhancer.provider)==true);
	}
	
	@Test
	public void postExecuteTargetProviders() throws Exception{
		SuggestionTO suggestion = new SuggestionTO();
		
		RequestSuggestionTO request = mock(RequestSuggestionTO.class);
		when(request.getSuggestions()).thenReturn(suggestion);
		
		wordnetEnhancer.request = request;
		
		wordnetEnhancer.postExecute();
		assertEquals(suggestion, wordnetEnhancer.request.getSuggestions());
	}
	
	
	@Test
	public void enhanceTest() throws Exception{
		SuggestionTO suggest = initializeSuggest();
		
		Map<String, TagTO> tags = new HashMap<String, TagTO>();
		TagTO tag = new TagTO("open", LanguageDetector.ISO_639_ENGLISH,
				LuceneEnhancer.provider, suggest.getResource());
		tag.setValue(4.0);
		tags.put(tag.getLabel(), tag);
		
		wordnetEnhancer.tags = tags;
		wordnetEnhancer.suggest = suggest;
		
		ResourceTO resource = new ResourceTO();
		resource.setDescription("Open");
		resource.setDescription("");
		
		SuggestionTO suggestion = new SuggestionTO();
		suggestion.setTags(tags);
		
		RequestSuggestionTO request = mock(RequestSuggestionTO.class);
		when(request.getResource()).thenReturn(resource);
		when(request.getSuggestions()).thenReturn(suggestion);
		
		wordnetEnhancer.enhance(request);
		assertEquals(3d, suggestion.getTags().get("clear").getValue(), 1e-15d);
	}
	
	@Test
	public void enhanceNonEnglishTest() throws Exception{
		SuggestionTO suggest = initializeSuggest();
		
		Map<String, TagTO> tags = new HashMap<String, TagTO>();
		TagTO tag = new TagTO("abierto", LanguageDetector.ISO_639_SPANISH,
				LuceneEnhancer.provider, suggest.getResource());
		tag.setValue(4.0);
		tags.put(tag.getLabel(), tag);
		
		wordnetEnhancer.tags = tags;
		wordnetEnhancer.suggest = suggest;
		
		ResourceTO resource = new ResourceTO();
		resource.setDescription("Abierto");
		resource.setDescription("");
		
		SuggestionTO suggestion = new SuggestionTO();
		suggestion.setTags(tags);
		
		RequestSuggestionTO request = mock(RequestSuggestionTO.class);
		when(request.getResource()).thenReturn(resource);
		when(request.getSuggestions()).thenReturn(suggestion);
		
		wordnetEnhancer.enhance(request);
		assertEquals(4.0, suggestion.getTags().get("abierto").getValue(),1e-15d);
	}
	
	private SuggestionTO initializeSuggest() throws Exception {
		SuggestionTO suggest = new SuggestionTO();
		ResourceTO resource = new ResourceTO();
		RequestSuggestionTO request = mock(RequestSuggestionTO.class);
		when(request.getResource()).thenReturn(resource);
		when(request.getSuggestions()).thenReturn(suggest);
		wordnetEnhancer.request = request;
		wordnetEnhancer.preExecute();
		return wordnetEnhancer.suggest;
	}
}
