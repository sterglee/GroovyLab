package  wekaCore;


/**
 * <code>UnsupportedAttributeTypeException</code> is used in situations
 * where the throwing object is not able to accept Instances with the
 * supplied structure, because one or more of the Attributes in the
 * Instances are of the wrong type.
 *
 */
public class UnsupportedAttributeTypeException extends WekaException {

  /**
   * Creates a new <code>UnsupportedAttributeTypeException</code> instance
   * with no detail message.
   */
  public UnsupportedAttributeTypeException() { 
    super(); 
  }

  /**
   * Creates a new <code>UnsupportedAttributeTypeException</code> instance
   * with a specified message.
   *
   * @param messagae a <code>String</code> containing the message.
   */
  public UnsupportedAttributeTypeException(String message) { 
    super(message); 
  }
}
