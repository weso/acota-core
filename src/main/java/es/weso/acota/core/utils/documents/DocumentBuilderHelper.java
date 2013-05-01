package es.weso.acota.core.utils.documents;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import es.weso.acota.core.exceptions.AcotaDocumentBuilderException;

/**
 * DocumentBuilderHelper is a helper class for building Documents from different sources.
 */
public class DocumentBuilderHelper {

	/**
	 * Returns a StringReader from the supplied content
	 * @param content String providing the character stream
	 * @return StringReader from the supplied content
	 */
    public static Reader getStringReader(String content) {
        return new StringReader(content);
    }

    /**
     * Returns a InputSource from the supplied URI
     * @param uri The system identifier of the file
     * @return InputSource from the article 
     */
    public static InputSource getInputSourceFromString(String uri) {
        Reader reader = getStringReader(uri);
        return new InputSource(reader);
    }

    /**
     * Returns a InputSource from the supplied Reader
     * @param reader Character streams
     * @return InputSource from the article 
     */
    public static InputSource getInputSourceFromReader(Reader reader) {
        return new InputSource(reader);
    }

    /**
     * Creates a new DocumentBuilder
     * @return New DocumentBuilder
     * @throws AcotaDocumentBuilderException Any exception that occurs during the DOM creation.
     */
    public static DocumentBuilder createDocumentBuilder() throws AcotaDocumentBuilderException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            return factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new AcotaDocumentBuilderException(e);
        }
    }

    /**
     * Returns a Document from the input path
     * @param uri The system identifier of the file
     * @return New Document from input path
     * @throws AcotaDocumentBuilderException Any exception that occurs during the DOM creation.
     */
    public static Document getDocumentFromString(String uri) throws AcotaDocumentBuilderException {
        try {
            return createDocumentBuilder().parse(getInputSourceFromString(uri));
        } catch (SAXException e) {
            throw new AcotaDocumentBuilderException(e);
        } catch (IOException e) {
            throw new AcotaDocumentBuilderException(e);
        } catch (AcotaDocumentBuilderException e) {
            throw new AcotaDocumentBuilderException(e);
        }
    }

    /**
     * Returns a Document from the supplied Reader
     * @param reader Supplied Reader
     * @return New Document from the supplied Reader
     * @throws AcotaDocumentBuilderException Any exception that occurs during the DOM creation.
     */
    public static Document getDocumentFromReader(Reader reader) throws AcotaDocumentBuilderException {
        try {
            return createDocumentBuilder().parse(getInputSourceFromReader(reader));
        } catch (SAXException e) {
            throw new AcotaDocumentBuilderException(e);
        } catch (IOException e) {
            throw new AcotaDocumentBuilderException(e);
        }
    }

    /**
     * Returns a new Document from the supplied inputStream
     * @param inputStream Supplied InputStream
     * @return New Document from the supplied inputStream
     * @throws AcotaDocumentBuilderException Any exception that occurs during the DOM creation.
     */
    public static Document getDocumentFromInputStream(InputStream inputStream) throws AcotaDocumentBuilderException {
        try {
            return createDocumentBuilder().parse(inputStream);
        } catch (SAXException e) {
            throw new AcotaDocumentBuilderException(e);
        } catch (IOException e) {
            throw new AcotaDocumentBuilderException(e);
        }
    }

    /**
     * Returns an empty Document
     * @return Empty Document
     * @throws AcotaDocumentBuilderException Any exception that occurs during the DOM creation.
     */
    public static Document getEmptyDocument() throws AcotaDocumentBuilderException {
        return createDocumentBuilder().newDocument();
    }

    /**
     * Returns a Document from the File
     * @param file File Path
     * @return Document from the File
     * @throws AcotaDocumentBuilderException Any exception that occurs during the DOM creation.
     */
    public static Document getDocumentFromFile(File file) throws AcotaDocumentBuilderException{
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return getDocumentFromInputStream(is);
        } catch(IOException e){
        	 throw new AcotaDocumentBuilderException(e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                throw new AcotaDocumentBuilderException(e);
            }
        }
    }

}