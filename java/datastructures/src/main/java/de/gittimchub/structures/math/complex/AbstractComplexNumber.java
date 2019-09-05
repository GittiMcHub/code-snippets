package de.gittimchub.structures.math.complex;

/**
 * Abstrakte Klasse fuer eine Komplexe Zahl
 *
 * @author GittiMcHub
 *adff
 */
public abstract class AbstractComplexNumber implements ComplexNumber {

  @Override
  public double getAbsoluteAmount() {
    return Math.sqrt(Math.pow(this.getReal(), 2) + Math.pow(this.getImaginary(), 2));
  }

  public double getSinPhi() {
    return Math.sin(this.getImaginary() / this.getAbsoluteAmount());
  }

  public double getCosPhi() {
    return Math.cos(this.getReal() / this.getAbsoluteAmount());
  }

  public double getTanPhi() {
    return Math.tan(this.getImaginary() / this.getReal());
  }

  public double getPhi() {
    // Quadrant I
    if (this.getReal() > 0 && this.getImaginary() > 0) {
      return Math.toDegrees(Math.atan(this.getImaginary() / this.getReal()));
    }
    // Quadrant II
    if (this.getReal() < 0 && this.getImaginary() > 0) {
      return 180 - Math.toDegrees(Math.abs(Math.atan(this.getImaginary() / this.getReal())));
    }
    // Quadrant III
    if (this.getReal() < 0 && this.getImaginary() < 0) {
      return 180 + Math.toDegrees(Math.abs(Math.atan(this.getImaginary() / this.getReal())));
    }
    // Quadrant VI
    if (this.getReal() > 0 && this.getImaginary() < 0) {
      return 360 - Math.toDegrees(Math.abs(Math.atan(this.getImaginary() / this.getReal())));
    }
    return 0.0;
  }

  /**
   * Alias fuer getAbsoluteAmount
   * 
   * @return
   */
  public double getRadius() {
    return this.getAbsoluteAmount();
  }

  /**
   * Alias fuer getAbsoluteAmount
   * 
   * @return
   */
  public double getAmountOfZ() {
    return this.getAbsoluteAmount();
  }

  @Override
  public String toString() {
    return this.getReal() + " + " + this.getImaginary() + "i";
    // return this.getReal() + " + " + this.getImaginary() + "i (|z|=" + this.getAbsoluteAmount() +
    // ")";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof ComplexNumber))
      return false;

    ComplexNumber c = (ComplexNumber) o;

    return c.getReal() == this.getReal() && c.getImaginary() == this.getImaginary();
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = (int) (37 * result + (this.getReal() * 100));
    result = (int) (37 * result + (this.getImaginary() * 100));

    return result;
  }

  /**
   * Vergleicht auf Basis des Betrags von z (|z|), welche von Beiden Groesser ist
   * 
   * @return int -1, 0, 1
   */
  @Override
  public int compareTo(ComplexNumber cn) {
    return Double.compare(this.getAbsoluteAmount(), cn.getAbsoluteAmount());
  }

}
