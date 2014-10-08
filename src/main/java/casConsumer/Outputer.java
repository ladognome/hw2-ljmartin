package casConsumer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import objects.AnnotationObject;
import objects.DocID;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.collection.base_cpm.CasObjectProcessor;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;

/* 
 * Outputs annotations in <ID>|<start> <end>|<entity> form
 * Prints to file specified from cpe xml(required)
 * Can specify gold standard file to compare to in order to create precision and recall measures
 * Used org.apache.uima.examples.cpe.AnnotationPrinter as template
 */

public class Outputer extends CasConsumer_ImplBase implements CasObjectProcessor {
  File outFile;

  FileWriter fileWriter;

  private String gold;

  private HashMap<String, Double> predictions;

  private double threshold = 0.8;

  public Outputer() {
    predictions = new HashMap<String, Double>();
  }

  public void initialize() throws ResourceInitializationException {

    // extract configuration parameter settings
    String oPath = (String) getUimaContext().getConfigParameterValue("outputFile");
    // find goldStandard file, if it is given
    gold = (String) getUimaContext().getConfigParameterValue("goldStandard");

    // Output file should be specified in the descriptor
    if (oPath == null) {
      throw new ResourceInitializationException(
              ResourceInitializationException.CONFIG_SETTING_ABSENT, new Object[] { "outputFile" });
    }

    // If specified output directory does not exist, try to create it
    outFile = new File(oPath.trim());
    if (outFile.getParentFile() != null && !outFile.getParentFile().exists()) {
      if (!outFile.getParentFile().mkdirs())
        throw new ResourceInitializationException(
                ResourceInitializationException.RESOURCE_DATA_NOT_VALID, new Object[] { oPath,
                    "outputFile" });
    }
    try {
      fileWriter = new FileWriter(outFile);
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }
  }

  @Override
  public void processCas(CAS aCAS) throws ResourceProcessException {
    JCas jcas;
    String id = null;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }

    Iterator<Annotation> it = jcas.getAnnotationIndex(DocID.type).iterator();
    if (it.hasNext()) {
      DocID did = (DocID) it.next();
      id = did.getID();
    }

    // iterate and print annotations
    Iterator<Annotation> annotationIter = jcas.getAnnotationIndex(AnnotationObject.type).iterator();
    while (annotationIter.hasNext()) {
      String outString = "";
      AnnotationObject annot = (AnnotationObject) annotationIter.next();
      if (id != null) {
        outString = id + "|" + annot.getBegin() + " " + annot.getEnd() + "|" + annot.getGeneName();
        if (annot.getCasProcessorId() == "annotators.PosTagger") {
          if (predictions.containsKey(outString)) {
            double conf = predictions.get(outString) + annot.getConfidence();
            predictions.put(outString, conf);
          }
        } else {
          predictions.put(outString, annot.getConfidence());
        }

      }

    }
  }

  @Override
  public void destroy() {
    Iterator<Entry<String, Double>> it = predictions.entrySet().iterator();
    while (it.hasNext()) {
      Entry<String, Double> pairs = (Entry<String, Double>) it.next();
      if (pairs.getValue() >= threshold) {
        try {
          fileWriter.write(pairs.getKey() + "\n");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    if (gold != null) {
      try {
        checkAccuracy(predictions, gold);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (fileWriter != null) {
      try {
        fileWriter.close();
      } catch (IOException e) {
        // ignore IOException on destroy
      }
    }
  }

  /**
   * Prints (to System.out) precision and recall given a gold-standard and the predictions.
   *
   * @param predictions
   *          an arrayList of strings that hold your predictions in the same format as the
   *          goldStandard, split by line
   * @param goldStandard
   *          a string of the location where the goldStandard file can be found
   * @return void
   */
  private void checkAccuracy(HashMap<String, Double> predictions, String goldStandard)
          throws IOException {

    BufferedReader br = new BufferedReader(new FileReader(goldStandard));
    HashMap<String, String> gold = new HashMap<String, String>();
    String line;
    // prediction = id + "|" + annot.getBegin() + " " + annot.getEnd() + "|" + annot.getGeneName();
    while ((line = br.readLine()) != null) {
      int p = line.lastIndexOf("|");
      String gene = line.substring(p + 1);
      String key = line.substring(0, p + 1);
      gold.put(key, gene);
    }
    br.close();

    int relevant = gold.size();
    int retrieved = predictions.size();
    int relRet = 0;

    for (String s : predictions.keySet()) {
      int p = s.lastIndexOf("|");
      String key = s.substring(0, p + 1);
      if (gold.containsKey(key)) {
        relRet++;
      }
    }

    double precision = (double) relRet / (double) retrieved;
    double recall = (double) relRet / (double) relevant;
    double f1 = 2 * precision * recall / (precision + recall);
    System.out.println("Precision: " + relRet + "/" + retrieved + " = " + precision + "\nRecall: "
            + relRet + "/" + relevant + " = " + recall + "\nF1: " + f1);

  }

}
