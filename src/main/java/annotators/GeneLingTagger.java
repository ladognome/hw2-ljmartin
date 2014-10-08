package annotators;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.ConfidenceChunker;
import com.aliasi.util.AbstractExternalizable;

import util.Annotater_Helper;

/*
 * Wrapper to call PosTagNamedEntityRecognizer; gets nouns and adjectives
 * Gets word from text, given the positions
 * Removes whitespaces in positions
 * Updates CAS with annotation, except for words found in list of most common English words
 * Used org.apache.uima.examples.cas.RegExAnnotator as template
 */

public class GeneLingTagger extends Annotater_Helper {

  private ConfidenceChunker chunker;

  static int MAX_N_BEST_CHUNKS;

  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    try {
      chunker = (ConfidenceChunker) AbstractExternalizable.readResourceObject((String) aContext.getConfigParameterValue("GeneHMM"));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    MAX_N_BEST_CHUNKS = 10;
  }

  @Override
  public void process(JCas aCAS) throws AnalysisEngineProcessException {
    // The JCas object is the data object inside UIMA where all the
    // information is stored. It contains all annotations created by
    // previous annotators, and the document text to be analyzed.

    // get document text from JCas
    String docText = aCAS.getDocumentText();

    iterateMap(docText, aCAS);

  }

  /**
   * Adding annotations to CAS from a map of positions, eliminating common words
   *
   * @param pos
   *          a map with starting positions(int) for keys and end positions(int) as values
   * @param doc
   *          a string
   * @param aCAS
   *          the JCas you want to add the annotations to
   * @return void
   */
  private void iterateMap(String doc, JCas aCAS) {

    char[] cs = doc.toCharArray();
    Iterator<Chunk> it = chunker.nBestChunks(cs, 0, cs.length, MAX_N_BEST_CHUNKS);
    while (it.hasNext()) {
      Chunk chunk = it.next();
      double conf = Math.pow(2.0, chunk.score());
      int start = chunk.start();
      int end = chunk.end();
      if (conf > 0.8) {
        // System.out.println(start + " " + end + " " + conf + " " + doc.substring(start, end));
        addToCas(start, end, conf, doc, this.getClass().getName(), aCAS);
      }
    }
  }

}
