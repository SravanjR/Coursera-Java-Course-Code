package textgen;

import java.util.AbstractList;


/** A class that implements a doubly linked list
 * 
 * @author UC San Diego Intermediate Programming MOOC team
 *
 * @param <E> The type of the elements stored in the list
 */
public class MyLinkedList<E> extends AbstractList<E> {
	LLNode<E> head;
	LLNode<E> tail;
	int size;

	/** Create a new empty LinkedList */
	public MyLinkedList() {
		// TODO: Implement this method
		this.size = 0;
		this.head = new LLNode<E>();
		this.tail = new LLNode<E>();
		this.head.next = this.tail;
		this.tail.prev = this.head;
	}

	/**
	 * Appends an element to the end of the list
	 * @param element The element to add
	 */
	public boolean add(E element) 
	{
		// TODO: Implement this method
		if(element == null){
			throw new NullPointerException("Not Valid Element");
		}
		LLNode<E> nodeAdd = new LLNode<E>(element);
		LLNode<E> previous = tail.prev;
		previous.next = nodeAdd;
		nodeAdd.prev = previous;
		nodeAdd.next = tail;
		tail.prev = nodeAdd;
		size++;
		return true;
	}

	/** Get the element at position index 
	 * @throws IndexOutOfBoundsException if the index is out of bounds. */
	public E get(int index) 
	{
		// TODO: Implement this method.
		if (index < 0 || index > size - 1) {
			throw new IndexOutOfBoundsException("Invalid index");
		}
		LLNode<E> target = head;
		for (int i = 0; i <= index; i++) {
			target = target.next;
		}
		E value = target.data;
		return value;
	}

	/**
	 * Add an element to the list at the specified index
	 * @param The index where the element should be added
	 * @param element The element to add
	 */
	public void add(int index, E element ) 
	{
		// TODO: Implement this method
		if(element == null){
			throw new NullPointerException("Not Valid Element");
		}
		if ((index < 0 || index > size - 1) && (index != 0 || size != 0)) {
			throw new IndexOutOfBoundsException("Invalid index input!!");
		}
		LLNode<E> nodeAdd = new LLNode<E>(element);
		LLNode<E> indexBack = head;
		for (int i = 0; i <= index; i++) {
			indexBack = indexBack.next;
		}
		LLNode<E> previous = indexBack.prev;
		previous.next = nodeAdd;
		nodeAdd.prev = previous;
		nodeAdd.next = indexBack;
		indexBack.prev = nodeAdd;
		size++;
	}


	/** Return the size of the list */
	public int size() 
	{
		// TODO: Implement this method
		return size;
	}

	/** Remove a node at the specified index and return its data element.
	 * @param index The index of the element to remove
	 * @return The data element removed
	 * @throws IndexOutOfBoundsException If index is outside the bounds of the list
	 * 
	 */
	public E remove(int index) 
	{
		// TODO: Implement this method
		if (index < 0 || index > size - 1) {
			throw new IndexOutOfBoundsException("Invalid index input!!");
		}
		LLNode<E> nodeRemove = head;
		for (int i = 0; i <= index; i++) {
			nodeRemove = nodeRemove.next;
		}
		LLNode<E> previous = nodeRemove.prev;
		LLNode<E> nextNode = nodeRemove.next;
		
		previous.next = nextNode;
		nextNode.prev = previous;
		size--;
		E value = nodeRemove.data;
		return value;
	}

	/**
	 * Set an index position in the list to a new element
	 * @param index The index of the element to change
	 * @param element The new element
	 * @return The element that was replaced
	 * @throws IndexOutOfBoundsException if the index is out of bounds.
	 */
	public E set(int index, E element) 
	{
		// TODO: Implement this method
		if(element == null){
			throw new NullPointerException("Not Valid Element");
		}
		if (index < 0 || index > size - 1) {
			throw new IndexOutOfBoundsException("Invalid index input!!");
		}
		LLNode<E> nodeSet = head;
		for (int i = 0; i <= index; i++) {
			nodeSet = nodeSet.next;
		}
		nodeSet.data = element;
		return element;
	}   
}

class LLNode<E> 
{
	LLNode<E> prev;
	LLNode<E> next;
	E data;

	// TODO: Add any other methods you think are useful here
	// E.g. you might want to add another constructor

	public LLNode() 
	{
		this.data = null;
		this.prev = null;
		this.next = null;
	}
	
	public LLNode(E e) 
	{
		this.data = e;
		this.prev = null;
		this.next = null;
	}

}
