package objects;


/* First created by JCasGen Sun Sep 28 20:45:07 EDT 2014 */

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import edu.cmu.deiis.types.Annotation;


/** 
 * Updated by JCasGen Tue Oct 07 21:14:15 EDT 2014
 * XML source: /home/lara/workspace/hw2-ljmartin/src/main/resources/descriptors/aaeDescriptor.xml
 * @generated */
public class AnnotationObject extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(AnnotationObject.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected AnnotationObject() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public AnnotationObject(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public AnnotationObject(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public AnnotationObject(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: geneName

  /** getter for geneName - gets entity
   * @generated
   * @return value of the feature 
   */
  public String getGeneName() {
    if (AnnotationObject_Type.featOkTst && ((AnnotationObject_Type)jcasType).casFeat_geneName == null)
      jcasType.jcas.throwFeatMissing("geneName", "objects.AnnotationObject");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AnnotationObject_Type)jcasType).casFeatCode_geneName);}
    
  /** setter for geneName - sets entity 
   * @generated
   * @param v value to set into the feature 
   */
  public void setGeneName(String v) {
    if (AnnotationObject_Type.featOkTst && ((AnnotationObject_Type)jcasType).casFeat_geneName == null)
      jcasType.jcas.throwFeatMissing("geneName", "objects.AnnotationObject");
    jcasType.ll_cas.ll_setStringValue(addr, ((AnnotationObject_Type)jcasType).casFeatCode_geneName, v);}    
   
    
  //*--------------*
  //* Feature: start

  /** getter for start - gets starting position (no spaces)
   * @generated
   * @return value of the feature 
   */
  public int getStart() {
    if (AnnotationObject_Type.featOkTst && ((AnnotationObject_Type)jcasType).casFeat_start == null)
      jcasType.jcas.throwFeatMissing("start", "objects.AnnotationObject");
    return jcasType.ll_cas.ll_getIntValue(addr, ((AnnotationObject_Type)jcasType).casFeatCode_start);}
    
  /** setter for start - sets starting position (no spaces) 
   * @generated
   * @param v value to set into the feature 
   */
  public void setStart(int v) {
    if (AnnotationObject_Type.featOkTst && ((AnnotationObject_Type)jcasType).casFeat_start == null)
      jcasType.jcas.throwFeatMissing("start", "objects.AnnotationObject");
    jcasType.ll_cas.ll_setIntValue(addr, ((AnnotationObject_Type)jcasType).casFeatCode_start, v);}    
   
    
  //*--------------*
  //* Feature: end

  /** getter for end - gets ending position (no spaces)
   * @generated
   * @return value of the feature 
   */
  public int getEnd() {
    if (AnnotationObject_Type.featOkTst && ((AnnotationObject_Type)jcasType).casFeat_end == null)
      jcasType.jcas.throwFeatMissing("end", "objects.AnnotationObject");
    return jcasType.ll_cas.ll_getIntValue(addr, ((AnnotationObject_Type)jcasType).casFeatCode_end);}
    
  /** setter for end - sets ending position (no spaces) 
   * @generated
   * @param v value to set into the feature 
   */
  public void setEnd(int v) {
    if (AnnotationObject_Type.featOkTst && ((AnnotationObject_Type)jcasType).casFeat_end == null)
      jcasType.jcas.throwFeatMissing("end", "objects.AnnotationObject");
    jcasType.ll_cas.ll_setIntValue(addr, ((AnnotationObject_Type)jcasType).casFeatCode_end, v);}    
  }

    