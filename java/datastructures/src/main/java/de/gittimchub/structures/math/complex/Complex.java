package de.gittimchub.structures.math.complex;

/**
 * Immutable Komplexe Zahl
 * 
 * @author GittiMcHub
 */
public class Complex extends AbstractComplexNumber {

  private final double real;
  private final double imaginary;

  private Complex(double real, double imaginary) {
    this.real = real;
    this.imaginary = imaginary;
  }

  @Override
  public double getReal() {
    return this.real;
  }

  @Override
  public double getImaginary() {
    return this.imaginary;
  }

  @Override
  public ComplexNumber add(ComplexNumber c) {
    return new Complex(this.getReal() + c.getReal(), this.getImaginary() + c.getImaginary());
  }

  @Override
  public ComplexNumber subtract(ComplexNumber c) {
    return new Complex(this.getReal() - c.getReal(), this.getImaginary() - c.getImaginary());
  }

  @Override
  public ComplexNumber multiply(ComplexNumber c) {
    return new Complex(this.getReal() * c.getReal() - this.getImaginary() * c.getImaginary(),
        this.getReal() * c.getImaginary() + this.getImaginary() * c.getReal());
  }

  @Override
  public ComplexNumber divide(ComplexNumber c) {
    double real = (this.getReal() * c.getReal() + this.getImaginary() * c.getImaginary())
        / (Math.pow(c.getReal(), 2) + Math.pow(c.getImaginary(), 2));
    double imag = (this.getImaginary() * c.getReal() - this.getReal() * c.getImaginary())
        / (Math.pow(c.getReal(), 2) + Math.pow(c.getImaginary(), 2));

    return new Complex(real, imag);
  }

  @Override
  public ComplexNumber pow(int exponent) {
    ComplexNumber toMultiply = new Complex(this.getReal(), this.getImaginary());
    ComplexNumber result = new Complex(this.getReal(), this.getImaginary());

    for (int i = 1; i < exponent; i++) {
      result = result.multiply(toMultiply);
    }
    return result;
  }

  @Override
  public ComplexNumber absolute() {
    return new Complex(Math.abs(this.getReal()), Math.abs(this.getImaginary()));
  }

  @Override
  public ComplexNumber negate() {
    return new Complex(Math.abs(this.getReal()) * -1, Math.abs(this.getImaginary()) * -1);
  }

  @Override
  public ComplexNumber conjugate() {
    return new Complex(this.getReal(), this.getImaginary() * -1);
  }

  /**
   * Erzeugt eine Komplexe Zahl mit Polarkoordinaten
   * 
   * @param absoluteAmount
   *          double - Betrag von z
   * @param phi
   *          double - (Winkel)
   * @return new ComplexNumber
   */
  public static Complex valueOfPolar(double absoluteAmount, double phi) {

    // Falls ein negativer Winkel angegeben wird
    phi = phi > 0 ? (phi % 360) : (360 + (phi % 360));

    double cReal = absoluteAmount * Math.cos(Math.toRadians(phi));
    double cImaginary = absoluteAmount * Math.sin(Math.toRadians(phi));

    return new Complex(cReal, cImaginary);
  }

  /**
   * Erzeugt eine Komplexe Zahl aus Kartesischen Koordinaten
   * 
   * @param x
   *          double - Realanteil
   * @param y
   *          double - Imaginaeranteil
   * @return new ComplexNumber
   */
  public static Complex valueOfCartesian(double x, double y) {
    return new Complex(x, y);
  }

}
