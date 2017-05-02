/**
 * Created by Megan on 4/8/2017.
 */
public class Stack {
    private int maxSize;
    private Symbol[] stackArray;
    private int top;
    public Stack(int value){
        maxSize = value;
        stackArray = new Symbol[maxSize];
        top = -1;
    }
    //-------------------------------------
    public void push(Symbol value){
        stackArray[++top] = value;
    }
    //--------------------
    public Symbol pop(){
        return stackArray[top--];
    }
    public Symbol peek(){
        return stackArray[top];
    }
    public boolean isEmpty(){
        return (top == -1);
    }
    public boolean isFull(){
        return (top == maxSize-1);
    }

}
