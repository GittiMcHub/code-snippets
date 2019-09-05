package de.gittimchub.structures.deque;

/**
 * @author GittiMcHub
 * @param <T>
 */
public class Element<T> {

    private T content;

    private Element<T> previousElement;
    private Element<T> nextElement;

    public Element(T content){
        this.content = content;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public Element<T> getPreviousElement() {
        return previousElement;
    }

    public void setPreviousElement(Element<T> previousElement) {
        this.previousElement = previousElement;
    }

    public Element<T> getNextElement() {
        return nextElement;
    }

    public void setNextElement(Element<T> nextElement) {
        this.nextElement = nextElement;
    }

    @Override
    public String toString() {
        return "{" +
                (previousElement != null ? previousElement.content : "null") + "<previous<" +
                " content=" + content +
                " >next> " + (nextElement != null ? nextElement.content : "null") +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Element<?>){
            if( ((Element<?>) obj).getContent() == this.getContent())
            return false;
        }
        return false;
    }
}
