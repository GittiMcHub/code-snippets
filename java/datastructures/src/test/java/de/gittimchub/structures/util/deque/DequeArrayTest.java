package de.gittimchub.structures.util.deque;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author GittiMcHub
 */
public class DequeArrayTest {


    private static String element1;
    private static String element2;
    private static String element3;
    private static String element4;
    private static String element5;

    /**
     * Test Array aufbauen und Test Predicates definieren
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
        DequeArray<String> d = new DequeArray<String>();
        d.push(element1);
        d.push(element2);
        d.push(element3);
        d.push(element4);
        d.push(element5);

        // das zuletzt reingepackte Element sollte zurueckkommen
        assertEquals(element5, d.peekLast());

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
        DequeArray<String> d = new DequeArray<String>();
        d.enqueue(element1);
        d.enqueue(element2);
        d.enqueue(element3);
        d.enqueue(element4);
        d.enqueue(element5);

        // das zuerst reingepackte Element sollte zurueckkommen
        assertEquals(element1, d.peekFirst());
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
