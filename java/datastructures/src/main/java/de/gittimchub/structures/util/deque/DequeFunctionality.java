package de.gittimchub.structures.util.deque;

/**
 * Interface zur Beschreibung einer Deque Funktionalitaet
 * @param <T>
 *
 * @author GittiMcHub
 */
public interface DequeFunctionality<T> {

    /*
        Stack-artig
     */

    /**
     * Element "oben" auf das Deque legen
     * @param element
     * @throws IllegalArgumentException
     */
    public DequeFunctionality<?> push(T element) throws IllegalArgumentException;

    /**
     * Das "oberste" Element wegnehmen
     * @throws NullPointerException
     */
    public DequeFunctionality<?> pop() throws NullPointerException;

    /**
     * Das "oberste" Element zurueckgeben
     * @return
     */
    public Element<T> peekLast();


    /*
    Queue-artig
     */

    /**
     * Das Element "unter" das Deque Legen
     * @param element
     * @throws IllegalArgumentException
     */
    public DequeFunctionality<?> enqueue(T element) throws IllegalArgumentException;

    /**
     * Das "untereste" Element  wegnehmen
     * @throws NullPointerException
     */
    public DequeFunctionality<?> deque() throws NullPointerException;

    /**
     * Das "unterste" Element zurueckgeben
     * @return
     */
    public Element<T> peekFirst();

    /*
    Generelles
     */

    /**
     * Pruefung, ob das Deque leer ist
     * @return
     */
    public boolean isEmpty();



}
