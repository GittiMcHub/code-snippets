package de.gittimchub.structures.math.complex;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComplexTest {
    private static double aReal;
    private static double aImag;
    private static double bReal;
    private static double bImag;

    private static Complex a;
    private static Complex b;

    @BeforeAll
    static void initAll() {
        aReal = 5.0;
        aImag = 6.0;
        bReal = -3.0;
        bImag = 4.0;

        a = Complex.valueOfCartesian(aReal, aImag);
        b = Complex.valueOfCartesian(bReal, bImag);

    }

    @Test
    void testComplex() {
        assertNotNull(a);
        assertTrue(a instanceof Complex);
        assertTrue(a instanceof ComplexNumber);
    }

    @Test
    void testGetReal() {
        assertEquals(aReal, a.getReal());
    }

    @Test
    void testGetImaginary() {
        assertEquals(aImag, a.getImaginary());
    }

    @Test
    void testAdd() {
        assertEquals(a.add(b).getReal(), 2.0);
        assertEquals(a.add(b).getImaginary(), 10.0);
    }

    @Test
    void testSubtract() {
        assertEquals(a.subtract(b).getReal(), 8.0);
        assertEquals(a.subtract(b).getImaginary(), 2.0);
    }

    @Test
    void testMultiply() {
        assertEquals(a.multiply(b).getReal(), -39.0);
        assertEquals(a.multiply(b).getImaginary(), 2.0);

        assertEquals(b.multiply(a).getReal(), -39.0);
        assertEquals(b.multiply(a).getImaginary(), 2.0);
    }

    @Test
    void testDivide() {
        assertEquals(a.divide(b).getReal(), 0.36);
        assertEquals(a.divide(b).getImaginary(), -1.52);

        assertEquals(b.divide(a).getReal(), 9.0 / 61.0);
        assertEquals(b.divide(a).getImaginary(), 38.0 / 61.0);
    }

    @Test
    void testPow() {
        assertEquals(a.pow(3).getReal(), a.multiply(a).multiply(a).getReal());
        assertEquals(a.pow(3).getImaginary(), a.multiply(a).multiply(a).getImaginary());
    }

    @Test
    void testAbsolute() {
        assertEquals(aReal, a.absolute().getReal());
        assertEquals(aImag, a.absolute().getImaginary());

        assertEquals(bReal * -1, b.absolute().getReal());
        assertEquals(bImag, b.absolute().getImaginary());
    }

    @Test
    void testNegate() {
        assertEquals(aReal * -1, a.negate().getReal());
        assertEquals(aImag * -1, a.negate().getImaginary());

        assertEquals(bReal, b.negate().getReal());
        assertEquals(bImag * -1, b.negate().getImaginary());
    }

    @Test
    void testConjugate() {
        assertEquals(aReal, a.conjugate().getReal());
        assertEquals(aImag * -1, a.conjugate().getImaginary());

        assertEquals(bReal, b.conjugate().getReal());
        assertEquals(bImag * -1, b.conjugate().getImaginary());
    }

    @Test
    void testGetAbsoluteAmount() {
        assertEquals(a.getAbsoluteAmount(), 7.810249675906654);

    }

    @Test
    void testGetSinPhi() {
        // Sin(a) = Gegenkathete / Hypotenuse
        assertEquals(a.getSinPhi(), Math.sin(a.getImaginary() / a.getAbsoluteAmount()));
    }

    @Test
    void testGetCosPhi() {
        // Cos(a) Ankathete / Hypotenuse
        assertEquals(a.getCosPhi(), Math.cos(a.getReal() / a.getAbsoluteAmount()));
    }

    @Test
    void testGetTanPhi() {
        // Tan(a) = Gegenkathete / Ankathete
        assertEquals(a.getTanPhi(), Math.tan(a.getImaginary() / a.getReal()));
    }

    @Test
    void testGetPhi() {
        assertEquals(a.getPhi(), Math.toDegrees(Math.atan(a.getImaginary() / a.getReal())));
    }

    @Test
    void testEqualsObject() {
        assertTrue(a.equals(Complex.valueOfCartesian(5.0, 6.0)));
    }

    @Test
    void testValueOfPolar() {
        ComplexNumber aFromPolar = Complex.valueOfPolar(a.getAbsoluteAmount(), a.getPhi());
        // +- 0.1 Rundungsdifferenzen mit Doubles
        assertTrue(
                aFromPolar.getReal() >= (a.getReal() - 0.1) && aFromPolar.getReal() <= (a.getReal() + 0.1));
        assertTrue(aFromPolar.getImaginary() >= (a.getImaginary() - 0.1)
                && aFromPolar.getImaginary() <= (a.getImaginary() + 0.1));

        ComplexNumber bFromPolar = Complex.valueOfPolar(b.getAbsoluteAmount(), b.getPhi());
        assertTrue(
                bFromPolar.getReal() >= (b.getReal() - 0.1) && bFromPolar.getReal() <= (b.getReal() + 0.1));
        assertTrue(bFromPolar.getImaginary() >= (b.getImaginary() - 0.1)
                && bFromPolar.getImaginary() <= (b.getImaginary() + 0.1));
    }

    @Test
    void testValueOfCartesian() {
        assertTrue(a.equals(Complex.valueOfCartesian(aReal, aImag)));
        assertTrue(b.equals(Complex.valueOfCartesian(bReal, bImag)));
    }

    @Test
    void testToString() {
        assertEquals("5.0 + 6.0i", a.toString());
    }

    @Test
    void testGetRadius() {
        assertEquals(a.getAbsoluteAmount(), a.getRadius());
    }

    @Test
    void testGetAmountOfZ() {
        assertEquals(a.getAbsoluteAmount(), a.getAmountOfZ());
    }

    @Test
    void testHashCode() {
        assertEquals(a.hashCode(), a.hashCode());
        assertEquals(a.hashCode(), Complex.valueOfCartesian(aReal, aImag).hashCode());
        assertNotEquals(b.hashCode(), a.hashCode());

    }
}