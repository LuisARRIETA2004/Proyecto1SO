package com.mycompany.satellite.Helper;

import main.classes.PCB; // Importación necesaria para que runRoundRobin reconozca el objeto

/**
 * Cola Genérica (FIFO) implementada con lista enlazada simple.
 * No utiliza librerías de java.util
 */
public class Queue<T> {

    // Constante para la lógica de tu compañero
    private static final int QUANTUM = 5;

    private Node<T> front; // Inicio de la cola (por donde salen)
    private Node<T> rear;  // Final de la cola (por donde entran)
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
        if (isEmpty()) {
            return null;
        }
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
        if (isEmpty() || dataToRemove == null) {
            return false;
        }

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
        if (isEmpty() || index < 0 || index >= size) {
            return null;
        }
        Node<T> aux = front;
        for (int i = 0; i < index; i++) {
            aux = aux.getNext();
        }
        return aux.getData();
    }

    /**
     * Lógica de Round Robin solicitada por el compañero.
     * ADAPTADA para usar los métodos reales de tu clase PCB.
     */
    public void runRoundRobin(Queue<PCB> readyQueue) {
        // Nota: Este bucle ejecuta toda la simulación de golpe en la consola.
        // Si lo usas así en la interfaz gráfica, puede que la ventana se congele
        // hasta que termine el while.
        
        while (!readyQueue.isEmpty()) {

            PCB currentProcess = readyQueue.dequeue();

            // CAMBIO 1: Usamos getId() en lugar de getProcessName()
            System.out.println("Proceso ID: " + currentProcess.getId());
            
            int timeSpent = 0;

            // CAMBIO 2: Usamos getCiclosRestantes() en lugar de getTimeInCpu()
            if (currentProcess.getCiclosRestantes() > QUANTUM) {
                timeSpent = QUANTUM;
            } else {
                timeSpent = currentProcess.getCiclosRestantes();
            }
            
            // CAMBIO 3: Actualizamos el tiempo restante
            currentProcess.setCiclosRestantes(currentProcess.getCiclosRestantes() - timeSpent); 
            
            if (currentProcess.getCiclosRestantes() > 0) {
                System.out.println("   -> NO TERMINADO (Restan: " + currentProcess.getCiclosRestantes() + ")");
                readyQueue.enqueue(currentProcess);
            } else {
                // CAMBIO 4: Usamos getId() nuevamente
                System.out.println("   -> Proceso " + currentProcess.getId() + " TERMINO ");
            }

            System.out.println("--------------------------------");
        }
    }
}