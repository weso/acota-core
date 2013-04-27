package es.weso.acota.core.business.enhancer.analyzer.lucene;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LengthFilter;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

/**
 * For non Spanish text, it tokenizes, converts to
 * lower case, removes words to long (>50) and to short(<3),
 * and removes Spanish stops words.
 * 
 * @author César Luis Alvargonzález
 */
public class SpanishStopAnalyzer extends Analyzer {
	
	private final static String[] SPANISH_STOP_WORDS = {
		"de","la","que","el","en","y","a","los","del","se","las","por","un","para",
		"con","no","una","su","al","es","lo","como","más","pero","sus","le","ya","o",
		"fue","este","ha","sí", "si","porque","esta","son","entre","está","cuando","muy","sin",
		"sobre","ser","tiene","también","me","hasta","hay","donde","han","quien","están",
		"estado","desde","todo","nos","durante","estados","todos","uno","les","ni","contra",
		"otros","fueron","ese","eso","había","ante","ellos","e","esto","mí","antes","algunos",
		"qué","unos","yo","otro","otras","otra","él","tanto","esa","estos","mucho","quienes",
		"nada","muchos","cual","sea","poco","ella","estar","haber","estas","estaba","estamos",
		"algunas","algo","nosotros","mi","mis","tú","te","ti","tu","tus","ellas","nosotras",
		"vosotros","vosotras","os","mío","mía","míos","mías","tuyo","tuya","tuyos","tuyas",
		"suyo","suya","suyos","suyas","nuestro","nuestra","nuestros","nuestras","vuestro",
		"vuestra","vuestros","vuestras","esos","esas","estoy","estás","está","estamos",
		"estáis","están","esté","estés","estemos","estéis","estén","estaré","estarás",
		"estará","estaremos","estaréis","estarán","estaría","estarías","estaríamos"
		,"estaríais","estarían","estaba","estabas","estábamos","estabais","estaban",
		"estuve","estuviste","estuvo","estuvimos","estuvisteis","estuvieron","estuviera",
		"estuvieras","estuviéramos","estuvierais","estuvieran","estuviese","estuvieses","estuviésemos",
		"estuvieseis","estuviesen","estando","estado","estada","estados","estadas","estad","he","has","ha",
		"hemos","habéis","han","haya","hayas","hayamos","hayáis","hayan","habré","habrás","habrá","habremos",
		"habréis","habrán","habría","habrías","habríamos","habríais","habrían","había","habías","habíamos",
		"habíais","habían","hube","hubiste","hubo","hubimos","hubisteis","hubieron","hubiera","hubieras",
		"hubiéramos","hubierais","hubieran","hubiese","hubieses","hubiésemos","hubieseis","hubiesen",
		"habiendo","habido","habida","habidos","habidas","soy","eres","es","somos","sois","son",
		"sea","seas","seamos","seáis","sean","seré","serás","será","seremos","seréis","serán",
		"sería","serías","seríamos","seríais","serían","era","eras","éramos","erais","eran",
		"fui","fuiste","fue","fuimos","fuisteis","fueron","fuera","fueras","fuéramos",
		"fuerais","fueran","fuese","fueses","fuésemos","fueseis","fuesen","siendo",
		"sido","tengo","tienes","tiene","tenemos","tenéis","tienen","tenga","tengas",
		"tengamos","tengáis","tengan","tendré","tendrás","tendrá","tendremos",
		"tendréis","tendrán","tendría","tendrías","tendríamos","tendríais",
		"tendrían","tenía","tenías","teníamos","teníais","tenían","tuve","tuviste","tuvo",
		"tuvimos","tuvisteis","tuvieron","tuviera","tuvieras","tuviéramos","tuvierais","tuvieran",
		"tuviese","tuvieses","tuviésemos","tuvieseis","tuviesen","teniendo","tenido","tenida","tenidos",
		"tenidas","tened"
	};
	
	private static SpanishStopAnalyzer SPANISH_STOP_ANALYZER;
	
	protected Set<Object> stopWrods;
	
	/**
	 * Zero-argument default constructor.
	 */
	private SpanishStopAnalyzer() {
		this.stopWrods = StopFilter.makeStopSet(Version.LUCENE_31,SPANISH_STOP_WORDS);
	}
	
	/**
	 * Gets an instance of {@link EnglishStopAnalyzer }, 
	 * in the case it does not exists, it will create one,
	 * @return The {@link EnglishStopAnalyzer }'s instance
	 */
	public static SpanishStopAnalyzer getInstance(){
		if(SPANISH_STOP_ANALYZER==null)
			SpanishStopAnalyzer.SPANISH_STOP_ANALYZER = new SpanishStopAnalyzer();
		return SPANISH_STOP_ANALYZER;
	}

	@Override
	public final TokenStream tokenStream(String arg0, Reader reader) {

		TokenStream result = new StandardTokenizer(Version.LUCENE_31, reader);  
		
		result = new LowerCaseFilter(Version.LUCENE_31, result);
		
		result = new LengthFilter(false, result, 3, 50);
		
		return new StopFilter(Version.LUCENE_31,result, stopWrods);
	}
	
	@Override
	public final TokenStream reusableTokenStream(String fieldName, Reader reader)
			throws IOException {
		return super.reusableTokenStream(fieldName, reader);
	}

}
