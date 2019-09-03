
package org.jblas.ranges;

/**
 * Range which varies from a given interval. Endpoint is exclusive!
 * <p/>
 * "new IntervalRange(0, 3)" enumerates 0, 1, 2.
 */
public class IntervalRange implements Range {
  private int start;
  private int end;
  private int value;

  /**
   * Construct new interval range. Endpoints are inclusive.
   */
  public IntervalRange(int a, int b) {
    start = a;
    end = b;
  }

  public void init(int lower, int upper) {
    value = start;
    if (start < lower || end > upper + 1) {
      throw new IllegalArgumentException("Bounds " + lower + " to " + upper + " are beyond range interval " + start + " to " + end + ".");
    }
  }

  public int length() {
    return end - start;
  }

  public void next() {
    value++;
  }

  public int index() {
    return value - start;
  }

  public int value() {
    return value;
  }

  public boolean hasMore() {
    return value < end;
  }

  @Override
  public String toString() {
    return String.format("<Interval Range from %d to %d, length %d index=%d value=%d>", start, end, length(), index(), value());
  }
}
