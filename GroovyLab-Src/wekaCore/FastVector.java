package  wekaCore;

import java.util.*;
import java.io.*;


public class FastVector implements Copyable, Serializable {

  /**
   * Class for enumerating the vector's elements.
   */
  public class FastVectorEnumeration implements Enumeration {

    /** The counter. */
    private int m_Counter;

    /** The vector. */
    private FastVector m_Vector;

    /** Special element. Skipped during enumeration. */
    private int m_SpecialElement;

    /**
     * Constructs an enumeration.
     *
     * @param vector the vector which is to be enumerated
     */
    public FastVectorEnumeration(FastVector vector) {

      m_Counter = 0;
      m_Vector = vector;
      m_SpecialElement = -1;
    }

    /**
     * Constructs an enumeration with a special element.
     * The special element is skipped during the enumeration.
     *
     * @param vector the vector which is to be enumerated
     * @param special the index of the special element
     */
    public FastVectorEnumeration(FastVector vector, int special) {

      m_Vector = vector;
      m_SpecialElement = special;
      if (special == 0) {
	m_Counter = 1;
      } else {
	m_Counter = 0;
      }
    }


    /**
     * Tests if there are any more elements to enumerate.
     *
     * @return true if there are some elements left
     */
    public final boolean hasMoreElements() {

      if (m_Counter < m_Vector.size()) {
	return true;
      }
      return false;
    }

    /**
     * Returns the next element.
     *
     * @return the next element to be enumerated
     */
    public final Object nextElement() {
  
      Object result = m_Vector.elementAt(m_Counter);

      m_Counter++;
      if (m_Counter == m_SpecialElement) {
	m_Counter++;
      }
      return result;
    }
  }

  /** The array of objects. */
  private Object[] m_Objects;

  /** The current size; */
  private int m_Size;

  /** The capacity increment */
  private int m_CapacityIncrement;
  
  /** The capacity multiplier. */
  private double m_CapacityMultiplier;

  /**
   * Constructs an empty vector with initial
   * capacity zero.
   */
  public FastVector() {
  
    m_Objects = new Object[0];
    m_Size = 0;
    m_CapacityIncrement = 1;
    m_CapacityMultiplier = 2;
  }

  /**
   * Constructs a vector with the given capacity.
   *
   * @param capacity the vector's initial capacity
   */
  public FastVector(int capacity) {

    m_Objects = new Object[capacity];
    m_Size = 0;
    m_CapacityIncrement = 1;
    m_CapacityMultiplier = 2;
  }

  /**
   * Constructs a vector with the given capacity, capacity 
   * increment and capacity mulitplier.
   *
   * @param capacity the vector's initial capacity
   */
  public FastVector(int capacity, int capIncrement, 
		    double capMultiplier) {

    m_Objects = new Object[capacity];
    m_Size = 0;
    m_CapacityIncrement = capIncrement;
    m_CapacityMultiplier = capMultiplier;
  }

  /**
   * Adds an element to this vector. Increases its
   * capacity if its not large enough.
   *
   * @param element the element to add
   */
  public final void addElement(Object element) {

    Object[] newObjects;

    if (m_Size == m_Objects.length) {
      newObjects = new Object[(int)m_CapacityMultiplier *
			     (m_Objects.length +
			      m_CapacityIncrement)];
      System.arraycopy(m_Objects, 0, newObjects, 0, m_Size);
      m_Objects = newObjects;
    }
    m_Objects[m_Size] = element;
    m_Size++;
  }

  /**
   * Returns the capacity of the vector.
   *
   * @return the capacity of the vector
   */
  public final int capacity() {
  
    return m_Objects.length;
  }

  /**
   * Produces a shallow copy of this vector.
   *
   * @return the new vector
   */
  public final Object copy() {

    FastVector copy = new FastVector(m_Objects.length, 
				     m_CapacityIncrement,
				     m_CapacityMultiplier);
    copy.m_Size = m_Size;
    System.arraycopy(m_Objects, 0, copy.m_Objects, 0, m_Size);
    return copy;
  }

  /**
   * Clones the vector and shallow copies all its elements.
   * The elements have to implement the Copyable interface.
   * 
   * @return the new vector
   */
  public final Object copyElements() {

    FastVector copy = new FastVector(m_Objects.length, 
				     m_CapacityIncrement,
				     m_CapacityMultiplier);
    copy.m_Size = m_Size;
    for (int i = 0; i < m_Size; i++) {
      copy.m_Objects[i] = ((Copyable)m_Objects[i]).copy();
    }
    return copy;
  }

  /**
   * Returns the element at the given position.
   *
   * @param index the element's index
   * @return the element with the given index
   */
  public final Object elementAt(int index) {

    return m_Objects[index];
  }

  /**
   * Returns an enumeration of this vector.
   *
   * @return an enumeration of this vector
   */
  public final Enumeration elements() {
  
    return new FastVectorEnumeration(this);
  }

  /**
   * Returns an enumeration of this vector, skipping the
   * element with the given index.
   *
   * @param index the element to skip
   * @return an enumeration of this vector
   */
  public final Enumeration elements(int index) {
  
    return new FastVectorEnumeration(this, index);
  }

  /**
   * Returns the first element of the vector.
   *
   * @return the first element of the vector
   */
  public final Object firstElement() {

    return m_Objects[0];
  }

  /**
   * Searches for the first occurence of the given argument, 
   * testing for equality using the equals method. 
   *
   * @param element the element to be found
   * @return the index of the first occurrence of the argument 
   * in this vector; returns -1 if the object is not found
   */
  public final int indexOf(Object element) {

    for (int i = 0; i < m_Size; i++) {
      if (element.equals(m_Objects[i])) {
	return i;
      }
    }
    return -1;
  }

  /**
   * Inserts an element at the given position.
   *
   * @param element the element to be inserted
   * @param index the element's index
   */
  public final void insertElementAt(Object element, int index) {

    Object[] newObjects;

    if (m_Size < m_Objects.length) {
      for (int i = m_Size - 1; i >= index; i--) {
	m_Objects[i + 1] = m_Objects[i];
      }
      m_Objects[index] = element;
    } else {
      newObjects = new Object[(int)m_CapacityMultiplier *
			     (m_Objects.length +
			      m_CapacityIncrement)];
      System.arraycopy(m_Objects, 0, newObjects, 0, index);
      newObjects[index] = element;
      System.arraycopy(m_Objects, index, newObjects, index + 1,
		       m_Size - index);
      m_Objects = newObjects;
    }
    m_Size++;
  }

  /**
   * Returns the last element of the vector.
   *
   * @return the last element of the vector
   */
  public final Object lastElement() {

    return m_Objects[m_Size - 1];
  }

  /**
   * Deletes an element from this vector.
   *
   * @param index the index of the element to be deleted
   */
  public final void removeElementAt(int index) {

    Object[] newObjects = new Object[m_Objects.length];

    System.arraycopy(m_Objects, 0, newObjects, 0, index);
    System.arraycopy(m_Objects, index + 1, newObjects,
		     index, m_Objects.length - (index + 1));
    m_Objects = newObjects;
    m_Size--;
  }

  /**
   * Removes all components from this vector and sets its 
   * size to zero. 
   */
  public final void removeAllElements() {

    m_Objects = new Object[m_Objects.length];
    m_Size = 0;
  }

  /**
   * Sets the vector's capacity to the given value.
   *
   * @param capacity the new capacity
   */
  public final void setCapacity(int capacity) {

    Object[] newObjects = new Object[capacity];
   
    System.arraycopy(m_Objects, 0, newObjects, 0, capacity);
    m_Objects = newObjects;
    if (m_Objects.length < m_Size)
      m_Size = m_Objects.length;
  }

  /**
   * Sets the element at the given index.
   *
   * @param element the element to be put into the vector
   * @param index the index at which the element is to be placed
   */
  public final void setElementAt(Object element, int index) {

    m_Objects[index] = element;
  }

  /**
   * Returns the vector's current size.
   *
   * @return the vector's current size
   */
  public final int size() {

    return m_Size;
  }

  /**
   * Swaps two elements in the vector.
   *
   * @param first index of the first element
   * @param second index of the second element
   */
  public final void swap(int first, int second) {

    Object help = m_Objects[first];

    m_Objects[first] = m_Objects[second];
    m_Objects[second] = help;
  }

  /**
   * Sets the vector's capacity to its size.
   */
  public final void trimToSize() {

    Object[] newObjects = new Object[m_Size];
    
    System.arraycopy(m_Objects, 0, newObjects, 0, m_Size);
    m_Objects = newObjects;
  }
}
