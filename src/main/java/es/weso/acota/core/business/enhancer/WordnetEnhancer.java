package es.weso.acota.core.business.enhancer;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;


import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import es.weso.acota.core.CoreConfiguration;
import es.weso.acota.core.entity.ProviderTO;
import es.weso.acota.core.entity.TagTO;
import es.weso.acota.core.exceptions.AcotaConfigurationException;

/**
 * WordnetEnhancer is an {@link Enhancer} specialized in increasing the weight 
 * of the terms that match with the synonyms of the founded terms, if the term
 * was already in the {@link TagTO}'s Map, or adds the term to
 * the {@link TagTO}'s Map.
 * 
 * @author César Luis Alvargonzález
 */
public class WordnetEnhancer extends EnhancerAdapter implements Configurable {

	protected String wordnetEnDict;
	protected boolean wordnetEnDictModified;
	protected double wordnetRelevance;
	
	protected IDictionary dictionary;
	
	protected CoreConfiguration configuration;
	
	/**
	 * Zero-argument default constructor
	 * @throws IOException Any exception that occurs while initializing Wordnet's dictionary
	 * @throws AcotaConfigurationException Any exception that occurs while initializing 
	 * a Configuration object
	 */
	public WordnetEnhancer() throws IOException, AcotaConfigurationException  {
		super();
		WordnetEnhancer.provider = new ProviderTO("Wordnet Enhancer");
		loadConfiguration(configuration);
	}
	
	/**
	 * One-argument constructor
	 * @param configuration acota-core's configuration class
	 * @throws IOException Any exception that occurs while initializing Wordnet's dictionary
	 * @throws AcotaConfigurationException Any exception that occurs while initializing 
	 * a Configuration object
	 */
	public WordnetEnhancer(CoreConfiguration configuration) throws IOException, AcotaConfigurationException  {
		super();
		WordnetEnhancer.provider = new ProviderTO("Wordnet Enhancer");
		loadConfiguration(configuration);
	}
	
	@Override
	public void loadConfiguration(CoreConfiguration configuration) throws AcotaConfigurationException{
		if(configuration==null)
			configuration = new CoreConfiguration();
		this.configuration = configuration;
		this.wordnetRelevance = configuration.getWordnetRelevance();
		if(wordnetEnDict == null || !wordnetEnDict.equals(configuration.getWordnetEnDict())){
			this.wordnetEnDictModified = true;
			this.wordnetEnDict = configuration.getWordnetEnDict();
		}
	}

	protected void loadWordnetDict() throws IOException{
		if(dictionary!=null && dictionary.isOpen()){
			dictionary.close();		
		}
		URL url = new URL ("file", null, wordnetEnDict) ;
		this.dictionary = new Dictionary ( url ) ;
		dictionary.open();	
	}

	@Override
	protected void execute() throws Exception {
		if(wordnetEnDictModified){
			loadWordnetDict();
		}
		Set<Entry<String, TagTO>> backupSet = new HashSet<Entry<String, TagTO>>();
		for (Entry<String, TagTO> label : tags.entrySet()) {
			backupSet.add(label);
		}
		for (Entry<String, TagTO> label : backupSet) {
			findSynonims(label.getKey());
		}
	}

	@Override
	protected void preExecute() throws Exception {
		this.suggest = request.getSuggestions();
		this.tags = suggest.getTags();
		suggest.setResource(request.getResource());

	}

	@Override
	protected void postExecute() throws Exception {
		this.request.getTargetProviders().add(provider);
		this.request.setSuggestions(suggest);

	}

	/**
	 * Increases the weight of the terms that match with the synonyms of
	 * the label founded
	 * @param label Label founded
	 */
	protected void findSynonims(String label){
		IIndexWord idxWord = dictionary.getIndexWord (label , POS . NOUN ) ;
		if(idxWord!=null){
			IWordID wordID = idxWord.getWordIDs () . get (0) ;
			IWord word = dictionary.getWord ( wordID ) ;
			ISynset synset = word.getSynset () ;
			String cleanWord = "";
			for ( IWord w : synset.getWords () ){
				cleanWord = w.getLemma().replace('_', ' ').toLowerCase();
				if(!cleanWord.equals(label)){
					TagTO tag = tags.get(cleanWord);
					if(tag == null)
						tag = new TagTO(cleanWord, "en", provider, suggest.getResource());
					fillSuggestions(tag, wordnetRelevance);
				}
			}
		}
	}

}
