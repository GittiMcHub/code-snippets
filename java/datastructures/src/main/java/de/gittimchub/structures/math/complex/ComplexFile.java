package de.gittimchub.structures.math.complex;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Eine Klasse zum Speichern und Lesen von Dateien mit Komplexen Zahlen
 * 
 * @author GittiMcHub
 */
public class ComplexFile {

  private final String filePath;
  private ArrayList<ComplexNumber> content;

  public ComplexFile(String filePath) {
    this.filePath = filePath;
    this.content = this.read();
  }

  public void setContent(ArrayList<ComplexNumber> cs) {
    this.content = cs;
  }

  public ArrayList<ComplexNumber> getContent() {
    return this.content;
  }
  
  public void sort() {
    Collections.sort(this.content);
  }

  public ArrayList<ComplexNumber> generateThousand() {

    ArrayList<ComplexNumber> thousand = new ArrayList<>();

    ComplexNumber z = Complex.valueOfCartesian(0, 0);
    ComplexNumber c = Complex.valueOfCartesian(-0.7, 0.2);
    thousand.add(z);
    for (int n = 1; n < 1000; n++) {
      z = z.pow(2).add(c);
      thousand.add(z);
    }

    return thousand;
  }

  /**
   * Schreibt complexStorage Zeile fuer Zeile in die Datei Gefunden auf:
   * https://examples.javacodegeeks.com/core-java/nio/file-nio/java-nio-write-file-example/
   */
  public void write() {
    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(this.filePath),Charset.forName("UTF-8"))){

      for (ComplexNumber cn : this.content) {
        writer.write(cn.toString() + "\n");
      }
      writer.flush();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public ArrayList<ComplexNumber> read() {

    ArrayList<ComplexNumber> content = new ArrayList<>();
    Files.exists(Paths.get(this.filePath));
    
    try {
      if (!Files.exists(Paths.get(this.filePath))) {
        Files.createFile(Paths.get(this.filePath));
      }
      
      List<String> lines = Files.readAllLines(Paths.get(this.filePath));

      // Pruef Ausdruck, ob es sich um eine Komplexe Zahl handelt
      // Alle moeglichen Komplexen Zahlen:
      // [-]?[\d]+(\.[\d]+)?[\s]{0,1}[+-]{1}[\s]{0,1}[-]?[\d]+(\.[\d]+)?[\s]{0,1}[ij]
      // Nur die aus diesem Programm:
      // [-]?[\d]+(\.[\d]+){1}[\s]{1}[+]{1}[\s]{1}[-]?[\d]+(\.[\d]+){1}[i]
      // z.B.: -1.1 + -2.2i oder 1.1 + 2.2i
      Pattern p = Pattern
          .compile("[-]?[\\d]+(\\.[\\d]+){1}[\\s]{1}[+]{1}[\\s]{1}[-]?[\\d]+(\\.[\\d]+){1}[i]");

      for (String s : lines) {
        Matcher matcher = p.matcher(s);

        if (matcher.find()) {
          String foundComplexNumber = matcher.group(0);
          if (foundComplexNumber != null) {
            content.add(complexFromString(foundComplexNumber));
          }
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    this.content = content;
    return content;
  }

  private ComplexNumber complexFromString(String cn) {

    // Pattern Zahlen zu extrahieren
    Pattern p = Pattern.compile("([-]?[\\d]+(\\.[\\d]+)?)");
    Matcher matcher = p.matcher(cn);

    double real = 0.0;
    double imag = 0.0;

    // Erste Zahl
    if (matcher.find()) {
      real = Double.valueOf(matcher.group(0));
    }
    // Zweite Zahl
    if (matcher.find()) {
      imag = Double.valueOf(matcher.group(0));
    }
    return Complex.valueOfCartesian(real, imag);
  }

}
