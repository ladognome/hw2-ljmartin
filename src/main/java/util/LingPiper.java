package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;

import com.aliasi.classify.ConditionalClassification;
import com.aliasi.hmm.HiddenMarkovModel;
import com.aliasi.hmm.HmmDecoder;
import com.aliasi.tag.TagLattice;
import com.aliasi.tokenizer.RegExTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.Streams;

public class LingPiper {
  FileInputStream fileIn;

  ObjectInputStream objIn;

  HiddenMarkovModel hmm;

  HmmDecoder decoder;

  static TokenizerFactory TOKENIZER_FACTORY;

  public LingPiper() throws ResourceInitializationException {

    try {
      fileIn = new FileInputStream("src/main/resources/hmm/pos-en-bio-genia.HiddenMarkovModel");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    try {
      objIn = new ObjectInputStream(fileIn);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      hmm = (HiddenMarkovModel) objIn.readObject();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    Streams.closeQuietly(objIn);
    decoder = new HmmDecoder(hmm);
    TOKENIZER_FACTORY = new RegExTokenizerFactory("(\\d|\\p{L})+|\\S");
  }

  public Map<Integer, String> getGeneSpans(String text) {
    Map<Integer, String> begin2end = new HashMap<Integer, String>();
    char[] cs = text.toCharArray();
    Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(cs, 0, cs.length);
    List<String> tokenList = Arrays.asList(tokenizer.tokenize()); 
    int start = 0;
    int end = 0;

    TagLattice<String> lattice = decoder.tagMarginal(tokenList);
    for (int tokenIndex = 0; tokenIndex < tokenList.size(); ++tokenIndex) {
      ConditionalClassification tagScores = lattice.tokenClassification(tokenIndex);
      start+=(end-start);
      end+=tokenList.get(tokenIndex).length();
      for (int i = 0; i < 4; ++i) {
        double conditionalProb = tagScores.score(i);
        String tag = tagScores.category(i);
        System.out.println(conditionalProb + ":"+tag);
      }
      String outString = end + "\t"+tokenList.get(tokenIndex);
      begin2end.put(start, outString);
    }
    return begin2end;

  }
}
