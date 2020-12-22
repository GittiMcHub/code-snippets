package de.gittimchub.structures.util.deque;


import java.util.Arrays;

/**
 * @author GittiMcHub
 */
public class DequeArray<T> {

    private Object elements[];

    public DequeArray() {
        this.elements = new Object[0];
    }

    /**
     * Packt ein Element oben rauf
     * @param element
     * @return
     * @throws IllegalArgumentException
     */
    public DequeArray<T> push(T element) throws IllegalArgumentException {
        if (element == null) {
            throw new IllegalArgumentException();
        }

        // Auf dem Array "Oben" ein platz schaffen, wo das Element hinkommt
        Object[] old = this.elements.clone();
        this.elements = new Object[old.length + 1];

        for(int i = 0; i < old.length; i++){
            this.elements[i] = old[i];
        }
        this.elements[old.length] = element;

        return this;
    }

    /**
     * Verarbeitet das oberste Element
     * @return
     * @throws NullPointerException
     */
    public DequeArray<T> pop() throws NullPointerException {
        try {

            this.elements[this.elements.length - 1] = null;

            Object[] old = this.elements.clone();
            this.elements = new Object[old.length - 1];

            for(int i = 0; i < elements.length; i++){
                this.elements[i] = old[i];
            }

        } catch (Exception e) {
            throw new NullPointerException();
        }
        return this;
    }

    /**
     * Zeigt das oberste Element
     * @return
     */
    public T peekLast() {
        @SuppressWarnings("unchecked")
        T t = (T) elements[this.elements.length - 1];
        return t;
    }

    /**
     * Reiht ein element ein
     * @param element
     * @return
     * @throws IllegalArgumentException
     */
    public DequeArray<T> enqueue(T element) throws IllegalArgumentException {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        Object[] old = this.elements.clone();
        this.elements = new Object[old.length + 1];

        // Array "nach oben" schieben, damit bei index 0 platz ist
        for(int i = 1; i <= old.length; i++){
            this.elements[i] = old[i-1];
        }

        this.elements[0] = element;

        return this;
    }

    /**
     * Verarbeitet das Element das zuerst reingekommen ist
     * @return
     * @throws NullPointerException
     */
    public DequeArray<T> deque() throws NullPointerException {
        try {

            this.elements[this.elements.length - 1] = null;

            Object[] old = this.elements.clone();
            this.elements = new Object[old.length - 1];

            for(int i = 0; i < elements.length; i++){
                this.elements[i] = old[i];
            }

        } catch (Exception e) {
            throw new NullPointerException();
        }
        return this;
    }

    public T peekFirst() {
        @SuppressWarnings("unchecked")
        T t = (T) this.elements[this.elements.length - 1];
        return t;
    }


    public boolean isEmpty() {
        return this.elements.length == 0;
    }

    @Override
    public String toString() {
        return "DequeArray{" +
                "elements=" + Arrays.toString(elements) +
                '}';
    }

    /*
    Reorg methoden
     */

}