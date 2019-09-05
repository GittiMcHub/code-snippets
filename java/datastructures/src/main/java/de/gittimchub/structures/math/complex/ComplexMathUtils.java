package de.gittimchub.structures.math.complex;



/**
 * 
 * Hilfsmethoden fuer Mathematische Operationen
 * 
 * @author GittiMcHub
 */
public class ComplexMathUtils {
  /**
   * Potenzieren einer komplexen Zahl mit einer natürlichen Zahl als Exponenten.
   * @param x Complex
   * @return MutableComplex
   */
  public static ComplexNumber pow(MutableComplex x) {
      double real;
      double imag;

      real = Math.exp(x.getReal() * Math.cos(x.getImaginary()));
      x.setReal(real);
      imag = Math.exp(x.getReal() * Math.sin(x.getImaginary()));
      x.setImaginary(imag);

      return x;
  }

  /**
   * Potenzieren einer komplexen Zahl mit einer natürlichen Zahl als Exponenten.
   * @param x Complex
   * @return Complex
   */
  public static ComplexNumber pow(Complex x) {
      double real;
      double imag;

      real = Math.exp(x.getReal() * Math.cos(x.getImaginary()));
      imag = Math.exp(x.getReal() * Math.sin(x.getImaginary()));

      return Complex.valueOfCartesian(real,imag);
  }

  /**
   * Berechnung des Sinuses einer komplexen Zahl.
   * @param x Complex
   * @return MutableComplex
   */
  public static ComplexNumber sinus(MutableComplex x){
      double real;
      double imag;

      real = Math.sin(x.getReal()) * Math.cosh(x.getImaginary());
      x.setReal(real);
      imag = Math.cos(x.getReal()) * Math.sinh(x.getImaginary());
      x.setImaginary(imag);

      return x;
  }

  /**
   * Berechnung des Sinuses einer komplexen Zahl.
   * @param x Complex
   * @return Complex
   */
  public static ComplexNumber sinus(Complex x){
      double real;
      double imag;

      real = Math.sin(x.getReal()) * Math.cosh(x.getImaginary());
      imag = Math.cos(x.getReal()) * Math.sinh(x.getImaginary());

      return Complex.valueOfCartesian(real,imag);
  }

  /**
   * Berechnung des Cosinuses einer komplexen Zahl.
   * @param x Complex
   * @return MutableComplex
   */
  public static ComplexNumber cosinus(MutableComplex x){
      double real;
      double imag;

      real = Math.cos(x.getReal()) * Math.cosh(x.getImaginary());
      x.setReal(real);
      imag = Math.sin(x.getReal()) * Math.sinh(x.getImaginary());
      x.setImaginary(imag);

      return x;
  }

  /**
   * Berechnung des Cosinuses einer komplexen Zahl.
   * @param x Complex
   * @return Complex
   */
  public static ComplexNumber cosinus(Complex x){
      double real;
      double imag;

      real = Math.cos(x.getReal()) * Math.cosh(x.getImaginary());
      imag = Math.sin(x.getReal()) * Math.sinh(x.getImaginary());

      return Complex.valueOfCartesian(real, imag);
  }

  /**
   * Berechnung des Tangens einer komplexen Zahl.
   * @param x Complex
   * @return MutableComplex
   */
  public static ComplexNumber tan(MutableComplex x){
      ComplexNumber sin;
      ComplexNumber cos;
      ComplexNumber div;

      sin = sinus(x);
      cos = cosinus(x);
      div = sin.divide(cos);

      x.setReal(div.getReal());
      x.setImaginary(div.getImaginary());

      return x;
  }

  /**
   * Berechnung des Tangens einer komplexen Zahl.
   * @param x Complex
   * @return Complex
   */
  public static ComplexNumber tan(Complex x){
      ComplexNumber sin;
      ComplexNumber cos;
      ComplexNumber div;

      sin = sinus(x);
      cos = cosinus(x);
      div = sin.divide(cos);

      return Complex.valueOfCartesian(div.getReal(), div.getImaginary());
  }
}
