package de.gittimchub.structures.math.complex;

/**
 * Mutable Komplexe Zahl
 * 
 * @author GittiMcHub
 */
public class MutableComplex extends AbstractComplexNumber {

  private double real;
  private double imaginary;

  public MutableComplex(double real, double imaginary) {
    this.real = real;
    this.imaginary = imaginary;
  }

  @Override
  public double getReal() {
    return real;
  }

  /**
   * Realanteil setzen
   * 
   * @param real
   *          double
   */
  public void setReal(double real) {
    this.real = real;
  }

  @Override
  public double getImaginary() {
    return imaginary;
  }

  /**
   * Imaginaeranteil setzen
   * 
   * @param imaginary
   *          double
   */
  public void setImaginary(double imaginary) {
    this.imaginary = imaginary;
  }

  @Override
  public ComplexNumber add(ComplexNumber c) {
    this.real = this.getReal() + c.getReal();
    this.imaginary = this.getImaginary() + c.getImaginary();
    return this;
  }

  @Override
  public ComplexNumber subtract(ComplexNumber c) {
    this.real = this.getReal() - c.getReal();
    this.imaginary = this.getImaginary() - c.getImaginary();
    return this;
  }

  @Override
  public ComplexNumber multiply(ComplexNumber c) {
    double newReal = this.getReal() * c.getReal() - this.getImaginary() * c.getImaginary();
    double newImag = this.getReal() * c.getImaginary() + this.getImaginary() * c.getReal();
    this.real = newReal;
    this.imaginary = newImag;
    return this;
  }

  @Override
  public ComplexNumber divide(ComplexNumber c) {
    double newReal = (this.getReal() * c.getReal() + this.getImaginary() * c.getImaginary())
        / (Math.pow(c.getReal(), 2) + Math.pow(c.getImaginary(), 2));
    double newImag = (this.getImaginary() * c.getReal() - this.getReal() * c.getImaginary())
        / (Math.pow(c.getReal(), 2) + Math.pow(c.getImaginary(), 2));
    this.real = newReal;
    this.imaginary = newImag;
    return this;
  }

  @Override
  public ComplexNumber pow(int exponent) {
    ComplexNumber forMultiply = new MutableComplex(this.real, this.imaginary);

    for (int i = 1; i < exponent; i++) {
      this.multiply(forMultiply);

    }
    return this;
  }

  @Override
  public ComplexNumber absolute() {
    this.real = Math.abs(this.getReal());
    this.imaginary = Math.abs(this.getImaginary());
    return this;
  }

  @Override
  public ComplexNumber conjugate() {
    this.imaginary = this.getImaginary() * -1;
    return this;
  }

  @Override
  public ComplexNumber negate() {
    this.real = Math.abs(this.getReal()) * -1;
    this.imaginary = Math.abs(this.getImaginary()) * -1;
    return this;
  }

  /**
   * Liefert eine Komplexe Zahl aus Polar Koordinaten
   * 
   * @param absoluteAmount
   *          double - Betrag von z
   * @param phi
   *          double - Winkel phi
   * @return MutableComplex
   */
  public static MutableComplex valueOfPolar(double absoluteAmount, double phi) {
    // Falls ein negativer Winkel angegeben wird
    phi = phi > 0 ? (phi % 360) : (360 + (phi % 360));

    double cReal = absoluteAmount * Math.cos(Math.toRadians(phi));
    double cImaginary = absoluteAmount * Math.sin(Math.toRadians(phi));

    return new MutableComplex(cReal, cImaginary);
  }

  /**
   * Erzeugt eine Komplexe Zahl aus Kartesischen Koordinaten
   * 
   * @param x
   * @param y
   * @return MutableComplex
   */
  public static MutableComplex valueOfCartesian(double x, double y) {
    return new MutableComplex(x, y);
  }

}
