package com.mycompany.satellite.Helper;
import main.classes.PCB;

public class Queue<T> {
    private Node<T> front;
    private Node<T> rear;
    private int size;

    public Queue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

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
        if (isEmpty()) return null;
        T data = front.getData();
        front = front.getNext();
        if (front == null) rear = null;
        size--;
        return data;
    }

    public T peek() {
        if (isEmpty()) return null;
        return front.getData();
    }

    public boolean isEmpty() { return front == null; }
    public int getSize() { return size; }

    // Método para recorrer la lista en la GUI
    public T get(int index) {
        if (isEmpty() || index < 0 || index >= size) return null;
        Node<T> aux = front;
        for (int i = 0; i < index; i++) aux = aux.getNext();
        return aux.getData();
    }
    
    // Método CRÍTICO para algoritmos (SRT, Prioridad, EDF)
    public void enqueueOrdenado(T data, String algoritmo) {
        if (!(data instanceof PCB)) { enqueue(data); return; }
        PCB newPcb = (PCB) data;
        Node<T> newNode = new Node<>(data);

        if (isEmpty()) {
            front = newNode; rear = newNode; size++; return;
        }
        // Cabeza
        if (shouldSwap(newPcb, (PCB)front.getData(), algoritmo)) {
            newNode.setNext(front); front = newNode; size++; return;
        }
        // Cuerpo
        Node<T> current = front;
        while (current.getNext() != null) {
            if (shouldSwap(newPcb, (PCB)current.getNext().getData(), algoritmo)) break;
            current = current.getNext();
        }
        newNode.setNext(current.getNext());
        current.setNext(newNode);
        if (newNode.getNext() == null) rear = newNode;
        size++;
    }

    private boolean shouldSwap(PCB nuevo, PCB actual, String algo) {
        switch (algo) {
            case "SRT": return nuevo.getCiclosRestantes() < actual.getCiclosRestantes();
            case "Prioridad": return nuevo.getPrioridad() < actual.getPrioridad(); // Menor valor = Mayor prioridad
            case "EDF": return nuevo.getDeadline() < actual.getDeadline();
            default: return false;
        }
    }
}
