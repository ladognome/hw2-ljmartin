package annotators;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import util.AnnotaterHelper;

/**
 * Analysis engine which gets nouns and adjectives from a specified text. Updates CAS with
 * annotation, except for words found in list of most common English words -- passes to addtoCas
 * 
 * List of most common words is from: http://www.wordfrequency.info/free.asp
 * 
 * @author Lara Martin
 **/

public class PosTagger extends AnnotaterHelper {

  private ArrayList<String> common;

  private StanfordCoreNLP pipeline;

  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos");
    pipeline = new StanfordCoreNLP(props);

    // list of most common words in English
    try {
      common = readFile((String) aContext.getConfigParameterValue("commonWords"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void process(JCas aCAS) throws AnalysisEngineProcessException {
    // get document text from JCas
    String docText = aCAS.getDocumentText();
    Map<Integer, Integer> pos = getGeneSpans(docText);
    iterateMap(pos, docText, aCAS);

  }

  /**
   * Adding annotations to CAS from a map of positions, eliminating common words. Sets confidence to
   * the F1 measure of this AE used by itself on the training data.
   *
   * @param m
   *          a map with starting positions(int) for keys and end positions(int) as values
   * @param doc
   *          a string that needs annotation
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

      if (!common.contains(entity)) {
        // Using PosTagger by itself---
        // Precision: 10862/86763 = 0.12519161393681638
        // Recall: 10862/18265 = 0.5946892964686559
        // F1: 0.206840080740374
        addToCas(start, end, 0.206840080740374, doc, this.getClass().getName(), aCAS);
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
    BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader()
            .getResourceAsStream(file)));
    ArrayList<String> list = new ArrayList<String>();
    String line;
    while ((line = br.readLine()) != null) {
      list.add(line.trim());
    }
    br.close();
    return list;
  }

  /**
   * Adding annotations to CAS from a map of positions, eliminating common words. Sets confidence to
   * the F1 measure of this AE used by itself on the training data.
   *
   * @param text
   *          Document text which needs annotations
   * @return Map<Integer, Integer> of positions of annotations. Key is beginning index, value is end
   *         index.
   */
  public Map<Integer, Integer> getGeneSpans(String text) {
    Map<Integer, Integer> begin2end = new HashMap<Integer, Integer>();
    Annotation document = new Annotation(text);
    pipeline.annotate(document);
    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
    for (CoreMap sentence : sentences) {
      List<CoreLabel> candidate = new ArrayList<CoreLabel>();
      for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
        String pos = token.get(PartOfSpeechAnnotation.class);
        if (pos.startsWith("NN") || pos.startsWith("JJ")) {
          candidate.add(token);
        } else if (candidate.size() > 0) {
          int begin = candidate.get(0).beginPosition();
          int end = candidate.get(candidate.size() - 1).endPosition();
          begin2end.put(begin, end);
          candidate.clear();
        }
      }
      if (candidate.size() > 0) {
        int begin = candidate.get(0).beginPosition();
        int end = candidate.get(candidate.size() - 1).endPosition();
        begin2end.put(begin, end);
        candidate.clear();
      }
    }
    return begin2end;
  }

}
