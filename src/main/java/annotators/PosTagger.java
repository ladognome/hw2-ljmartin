package annotators;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import objects.AnnotationObject;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import util.PosTagNamedEntityRecognizer;

/*
 * Wrapper to call PosTagNamedEntityRecognizer; gets nouns and adjectives
 * Gets word from text, given the positions
 * Removes whitespaces in positions
 * Updates CAS with annotation, except for words found in list of most common English words
 * Used org.apache.uima.examples.cas.RegExAnnotator as template
 * 
 * List of most common words is from:
 * http://www.wordfrequency.info/free.asp
 */

public class PosTagger extends JCasAnnotator_ImplBase {

  private PosTagNamedEntityRecognizer pt;

  private String commonWords = "src/main/resources/data/commonWords.txt";

  private ArrayList<String> common;

  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    pt = new PosTagNamedEntityRecognizer();

    // list of most common words in English
    try {
      common = readFile(commonWords);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void process(JCas aCAS) throws AnalysisEngineProcessException {
    // The JCas object is the data object inside UIMA where all the
    // information is stored. It contains all annotations created by
    // previous annotators, and the document text to be analyzed.

    // get document text from JCas
    String docText = aCAS.getDocumentText();

    // call PosTagNamedEntityRecognizer's span finder
    Map<Integer, Integer> pos = pt.getGeneSpans(docText);
    iterateMap(pos, docText, aCAS);

  }

  /**
   * Given a string, find the number of whitespace characters
   *
   * @param input
   *          a string
   * @return the count of whitespaces
   */
  private int countWhitespace(String input) {
    // given a string, find the number of whitespace characters
    int counter = 0;
    for (int i = 0; i < input.length(); i++) {
      if (Character.isWhitespace(input.charAt(i))) {
        counter++;
      }
    }
    return counter;
  }

  /**
   * Adding annotations to CAS from a map of positions, eliminating common words
   *
   * @param m
   *          a map with starting positions(int) for keys and end positions(int) as values
   * @param doc
   *          a string
   * @param aCAS
   *          the JCas you want to add the annotations to
   * @return void
   */
  private void iterateMap(Map<Integer, Integer> m, String doc, JCas aCAS) {
    // for each mapping from PosTagNamedEntityRecognizer
    for (Integer key : m.keySet()) {
      int start = key;
      int end = m.get(key);
      String entity = doc.substring(start, end);

      // if it's not a common word in English, then it's probably not a gene name
      if (!common.contains(entity)) {
        // fixing indices to remove whitespace
        int preEntitySpaceCount = countWhitespace(doc.substring(0, start));
        int entitySpaceCount = countWhitespace(entity);

        // add annotation to the CAS
        AnnotationObject ann = new AnnotationObject(aCAS);
        ann.setBegin(start - preEntitySpaceCount);
        ann.setEnd(end - preEntitySpaceCount - entitySpaceCount - 1);
        ann.setGeneName(entity);
        ann.addToIndexes();
      }
    }
  }

  /**
   * Reads a file and puts each line into an ArrayList
   *
   * @param file
   *          String path to be read
   * @return ArrayList<String> of lines from the file
   */
  private ArrayList<String> readFile(String file) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(file));
    ArrayList<String> list = new ArrayList<String>();
    String line;
    while ((line = br.readLine()) != null) {
      list.add(line.trim());
    }
    br.close();
    return list;
  }
}
