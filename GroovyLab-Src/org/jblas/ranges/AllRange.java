

package org.jblas.ranges;

/**
 * A range over all available indices. Can be used to address whole columns or rows. Like
 * the ":" index in matlab. Don't forget to call init() before using this range.
 */
public class AllRange implements Range {
  private int lower;
  private int upper;
  private int value;
  private int counter;

  public AllRange() {
  }

  public void init(int l, int u) {
    lower = l;
    upper = u;
    value = l;
    counter = 0;
  }

  public int length() {
    return upper - lower;
  }

  public int value() {
    return value;
  }

  public int index() {
    return counter;
  }

  public void next() {
    counter++;
    value++;
  }

  public boolean hasMore() {
    return value < upper;
  }

  @Override
  public String toString() {
    return String.format("<AllRange from %d to %d, with length %d, index=%d, value=%d>", lower, upper, length(), index(), value());
  }
}
