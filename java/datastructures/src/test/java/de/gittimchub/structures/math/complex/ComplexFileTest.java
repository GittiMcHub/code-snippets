package de.gittimchub.structures.math.complex;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


/**
 * Test Klasse -fuer ComplexFile
 * 
 * @author GittiMcHub
 *
 */
class ComplexFileTest {

  private static String filenameThousands;
  private static String filenameSorted;
  private static ComplexFile thousandsFile;
  private static ComplexFile sortedFile;

  @BeforeAll
  static void initAll() {

    Date d = Calendar.getInstance().getTime();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    String timestamp = sdf.format(d);

    filenameThousands = System.getProperty("java.io.tmpdir") + File.separator + "complex-thousands " + timestamp + ".txt";
    filenameSorted = System.getProperty("java.io.tmpdir") + File.separator + "complex-sorted " + timestamp + ".txt";

    thousandsFile = new ComplexFile(filenameThousands);
    sortedFile = new ComplexFile(filenameSorted);
  }

  @AfterAll
  static void cleanup() {
    // Loescht die Temp Dateien
    try {
      Files.delete(Paths.get(filenameThousands));
      Files.delete(Paths.get(filenameSorted));
    } catch (IOException e) { // Fehler sind hier egal
    }
  }

  @Test
  void testGenerateThousand() {
    Complex c = Complex.valueOfCartesian(-0.7, 0.2);

    assertEquals(1000, thousandsFile.generateThousand().size());
    // Die erste Zahl sollte n = 0 sein
    assertEquals(Complex.valueOfCartesian(0, 0), thousandsFile.generateThousand().get(0));
    // Danach = c
    assertEquals(c, thousandsFile.generateThousand().get(1));
    // dann z^2+c
    assertEquals(c.pow(2).add(c), thousandsFile.generateThousand().get(2));
  }

  @Test
  void testWrite() {
    // Datei sollte erzeugt werden wenn noch nicht vorhande
    assertTrue(Files.exists(Paths.get(filenameThousands)));

    try {
      // Aber sollte leer sein
      assertTrue(Files.readAllLines(Paths.get(filenameThousands)).isEmpty());
      // 1000 Zahlen generieren und schreiben
      thousandsFile.setContent(thousandsFile.generateThousand());
      thousandsFile.write();
      // Sollten jetzt 1000 drin sein
      assertEquals(1000, Files.readAllLines(Paths.get(filenameThousands)).size());

    } catch (IOException e) {
      // Test fehlschlagen lassen da Exception
      fail();
    }
  }

  @Test
  void testRead() {

    // Daten merken, dann schreiben
    ArrayList<ComplexNumber> writtenContent = thousandsFile.generateThousand();
    thousandsFile.setContent(writtenContent);
    thousandsFile.write();

    // ArrayLeeren
    thousandsFile.setContent(new ArrayList<ComplexNumber>());
    assertTrue(thousandsFile.getContent().isEmpty());
    // Neu einlesen
    thousandsFile.read();
    // Der Inhalt muesste gleich dem vorher geschirbenen Inhalt sein
    assertEquals(writtenContent, thousandsFile.getContent());
  }

  @Test
  void testSort() {
    // Zahlen generieren, sortieren und schreiben fuer Testzwecke
    sortedFile.setContent(sortedFile.generateThousand());
    sortedFile.sort();
    sortedFile.write();
    // Bei der Definition von "generateThousand" muesste 0.0 + 0.0i am Anfang und -0.7 + 0.2i am
    // Ende stehen
    assertEquals(Complex.valueOfCartesian(0, 0), sortedFile.getContent().get(0));
    assertEquals(Complex.valueOfCartesian(-0.7, 0.2),
        sortedFile.getContent().get(sortedFile.getContent().size() - 1));
  }

}
