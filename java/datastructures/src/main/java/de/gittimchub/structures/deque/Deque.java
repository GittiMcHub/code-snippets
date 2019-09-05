package de.gittimchub.structures.deque;

import java.util.ArrayList;

/**
 * @author GittiMcHub
 */
public class Deque<T> implements DequeFunctionality<T> {

    private ArrayList<Element<T>> elements;

    public Deque() {
        this.elements = new ArrayList<Element<T>>();
    }


    @Override
    public Deque<T> push(T element) throws IllegalArgumentException {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        Element<T> e = new Element<T>(element);
        this.elements.add(e);
        organizeElementsAsStack();
        return this;
    }

    @Override
    public Deque<T> pop() throws NullPointerException {
        try {
            this.elements.remove(this.elements.size() - 1);
        } catch (Exception e) {
            throw new NullPointerException();
        }

        organizeElementsAsStack();
        return this;
    }

    @Override
    public Element<T> peekLast() {
        organizeElementsAsStack();
        Element<T> element = this.elements.get(this.elements.size() - 1);
        return element;
    }

    //@Override
    public Deque<T> enqueue(T element) throws IllegalArgumentException {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        Element<T> e = new Element<T>(element);
        this.elements.add(0, e);
        organizeElementsAsQueue();
        return this;
    }

    @Override
    public Deque<T> deque() throws NullPointerException {
        try {
            this.elements.remove(0);
        } catch (Exception e) {
            throw new NullPointerException();
        }

        organizeElementsAsQueue();
        return this;
    }

    @Override
    public Element<T> peekFirst() {

        organizeElementsAsQueue();
        return this.elements.get(this.elements.size() - 1);

    }

    @Override
    public boolean isEmpty() {
        return this.elements.isEmpty();
    }

    /**
     * Verbindet die Elemente untereinander, sodass jedes Element seinen Nachbarn kennt
     * aus der Sicht eines Stacks
     */
    private void organizeElementsAsStack() {
        for (int i = 0; i < this.elements.size(); i++) {
            this.elements.get(i).setPreviousElement(i > 0 ? this.elements.get(i - 1) : null);
            this.elements.get(i).setNextElement(i == this.elements.size() - 1 ? null : this.elements.get(i + 1));
        }
    }

    /**
     * Verbindet die Elemente untereinander, sodass jedes Element seinen Nachbarn kennt
     * aus der Sicht einer Queue
     */
    private void organizeElementsAsQueue() {
        for (int i = 0; i < this.elements.size(); i++) {
            this.elements.get(i).setPreviousElement(i == this.elements.size() - 1 ? null : this.elements.get(i + 1));
            this.elements.get(i).setNextElement(i == 0 ? null : this.elements.get(i - 1));
        }
    }
}