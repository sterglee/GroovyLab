package  wekaCore;


/**
 * <code>WekaException</code> is used when some Weka-specific
 * checked exception must be raised.
 *
 */
public class WekaException extends Exception {

  /**
   * Creates a new <code>WekaException</code> instance
   * with no detail message.
   */
  public WekaException() { 
    super(); 
  }

  /**
   * Creates a new <code>WekaException</code> instance
   * with a specified message.
   *
   * @param messagae a <code>String</code> containing the message.
   */
  public WekaException(String message) { 
    super(message); 
  }
}
