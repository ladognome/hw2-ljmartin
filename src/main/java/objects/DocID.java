

/* First created by JCasGen Tue Oct 07 20:11:51 EDT 2014 */
package objects;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Oct 07 20:26:24 EDT 2014
 * XML source: /home/lara/workspace/hw2-ljmartin/src/main/resources/descriptors/collectionReaderDescriptor.xml
 * @generated */
public class DocID extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(DocID.class);
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
  protected DocID() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public DocID(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public DocID(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public DocID(JCas jcas, int begin, int end) {
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
  //* Feature: ID

  /** getter for ID - gets document ID
   * @generated
   * @return value of the feature 
   */
  public String getID() {
    if (DocID_Type.featOkTst && ((DocID_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "objects.DocID");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocID_Type)jcasType).casFeatCode_ID);}
    
  /** setter for ID - sets document ID 
   * @generated
   * @param v value to set into the feature 
   */
  public void setID(String v) {
    if (DocID_Type.featOkTst && ((DocID_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "objects.DocID");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocID_Type)jcasType).casFeatCode_ID, v);}    
  }

    