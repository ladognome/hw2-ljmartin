package annotators;

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
 * Gets the N-best chunks from LingPipe's genetag named entity extractor (HMM chunker)
 * Updates CAS with annotation for each sentence -- passes to addtoCas
 */

public class GeneLingTagger extends Annotater_Helper {

  private ConfidenceChunker chunker;

  static int MAX_N_BEST_CHUNKS = 10;

  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    try {
      chunker = (ConfidenceChunker) AbstractExternalizable.readResourceObject((String) aContext
              .getConfigParameterValue("GeneHMM"));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void process(JCas aCAS) throws AnalysisEngineProcessException {

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
      if (conf > 0.6) {
        // System.out.println(start + " " + end + " " + conf + " " + doc.substring(start, end));
        addToCas(start, end, conf, doc, this.getClass().getName(), aCAS);
      }
    }
  }

}
