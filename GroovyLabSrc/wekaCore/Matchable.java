package  wekaCore;
/** 
 * Interface to something that can be matched with tree matching
 * algorithms.
 *
  */
public interface Matchable {

  /**
   * Returns a string that describes a tree representing
   * the object in prefix order.
   *
   * @return the tree described as a string
   * @exception Exception if the tree can't be computed
   */
  public String prefix() throws Exception;
}








