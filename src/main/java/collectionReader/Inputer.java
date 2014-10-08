package collectionReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import objects.DocID;

/* 
 * Given input file, opens and reads it
 * If input is not specified in cpe xml, uses PARAM_INPUTFILE default location
 * Each line is its own CAS
 * Separates line by <ID> <text>
 * Used org.apache.uima.examples.cpe.FileSystemCollectionReader as template
 */

public class Inputer extends CollectionReader_ImplBase {
  public static final String PARAM_INPUTFILE = "src/main/resources/data/sample.in";

  public static final String PARAM_ENCODING = "Encoding";

  // input file
  private File inFile;

  private String currLine = "";

  private BufferedReader reader;

  public void initialize() throws ResourceInitializationException {

    // extract configuration parameter settings
    String iPath = (String) getUimaContext().getConfigParameterValue("inputFile");

    // If the parameter is not set, read from default file
    if (iPath == null) {
      iPath = PARAM_INPUTFILE;
    }
    // Read the path and try to open the file
    inFile = new File(iPath.trim());
    try {
      reader = new BufferedReader(new FileReader(inFile));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void getNext(CAS aCAS) throws IOException, CollectionException {
    JCas jcas;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new CollectionException(e);
    }

    // if we haven't already started, get a line for our first CAS
    if (currLine == "") {
      currLine = reader.readLine();
    }

    // split text by <ID> <document text>
    int firstSpace = currLine.indexOf(' ');
    String id = currLine.substring(0, firstSpace);
    String text = currLine.substring(firstSpace + 1);

    // add object to CAS
    jcas.setDocumentText(text);
    DocID did = new DocID(jcas);
    did.setID(id);
    did.addToIndexes();

    // go to next line in text
    currLine = reader.readLine();
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  @Override
  public Progress[] getProgress() {
    return null;
  }

  @Override
  public boolean hasNext() throws IOException, CollectionException {
    return (currLine != null);
  }

}
