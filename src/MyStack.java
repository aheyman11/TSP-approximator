/**
 * Created by andreahe on 4/28/15.
 * Linked list implementation of stack
 */
public class MyStack {
    Node header;

    public MyStack() {
        header = null;
    }

    public void push(Point point) {
        header = new Node(point, header);
    }
    public Point pop() {
        Point popped = header.data;
        header = header.next;
        return popped;
    }
    public Point peak(){
        return header.data;
    }
}
