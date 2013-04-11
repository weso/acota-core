package es.weso.acota.core.business.enhancer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import es.weso.acota.core.business.enhancer.LuceneEnhancer;
import es.weso.acota.core.business.enhancer.OpenNLPEnhancer;
import es.weso.acota.core.business.enhancer.analyzer.opennlp.SpanishOpenNLPAnalyzer;
import es.weso.acota.core.entity.ProviderTO;
import es.weso.acota.core.entity.RequestSuggestionTO;
import es.weso.acota.core.entity.ResourceTO;
import es.weso.acota.core.entity.SuggestionTO;
import es.weso.acota.core.entity.TagTO;
import es.weso.acota.core.exceptions.AcotaConfigurationException;

public class OpenNLPEnhancerTest {
	
	protected OpenNLPEnhancer openNLPEnhancer;
	
	@Before
	public void setUp() throws AcotaConfigurationException{
		this.openNLPEnhancer = new OpenNLPEnhancer();
	}
	
	@Test
	public void getProviderTest(){
		assertEquals(OpenNLPEnhancer.provider, openNLPEnhancer.getProvider());
	}
	
	@Test
	public void OpenNLPEConstructorPronouns(){
		assertEquals(Collections.EMPTY_SET, openNLPEnhancer.nouns);
	}
	
	@Test
	public void OpenNLPEConstructorVerbs(){
		assertEquals(Collections.EMPTY_SET, openNLPEnhancer.verbs);
	}

	@Test
	public void OpenNLPEConstructorNumbers(){
		assertEquals(Collections.EMPTY_SET, openNLPEnhancer.numbers);
	}
	
	@Test
	public void preExecuteEmptyRequestTest() throws Exception {
		ResourceTO resource = new ResourceTO();
		SuggestionTO suggestion = new SuggestionTO();

		RequestSuggestionTO request = mock(RequestSuggestionTO.class);
		when(request.getResource()).thenReturn(resource);
		when(request.getSuggestions()).thenReturn(suggestion);

		
		openNLPEnhancer.request = request;
		openNLPEnhancer.preExecute();
		
		assertEquals(resource, openNLPEnhancer.suggest.getResource());
	}
	
	@Test
	public void postExecuteTargetTermns() throws Exception{
		List<ProviderTO> providers = new LinkedList<ProviderTO>();
		
		RequestSuggestionTO request = mock(RequestSuggestionTO.class);
		when(request.getTargetProviders()).thenReturn(providers);
		
		openNLPEnhancer.request = request;
		
		openNLPEnhancer.postExecute();
		assertTrue(openNLPEnhancer.request.getTargetProviders().contains(OpenNLPEnhancer.provider)==true);
	}
	
	@Test
	public void postExecuteTargetProviders() throws Exception{
		SuggestionTO suggestion = new SuggestionTO();
		
		RequestSuggestionTO request = mock(RequestSuggestionTO.class);
		when(request.getSuggestions()).thenReturn(suggestion);
		
		openNLPEnhancer.request = request;
		
		openNLPEnhancer.postExecute();
		assertEquals(suggestion, openNLPEnhancer.request.getSuggestions());
	}
	
	@Test
	public void enhanceTest() throws Exception{
		SuggestionTO suggest = initializeSuggest();
		
		Map<String, TagTO> tags = new HashMap<String, TagTO>();
		TagTO tag = new TagTO("prueba", LuceneEnhancer.provider,
				suggest.getResource());
		tag.setValue(4.0d);
		tags.put(tag.getLabel(), tag);
		
		openNLPEnhancer.tags = tags;
		openNLPEnhancer.suggest = suggest;
		
		suggest.setTags(tags);
		
		ResourceTO resource = new ResourceTO();
		resource.setDescription("Hola esto es un contenido de prueba");
		resource.setLabel("Mi nombre es roberto");
		
		
		RequestSuggestionTO request = mock(RequestSuggestionTO.class);
		when(request.getResource()).thenReturn(resource);
		when(request.getSuggestions()).thenReturn(suggest);
		
		openNLPEnhancer.enhance(request);
		
		assertEquals(6.0d, suggest.getTags().get("prueba").getValue(),1e-15);
	}
	
	@Test
	public void processSentenceEmpty() throws Exception{
		SuggestionTO suggest = initializeSuggest();
		
		Map<String, TagTO> tags = new HashMap<String, TagTO>();
		TagTO tag1 = new TagTO("eso", LuceneEnhancer.provider,suggest.getResource());
		tag1.setValue(2.0);
		TagTO tag2 = new TagTO("tu", LuceneEnhancer.provider,suggest.getResource());
		tag2.setValue(2.0);
		TagTO tag3 = new TagTO("comer", LuceneEnhancer.provider,suggest.getResource());
		tag3.setValue(2.0);
		TagTO tag4 = new TagTO("uno", LuceneEnhancer.provider,suggest.getResource());
		tag4.setValue(2.0);
		
		tags.put(tag1.getLabel(), tag1);
		tags.put(tag2.getLabel(), tag2);
		tags.put(tag3.getLabel(), tag3);
		tags.put(tag4.getLabel(), tag4);
		
		openNLPEnhancer.tags = tags;
		openNLPEnhancer.suggest = suggest;
		
		suggest.setTags(tags);
		
		openNLPEnhancer.processSetence(new String[]{}, new String[]{});
		
		assertEquals(2.0d, tags.get("eso").getValue(), 1e-15d);
		assertEquals(2.0d, tags.get("tu").getValue(), 1e-15d);
		assertEquals(2.0d, tags.get("comer").getValue(), 1e-15d);
		assertEquals(2.0d, tags.get("eso").getValue(), 1e-15d);
	}
	
	@Test
	public void processSentenceDoNothing() throws Exception{
		SuggestionTO suggest = initializeSuggest();
		
		Map<String, TagTO> tags = new HashMap<String, TagTO>();
		TagTO tag = new TagTO("perro", LuceneEnhancer.provider,
				suggest.getResource());
		tag.setValue(2.0);
		tags.put(tag.getLabel(), tag);
		
		openNLPEnhancer.tags = tags;
		openNLPEnhancer.suggest = suggest;
		
		openNLPEnhancer.openNlpAnalyzer = new SpanishOpenNLPAnalyzer(openNLPEnhancer.configuration);
		
		openNLPEnhancer.processSetence(new String[]{"T"}, new String[]{"perro"});
		
		assertEquals(2d, openNLPEnhancer.tags.get("perro").getValue(), 1e-15d);
	}
	
	@Test
	public void processSentenceRemoveAll() throws Exception{
		SuggestionTO suggest = initializeSuggest();
		
		Map<String, TagTO> tags = new HashMap<String, TagTO>();
		TagTO tag1 = new TagTO("eso", LuceneEnhancer.provider,suggest.getResource());
		tag1.setValue(2.0);
		TagTO tag2 = new TagTO("perro", LuceneEnhancer.provider,suggest.getResource());
		tag2.setValue(2.0);
		TagTO tag3 = new TagTO("comer", LuceneEnhancer.provider,suggest.getResource());
		tag3.setValue(2.0);
		TagTO tag4 = new TagTO("uno", LuceneEnhancer.provider,suggest.getResource());
		tag4.setValue(2.0);
		
		tags.put(tag1.getLabel(), tag1);
		tags.put(tag2.getLabel(), tag2);
		tags.put(tag3.getLabel(), tag3);
		tags.put(tag4.getLabel(), tag4);
		
		openNLPEnhancer.tags = tags;
		openNLPEnhancer.suggest = suggest;
		
		suggest.setTags(tags);
		
		openNLPEnhancer.openNlpAnalyzer = new SpanishOpenNLPAnalyzer(openNLPEnhancer.configuration);
		openNLPEnhancer.processSetence(new String[]{"PN","NC","VS","Z"}, new String[]{"eso","perro","comer","uno"});
		
		assertEquals(openNLPEnhancer.tags.get("eso"),null);
		assertTrue(openNLPEnhancer.nouns.contains("perro"));
		assertTrue(openNLPEnhancer.verbs.contains("comer"));
		assertTrue(openNLPEnhancer.numbers.contains("uno"));
	}
	
	@Test 
	public void calculateMaxValueEmptyTest(){
		Map<String, TagTO> tags = new HashMap<String, TagTO>();
		
		openNLPEnhancer.tags = tags;
		assertEquals(0d, openNLPEnhancer.calculateMaxValue(), 1e-15d);
	}
	
	@Test 
	public void findAndremoveNotMatch() throws Exception{
		SuggestionTO suggest = initializeSuggest();
		
		Map<String, TagTO> tags = new HashMap<String, TagTO>();
		TagTO tag = new TagTO("esos", LuceneEnhancer.provider,
				suggest.getResource());
		tag.setValue(1.0);
		tags.put(tag.getLabel(), tag);
		
		openNLPEnhancer.tags = tags;
		openNLPEnhancer.suggest = suggest;
		
		openNLPEnhancer.findAndRemove("eso");
		
		assertTrue(null == openNLPEnhancer.tags.get("eso"));
	}
	
	@Test 
	public void findAndremoveTest() throws Exception{
		SuggestionTO suggest = initializeSuggest();
		
		Map<String, TagTO> tags = new HashMap<String, TagTO>();
		TagTO tag = new TagTO("eso", LuceneEnhancer.provider,
				suggest.getResource());
		tag.setValue(1.0);
		tags.put(tag.getLabel(), tag);
		
		openNLPEnhancer.tags = tags;
		openNLPEnhancer.suggest = suggest;
		
		openNLPEnhancer.findAndRemove("eso");
		
		assertTrue(null == openNLPEnhancer.tags.get("eso"));
	}
	
	@Test
	public void findAndChangeNounsEmpty() throws Exception{
		SuggestionTO suggest = initializeSuggest();
		
		Map<String, TagTO> tags = new HashMap<String, TagTO>();
		TagTO tag = new TagTO("yo", LuceneEnhancer.provider,
				suggest.getResource());
		tag.setValue(1.0);
		tags.put(tag.getLabel(), tag);
		
		openNLPEnhancer.tags = tags;
		openNLPEnhancer.suggest = suggest;
		
		openNLPEnhancer.findAndChangeNouns();
		
		assertEquals(1d, openNLPEnhancer.tags.get("yo").getValue(), 1e-15d);
	}
	
	@Test
	public void findAndChangeNounsTest() throws Exception{
		SuggestionTO suggest = initializeSuggest();
		
		Map<String, TagTO> tags = new HashMap<String, TagTO>();
		TagTO tag = new TagTO("yo", LuceneEnhancer.provider,
				suggest.getResource());
		tag.setValue(1.0);
		tags.put(tag.getLabel(), tag);
		
		openNLPEnhancer.tags = tags;
		openNLPEnhancer.suggest = suggest;
		
		openNLPEnhancer.nouns = new HashSet<String>();
		openNLPEnhancer.nouns.add("yo");
		
		openNLPEnhancer.findAndChangeNouns();
		assertEquals(1.5d, openNLPEnhancer.tags.get("yo").getValue(), 1e-15d);
	}
	
	@Test
	public void findAndChangeVerbsEmpty() throws Exception{
		SuggestionTO suggest = initializeSuggest();
		
		Map<String, TagTO> tags = new HashMap<String, TagTO>();
		TagTO tag = new TagTO("comer", LuceneEnhancer.provider,
				suggest.getResource());
		tag.setValue(2.0);
		tags.put(tag.getLabel(), tag);
		
		openNLPEnhancer.tags = tags;
		openNLPEnhancer.suggest = suggest;
		
		openNLPEnhancer.findAndChangeVerbs();
		
		assertEquals(2d, openNLPEnhancer.tags.get("comer").getValue(), 1e-15d);
	}
	
	@Test
	public void findAndChangeVerbsTest() throws Exception{
		SuggestionTO suggest = initializeSuggest();
		
		Map<String, TagTO> tags = new HashMap<String, TagTO>();
		TagTO tag = new TagTO("comer", LuceneEnhancer.provider,
				suggest.getResource());
		tag.setValue(2.0);
		tags.put(tag.getLabel(), tag);
		
		openNLPEnhancer.tags = tags;
		openNLPEnhancer.suggest = suggest;
		
		openNLPEnhancer.verbs = new HashSet<String>();
		openNLPEnhancer.verbs.add("comer");
		
		openNLPEnhancer.findAndChangeVerbs();
		
		assertEquals(1d, openNLPEnhancer.tags.get("comer").getValue(), 1e-15d);
	}
	
	@Test
	public void findAndChangeNumbersEmpty() throws Exception{
		SuggestionTO suggest = initializeSuggest();
		
		Map<String, TagTO> tags = new HashMap<String, TagTO>();
		TagTO tag = new TagTO("uno", LuceneEnhancer.provider,
				suggest.getResource());
		tag.setValue(1.0);
		tags.put(tag.getLabel(), tag);
		
		openNLPEnhancer.tags = tags;
		openNLPEnhancer.suggest = suggest;
		
		openNLPEnhancer.findAndChangeNumbers();
		
		assertEquals(1d, openNLPEnhancer.tags.get("uno").getValue(), 1e-15d);
	}
	
	@Test
	public void findAndChangeNumbersTest() throws Exception{
		SuggestionTO suggest = initializeSuggest();
		
		Map<String, TagTO> tags = new HashMap<String, TagTO>();
		TagTO tag = new TagTO("uno", LuceneEnhancer.provider,
				suggest.getResource());
		tag.setValue(1.0);
		tags.put(tag.getLabel(), tag);
		
		openNLPEnhancer.tags = tags;
		openNLPEnhancer.suggest = suggest;
		
		openNLPEnhancer.numbers = new HashSet<String>();
		openNLPEnhancer.numbers.add("uno");
		
		openNLPEnhancer.findAndChangeNumbers();
		
		assertEquals(0d, openNLPEnhancer.tags.get("uno").getValue(), 1e-15d);
	}
	
	private SuggestionTO initializeSuggest() throws Exception {
		SuggestionTO suggest = new SuggestionTO();
		ResourceTO resource = new ResourceTO();
		RequestSuggestionTO request = mock(RequestSuggestionTO.class);
		when(request.getResource()).thenReturn(resource);
		when(request.getSuggestions()).thenReturn(suggest);
		openNLPEnhancer.request = request;
		openNLPEnhancer.preExecute();
		return openNLPEnhancer.suggest;
	}
	
}
