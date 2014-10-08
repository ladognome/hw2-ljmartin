package casConsumer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import objects.AnnotationObject;
import objects.DocID;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.collection.base_cpm.CasObjectProcessor;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
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

  private ArrayList<String> predictions = new ArrayList<String>();

  public Outputer() {
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

    Iterator it = jcas.getAnnotationIndex(DocID.type).iterator();
    if (it.hasNext()) {
      DocID did = (DocID) it.next();
      id = did.getID();
    }

    // iterate and print annotations
    Iterator annotationIter = jcas.getAnnotationIndex(AnnotationObject.type).iterator();

    while (annotationIter.hasNext()) {
      AnnotationObject annot = (AnnotationObject) annotationIter.next();
      String outString = "";
      // System.out.println(annot.getCasProcessorId());
      try {
        if (id != null)
          outString = id + "|" + annot.getBegin() + " " + annot.getEnd() + "|"
                  + annot.getGeneName();
        if (!predictions.contains(outString)) {
          fileWriter.write(outString + "\n");
          predictions.add(outString);
        }

      } catch (IOException e) {
        throw new ResourceProcessException(e);
      }

    }
  }

  @Override
  public void destroy() {
    if (gold != null) {
      try {
        checkAccuracy(predictions, gold);
      } catch (IOException e) {
        // TODO Auto-generated catch block
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
   * @param prediction
   *          an arrayList of strings that hold your predictions in the same format as the
   *          goldStandard, split by line
   * @param goldStandard
   *          a string of the location where the goldStandard file can be found
   * @return void
   */
  private void checkAccuracy(ArrayList<String> prediction, String goldStandard) throws IOException {

    BufferedReader br = new BufferedReader(new FileReader(goldStandard));
    HashMap<String, String> gold = new HashMap<String, String>();
    String line;
    //prediction = id + "|" + annot.getBegin() + " " + annot.getEnd() + "|" + annot.getGeneName();
    while ((line = br.readLine()) != null) {
      int p=line.lastIndexOf("|");
      String gene = line.substring(p+1);
      String key = line.substring(0,p+1);
      gold.put(key, gene);
    }
    br.close();

    int relevant = gold.size();
    int retrieved = prediction.size();
    int relRet = 0;

    for (String s : prediction) {
      int p=s.lastIndexOf("|");
      String key = s.substring(0,p+1);
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
