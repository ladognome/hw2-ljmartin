package util;

import objects.AnnotationObject;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;

public abstract class Annotater_Helper extends JCasAnnotator_ImplBase {

  int i = 0;
  /**
   * Given a string, find the number of whitespace characters
   *
   * @param input
   *          a string
   * @return the count of whitespaces
   */
  protected int countWhitespace(String input) {
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
   * Given a series of items, add them to an annotation object
   *
   * @param start
   *          the start position of the entity
   * @param end
   *          the end position of the entity
   * @param conf
   *          a (double) confidence value
   * @param doc
   *          the original document text
   * @param aCASint
   *          the JCas we're working with
   * 
   * @return an AnnotationObject with all of the values added
   */
  protected void addToCas(int start, int end, double conf, String doc, String CAS_ID, JCas aCAS) {
    String entity = doc.substring(start, end);
    // fixing indices to remove whitespace
    int preEntitySpaceCount = countWhitespace(doc.substring(0, start));
    int entitySpaceCount = countWhitespace(entity);

    // add annotation to the CAS
    AnnotationObject ann = new AnnotationObject(aCAS);
    ann.setBegin(start - preEntitySpaceCount);
    ann.setEnd(end - preEntitySpaceCount - entitySpaceCount - 1);
    ann.setConfidence(conf);
    ann.setCasProcessorId(CAS_ID);
    ann.setGeneName(entity);
    ann.addToIndexes();
    i++;
    System.out.println(i+ "                "+conf + "       (" + start + ", " + end + ")       " + entity);
    
  }

}
