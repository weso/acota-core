package es.weso.acota.core.business.enhancer.analyzer.lucene;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LengthFilter;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

/**
 * For non English text, it tokenizes, converts to
 * lower case, removes words to long (>50) and to short(<3),
 * and removes English stops words.
 * 
 * @author César Luis Alvargonzález
 */
public class EnglishStopAnalyzer extends Analyzer {
	
	private static EnglishStopAnalyzer ENGLISH_STOP_ANALYZER;
	
	/**
	 * Zero-argument default constructor.
	 */
	private EnglishStopAnalyzer() {}

	/**
	 * Gets an instance of {@link EnglishStopAnalyzer }, 
	 * in the case it does not exists, it will create one,
	 * @return The {@link EnglishStopAnalyzer }'s instance
	 */
	public static EnglishStopAnalyzer getInstance(){
		if(ENGLISH_STOP_ANALYZER==null)
			EnglishStopAnalyzer.ENGLISH_STOP_ANALYZER = new EnglishStopAnalyzer();
		return ENGLISH_STOP_ANALYZER;
	}
	
	@Override
	public final TokenStream tokenStream(String arg0, Reader reader) {
		TokenStream result = new StandardTokenizer(Version.LUCENE_31, reader);
		
		result = new LowerCaseFilter(Version.LUCENE_31, result);
		
		result = new LengthFilter(false, result, 3, 50);
		
		result = new StopFilter(Version.LUCENE_31,result, StopAnalyzer.ENGLISH_STOP_WORDS_SET); 
	
		return result;
	}
	
	@Override
	public final TokenStream reusableTokenStream(String fieldName, Reader reader)
			throws IOException {
		return super.reusableTokenStream(fieldName, reader);
	}
}
