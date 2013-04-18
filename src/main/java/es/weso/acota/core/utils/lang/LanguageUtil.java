package es.weso.acota.core.utils.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

import es.weso.acota.core.exceptions.AcotaConfigurationException;

/**
 * Language Detection Util-Class, this class perform the language detection
 * and also contains the ISO 639 Codes supported by ACOTA. 
 * @see http://en.wikipedia.org/wiki/ISO_639
 * 
 * @author César Luis Alvargonzález
 */
public abstract class LanguageUtil {
	public static final String ISO_639_ENGLISH = "en";
	public static final String ISO_639_SPANISH = "es";
	public static final String ISO_639_UNDEFINED = "undefined";
	
	/**
	 * Detects the language of the supplied text
	 * @param text The text to detect
	 * @return ISO 639 Language Code
	 * @throws AcotaConfigurationException Any exception that occurs 
	 * while initializing a Configuration object
	 */
	public static String detect(String text) throws AcotaConfigurationException{
		Detector detector = null;
		
		if(text.isEmpty() || NumberUtils.isNumber(text))
			return ISO_639_UNDEFINED;
		try{
			if(DetectorFactory.getLangList().isEmpty()){
				loadProfilesAsJson();
			}
			detector = DetectorFactory.create();
			detector.append(text);
			return detector.detect();
		}catch(LangDetectException e){
			throw new AcotaConfigurationException("Failed langdetec initialization: "+text, e);
		}	
	}
	
	/**
	 * Loads the languages profiles from resources folder
	 * @throws AcotaConfigurationException Any exception that occurs 
	 * while initializing a Configuration object
	 */
	private static void loadProfilesAsJson() throws AcotaConfigurationException {
		try{
			String[] lan = new String[]{"de","en","es","fr","pt"};
			InputStream input = null;
			StringWriter writer = null;
			List<String> languages = new LinkedList<String>();
			for(String language : lan){
				input = LanguageUtil.class.getClassLoader().getResourceAsStream("resources/profiles/"+language);
				writer = new StringWriter();
				IOUtils.copy(input, writer, "utf-8");
				languages.add(writer.toString());
			}
			DetectorFactory.loadProfile(languages);
		}catch(IOException e1){
			throw new AcotaConfigurationException(e1);
		} catch (LangDetectException e2) {
			throw new AcotaConfigurationException(e2);
		}
	}
		
}
