

package org.jblas.ranges;

/**
 * A PointRange is a range which only has a single point.
 */
public class PointRange implements Range {
  private int value;
  private boolean consumed;

  /**
   * Construct a new PointRange with the one given index.
   */
  public PointRange(int v) {
    value = v;
  }

  public void init(int l, int u) {
    consumed = false;
  }

  public int length() {
    return 1;
  }

  public int value() {
    return value;
  }

  public int index() {
    return 0;
  }

  public void next() {
    consumed = true;
  }

  public boolean hasMore() {
    return !consumed;
  }

  @Override
  public String toString() {
    return String.format("<PointRange at=%d>", value);
  }
}
