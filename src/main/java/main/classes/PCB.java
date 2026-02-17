/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

public class PCB implements Runnable {

	private int id;
	private String estado;       // "Nuevo", "Listo", "Ejecucion", "Bloqueado", "Suspendido"
	private int programCounter;  // PC
	private int mar;             // MAR

	// Datos para la planificación
	private int ciclosTotales;   // Duración total (Instrucciones)
	private int ciclosRestantes; // Cuánto falta para terminar
	private int prioridad;       // 1 (Alta), 2 (Media), 3 (Baja)
	private int deadline;        // Ciclo límite para terminar

	// Constructor Completo
	public PCB(int id, int ciclosTotales, int prioridad, int deadline) {
		this.id = id;
		this.ciclosTotales = ciclosTotales;
		this.ciclosRestantes = ciclosTotales; // Al inicio, restante = total
		this.prioridad = prioridad;
		this.deadline = deadline;

		this.estado = "Nuevo";
		this.programCounter = 0;
		this.mar = 0;
	}

	// --- Getters y Setters ---
	public int getId() {
		return id;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public int getProgramCounter() {
		return programCounter;
	}

	public void setProgramCounter(int pc) {
		this.programCounter = pc;
	}

	public int getMar() {
		return mar;
	}

	public void setMar(int mar) {
		this.mar = mar;
	}

	public int getCiclosRestantes() {
		return ciclosRestantes;
	}

	public void setCiclosRestantes(int ciclos) {
		this.ciclosRestantes = ciclos;
	}

	public int getPrioridad() {
		return prioridad;
	}

	public int getDeadline() {
		return deadline;
	}

	public int getCiclosTotales() {
		return ciclosTotales;
	}

	// --- ToString para mostrar en la lista visual ---
	@Override
	public String toString() {
		// Ejemplo: "ID:1 | Prio:1 | Rest: 15"
		return "ID:" + id + " | P:" + prioridad + " | Rest:" + ciclosRestantes;
	}

	public void run() {
		try {
			while (this.ciclosRestantes > 0) {

				// Simulacion de trabajo
				Thread.sleep(1000);

				// Ajusta los tiempos
				this.ciclosRestantes--;
				this.programCounter++;
				System.out.println("Proceso " + id + " Intervalos restantes: " + ciclosRestantes);
			}

			this.estado = "Terminado"; // Process finished naturally

		} catch (InterruptedException e) {
			System.out.println("Proceso" + id + " En PAUSA (Context Switch).");
		}
	}
}
