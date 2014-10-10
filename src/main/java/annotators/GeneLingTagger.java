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

import util.AnnotaterHelper;

/**
 * Analysis Engine which gets the N-best chunks from LingPipe's genetag named entity extractor (HMM
 * chunker). Updates CAS with annotation for each sentence -- passes to addtoCas.
 * 
 * @author Lara Martin
 **/

public class GeneLingTagger extends AnnotaterHelper {

  private ConfidenceChunker chunker;

  // max number of chunks to find in a document
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

  /**
   * Call chunker method for each CAS
   **/
  @Override
  public void process(JCas aCAS) throws AnalysisEngineProcessException {

    // get document text from JCas
    String docText = aCAS.getDocumentText();

    chunker(docText, aCAS);

  }

  /**
   * Adding annotations to CAS from LingPipe chunker
   *
   * @param doc
   *          a string of the original text
   * @param aCAS
   *          the JCas you want to add the annotations to
   * @return void
   */
  private void chunker(String doc, JCas aCAS) {

    char[] cs = doc.toCharArray();
    Iterator<Chunk> it = chunker.nBestChunks(cs, 0, cs.length, MAX_N_BEST_CHUNKS);
    while (it.hasNext()) {
      Chunk chunk = it.next();
      double conf = Math.pow(2.0, chunk.score());
      int start = chunk.start();
      int end = chunk.end();
      // Only add to CAS if chunk above 0.6 confidence
      if (conf > 0.6) {
        // System.out.println(start + " " + end + " " + conf + " " + doc.substring(start, end));
        addToCas(start, end, conf, doc, this.getClass().getName(), aCAS);
      }
    }
  }

}
