package  wekaCore;


/**
 * <code>UnsupportedClassTypeException</code> is used in situations
 * where the throwing object is not able to accept Instances with the
 * supplied structure, because the class Attribute is of the wrong type.
 *
 */
public class UnsupportedClassTypeException extends WekaException {

  /**
   * Creates a new <code>UnsupportedClassTypeException</code> instance
   * with no detail message.
   */
  public UnsupportedClassTypeException() { 
    super(); 
  }

  /**
   * Creates a new <code>UnsupportedClassTypeException</code> instance
   * with a specified message.
   *
   * @param messagae a <code>String</code> containing the message.
   */
  public UnsupportedClassTypeException(String message) { 
    super(message); 
  }
}
