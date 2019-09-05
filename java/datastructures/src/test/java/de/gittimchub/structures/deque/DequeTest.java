package de.gittimchub.structures.deque;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author GittiMcHub
 */
public class DequeTest {


    private static String element1;
    private static String element2;
    private static String element3;
    private static String element4;
    private static String element5;

    /**
     * Test Array Aufbauen und Test Predicates definieren
     */
    @BeforeAll
    static void initAll() {
        element1 = "Element 1";
        element2 = "Element 2";
        element3 = "Element 3";
        element4 = "Element 4";
        element5 = "Element 5";
    }

    /*
    Test last in last out
     */
    @Test
    void testDequeAsStack() {
        Deque<String> d = new Deque<String>();
        d.push(element1);
        d.push(element2);
        d.push(element3);
        d.push(element4);
        d.push(element5);

        // das zuletzt reingepackte Element sollte zurueckkommen
        assertEquals(element5, d.peekLast().getContent());
        // Das vorletz reingepackte Element muesste dann Element 4 entsprechen
        assertTrue(d.peekLast().getPreviousElement().getContent().equals(element4));
        // Das Letzte Element sollte kein Nachfolger haben
        assertTrue(d.peekLast().getNextElement() == null);

        // Fehlerhafte Eingaben pruefen
        Executable nullArgumentCode = () -> d.push(null);
        Assertions.assertThrows(IllegalArgumentException.class, nullArgumentCode);


        // Elemente wieder wegnehmen
        d.pop().pop().pop().pop().pop();
        assertTrue(d.isEmpty());
        // Dann versuchen ein element rauszuholen -> sollte exception werfen
        Executable noMoreElementsCode = () -> d.pop();
        Assertions.assertThrows(NullPointerException.class, noMoreElementsCode);
    }

    /*
    Test first in first out
     */
    @Test
    void testDequeAsQueue() {
        Deque<String> d = new Deque<String>();
        d.enqueue(element1);
        d.enqueue(element2);
        d.enqueue(element3);
        d.enqueue(element4);
        d.enqueue(element5);

        // das zuerst reingepackte Element sollte zurueckkommen
        assertEquals(element1, d.peekFirst().getContent());
        // Das als zweites reingepackte Element muesste dann Element 2 entsprechen
        assertTrue(d.peekFirst().getNextElement().getContent().equals(element2));
        // Das erste Element sollte keinen Vorgaenger haben
        assertTrue(d.peekFirst().getPreviousElement() == null);

        // Fehlerhafte Eingaben pruefen
        Executable nullArgumentCode = () -> d.enqueue(null);
        Assertions.assertThrows(IllegalArgumentException.class, nullArgumentCode);


        // Elemente wieder wegnehmen
        d.deque().deque().deque().deque().deque();
        assertTrue(d.isEmpty());
        // Dann versuchen ein element rauszuholen -> sollte exception werfen
        Executable noMoreElementsCode = () -> d.deque();
        Assertions.assertThrows(NullPointerException.class, noMoreElementsCode);
    }
}
