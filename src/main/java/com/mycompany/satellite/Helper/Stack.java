/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.satellite.Helper;

/**
 *
 * @author truenno
 */
public class Stack<T> {
	
	private Node<T> top;
	
	public Stack() {
		this.top = null;
	}

	// Actualiza el tope del stack
	public void push(T data) {
		Node<T> newNode = new Node<>(data);
		newNode.next = top;
		top = newNode;
	}
}
