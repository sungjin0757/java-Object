package chapter7;
import java.util.Arrays;
import java.util.EmptyStackException;

public class Stack {
    private Object[] elements;
    private int size=0;
    private static final int DEFAULT_INITIAL_CAPACITY=16;

    public Stack(){
        elements=new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e){
        ensureCapacity();
        elements[size++]=e;
    }

    public Object pop(){
        if(size==0){
            throw new EmptyStackException();
        }
        Object pop=elements[--size];
        elements[size]=null;
        return pop;
    }

    private void ensureCapacity(){
        if(elements.length==size){
            elements=Arrays.copyOf(elements,size*2+1);
        }
    }
}
