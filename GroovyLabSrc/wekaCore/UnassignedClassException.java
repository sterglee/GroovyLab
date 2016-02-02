package  wekaCore;

public class UnassignedClassException extends RuntimeException {

  /**
   * Creates a new <code>UnassignedClassException</code> instance
   * with no detail message.
   */
  public UnassignedClassException() { 
    super(); 
  }

  /**
   * Creates a new <code>UnassignedClassException</code> instance
   * with a specified message.
   *
   * @param messagae a <code>String</code> containing the message.
   */
  public UnassignedClassException(String message) { 
    super(message); 
  }
}
