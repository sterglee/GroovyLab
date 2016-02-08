package  wekaCore;

public class UnassignedDatasetException extends RuntimeException {

  /**
   * Creates a new <code>UnassignedDatasetException</code> instance
   * with no detail message.
   */
  public UnassignedDatasetException() { 
    super(); 
  }

  /**
   * Creates a new <code>UnassignedDatasetException</code> instance
   * with a specified message.
   *
   * @param messagae a <code>String</code> containing the message.
   */
  public UnassignedDatasetException(String message) { 
    super(message); 
  }
}
