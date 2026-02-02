package com.mycompany.satellite.Helper;

import main.classes.PCB;

/**
 *
 *
 * Cola Genérica (FIFO) implementada con lista enlazada simple.
 *
 *
 * No utiliza librerías de java.util
 *
 *
 */
public class Queue<T> {

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
	/**
	 *
	 *
	 * Agrega un elemento al final de la cola.
	 *
	 *
	 */
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

	/**
	 *
	 *
	 * Saca y retorna el elemento del frente.
	 *
	 *
	 */
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

	/**
	 *
	 *
	 * Mira el elemento del frente sin sacarlo.
	 *
	 *
	 */
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

	// --- Métodos Auxiliares para el Proyecto ---
	/**
	 *
	 *
	 * Busca y elimina un objeto específico de la cola.
	 *
	 *
	 * Vital para sacar procesos que se bloquean o suspenden estando en la
	 * cola de listos.
	 *
	 *
	 */
	public boolean remove(T dataToRemove) {

		if (isEmpty() || dataToRemove == null) {

			return false;

		}

		// Caso 1: El dato está en el frente
		if (front.getData().equals(dataToRemove)) {

			dequeue();

			return true;

		}

		// Caso 2: Buscar en el resto de la lista
		Node<T> current = front;

		while (current.getNext() != null) {

			if (current.getNext().getData().equals(dataToRemove)) {

				Node<T> nodeToRemove = current.getNext();

				// Saltamos el nodo a eliminar
				current.setNext(nodeToRemove.getNext());

				// Si eliminamos el último, actualizamos rear
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

	/**
	 *
	 *
	 * Obtiene un elemento por su índice (útil para recorrer la cola en la
	 * GUI).
	 *
	 *
	 * @param index Índice (0 es el frente)
	 *
	 *
	 * @return El dato o null si fuera de rango.
	 *
	 *
	 */
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

	public void runRoundRobin(Queue<PCB> readyQueue) {
		while (!readyQueue.isEmpty()) {

			PCB currentProcess = readyQueue.dequeue();

			System.out.println("Proceso: " + currentProcess.getProcessName());
			int timeSpent = 0;

			if (currentProcess.getTimeInCpu() > QUANTUM) {
				timeSpent = QUANTUM;
			} else {
				timeSpent = currentProcess.getTimeInCpu();
			}
			currentProcess.setTimeInCpu(currentProcess.getTimeInCpu() - timeSpent); 
			if (currentProcess.getTimeInCpu() > 0) {
				System.out.println("NO TERMINADO");
				readyQueue.enqueue(currentProcess);
			} else {
				System.out.println("   -> " + currentProcess.getProcessName() + "TERMINO ");
			}

			System.out.println("--------------------------------");
		}
	}
}
