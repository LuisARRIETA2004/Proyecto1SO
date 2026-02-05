package com.mycompany.satellite.Helper;

/**
 * Cola Genérica (FIFO) implementada con lista enlazada simple.
 * No utiliza librerías de java.util
 */
public class Queue<T> {

    private Node<T> front; // Inicio de la cola
    private Node<T> rear;  // Final de la cola
    private int size;      // Tamaño actual

    public Queue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    // --- Métodos Principales ---
    public void enqueue(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
            rear.setNext(newNode);
            rear = newNode;
        }
        size++;
    }

    public T dequeue() {
        if (isEmpty()) {
            return null;
        }
        T data = front.getData();
        front = front.getNext();
        if (front == null) {
            rear = null;
        }
        size--;
        return data;
    }

    public T peek() {
        if (isEmpty()) return null;
        return front.getData();
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int getSize() {
        return size;
    }

    // --- Métodos Auxiliares ---
    public boolean remove(T dataToRemove) {
        if (isEmpty() || dataToRemove == null) return false;

        if (front.getData().equals(dataToRemove)) {
            dequeue();
            return true;
        }

        Node<T> current = front;
        while (current.getNext() != null) {
            if (current.getNext().getData().equals(dataToRemove)) {
                Node<T> nodeToRemove = current.getNext();
                current.setNext(nodeToRemove.getNext());
                if (nodeToRemove == rear) {
                    rear = current;
                }
                size--;
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    public T get(int index) {
        if (isEmpty() || index < 0 || index >= size) return null;
        
        Node<T> aux = front;
        for (int i = 0; i < index; i++) {
            aux = aux.getNext();
        }
        return aux.getData();
    }
}