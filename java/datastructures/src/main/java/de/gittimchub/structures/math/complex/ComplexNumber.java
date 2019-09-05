package de.gittimchub.structures.math.complex;

/**
 * Interface fuer Komplexe Zahlen
 * 
 * @author GittiMcHub
 *
 */
public interface ComplexNumber extends Comparable<ComplexNumber> {
  /**
   * Realanteil der Komplexen Zahl
   * 
   * @return double
   */
  public double getReal();

  /**
   * Imaginaeranteil der Komplexen Zahl
   * 
   * @return double
   */
  public double getImaginary();

  /**
   * Addition mit einer Komplexen Zahlen
   * 
   * @param ComplexNumber
   *          Komplexe Zahl c
   * @return ComplexNumber + c
   */
  public ComplexNumber add(ComplexNumber c);

  /**
   * Subtraktion mit einer Komplexen Zahlen
   * 
   * @param ComplexNumber
   *          Komplexe Zahl c
   * @return ComplexNumber - c
   */
  public ComplexNumber subtract(ComplexNumber c);

  /**
   * Multiplikation mit einer Komplexen Zahlen
   * 
   * @param ComplexNumber
   *          Komplexe Zahl c
   * @return ComplexNumber * c
   */
  public ComplexNumber multiply(ComplexNumber c);

  /**
   * Division mit einer Komplexen Zahlen
   * 
   * @param ComplexNumber
   *          Komplexe Zahl c
   * @return ComplexNumber / c
   */
  public ComplexNumber divide(ComplexNumber c);

  /**
   * Potenz einer Komplexen Zahlen
   * 
   * @return ComplexNumber ^ exponent
   */
  public ComplexNumber pow(int exponent);

  /**
   * Betraege der Komplexen Zahl (+,+) => (+,+) (+,-) => (+,+) (-,+) => (+,+) (-,-) => (+,+)
   * 
   * @return ComplexNumber
   */
  public ComplexNumber absolute();

  /**
   * Konjugation der Komplexen Zahl
   * 
   * (+,+) => (+,-) (+,-) => (+,+) (-,+) => (-,-) (-,-) => (-,+)
   * 
   * @return ComplexNumber
   */
  public ComplexNumber conjugate();

  /**
   * Negation der Komplexen Zahl
   * 
   * (+,+) => (-,-) (+,-) => (-,-) (-,+) => (-,-) (-,-) => (-,-)
   * 
   * @return ComplexNumber
   */
  public ComplexNumber negate();

  /**
   * Betrag von z , |z| oder auch Radius
   * 
   * @return double
   */
  public double getAbsoluteAmount();

  /**
   * Sinus von Phi im Bogenmass
   * 
   * @return double
   
  public double getSinPhi();

  /**
   * Cosinus von Phi im Bogenmass
   * 
   * @return double
   
  public double getCosPhi();

  /**
   * Tangens von Phi im Bogenmass
   * 
   * @return double
   
  public double getTanPhi();

  /**
   * Phi in Winkelgrad
   * 
   * @return double
   
  public double getPhi();
  */
}
