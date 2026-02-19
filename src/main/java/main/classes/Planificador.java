/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

import com.mycompany.satellite.Helper.Queue;
import com.mycompany.satellite.Helper.GeneradorProcesos;

public class Planificador {

    // --- Recursos del Sistema ---
    private CPU cpu;
    private final int MAX_MEMORIA = 10; // Capacidad de la RAM
    private int cicloReloj = 0;

    // --- Colas de Estado ---
    private Queue<PCB> colaListos;
    private Queue<PCB> colaBloqueados;
    private Queue<PCB> colaListosSusp; // Disco (Swap)
    private Queue<PCB> colaBloqSusp;

    // --- Control de Planificación ---
    private String algoritmoActual = "FCFS";
    private int quantum = 5;
    private int contadorQuantum = 0;

    public Planificador() {
        this.cpu = new CPU();
        this.colaListos = new Queue<>();
        this.colaBloqueados = new Queue<>();
        this.colaListosSusp = new Queue<>();
        this.colaBloqSusp = new Queue<>();
    }

    /**
     * CORAZÓN DEL SISTEMA: Se ejecuta en cada ciclo del reloj.
     */
    public void ejecutarCicloDelSistema() {
        cicloReloj++;

        // 1. Gestión de E/S (Procesos Bloqueados)
        gestionarBloqueados();

        // 2. Lógica de Ejecución en CPU
        if (cpu.isBusy()) {
            PCB p = cpu.getCurrentProcess();

            // Caso A: El proceso terminó sus instrucciones
            if (p.getCiclosRestantes() <= 0) {
                p.setEstado("Terminado");
                cpu.releaseProcess();
                contadorQuantum = 0;
                checkSwapping(); // Al liberar RAM, intentamos subir alguien de Disco
            } 
            // Caso B: El proceso llegó a su ciclo de interrupción por E/S
            else if (p.getCicloIrrupccionES() == (p.getCiclosTotales() - p.getProgramCounter())) {
                p.setEstado("Bloqueado");
                colaBloqueados.enqueue(p);
                cpu.releaseProcess();
                contadorQuantum = 0;
            } 
            // Caso C: Round Robin (Control de tiempo)
            else if (algoritmoActual.equals("Round Robin")) {
                contadorQuantum++;
                if (contadorQuantum >= quantum) {
                    p.setEstado("Listo");
                    colaListos.enqueue(p);
                    cpu.releaseProcess();
                    contadorQuantum = 0;
                } else {
                    cpu.ejecutarInstruccion();
                }
            } 
            // Caso D: Algoritmos con Expropiación (SRT, Prioridad, EDF)
            else {
                cpu.ejecutarInstruccion();
                checkExpropiacion(); 
            }
        }

        // 3. Dispatcher: Si el CPU quedó libre, asignamos el siguiente
        if (!cpu.isBusy() && !colaListos.isEmpty()) {
            PCB siguiente = colaListos.dequeue();
            cpu.assignProcess(siguiente);
            contadorQuantum = 0;
        }
    }

    /**
     * Recibe un proceso nuevo y decide si va a RAM o a SWAP (Disco).
     */
    public void recibirNuevoProceso(PCB nuevo) {
        if (getOcupacionMemoria() < MAX_MEMORIA) {
            nuevo.setEstado("Listo");
            insertarEnColaListos(nuevo);
        } else {
            nuevo.setEstado("Listo-Suspendido");
            colaListosSusp.enqueue(nuevo);
        }
    }

    private void insertarEnColaListos(PCB pcb) {
        if (algoritmoActual.equals("FCFS") || algoritmoActual.equals("Round Robin")) {
            colaListos.enqueue(pcb);
        } else {
            colaListos.enqueueOrdenado(pcb, algoritmoActual);
        }
    }

    /**
     * Mueve procesos de Disco a RAM si hay espacio disponible.
     */
    public void checkSwapping() {
        while (getOcupacionMemoria() < MAX_MEMORIA && !colaListosSusp.isEmpty()) {
            PCB p = colaListosSusp.dequeue();
            p.setEstado("Listo");
            insertarEnColaListos(p);
        }
    }

    private void gestionarBloqueados() {
        int tam = colaBloqueados.getSize();
        for (int i = 0; i < tam; i++) {
            PCB p = colaBloqueados.dequeue();
            p.setLongitudES(p.getLongitudES() - 1);
            
            if (p.getLongitudES() <= 0) {
                recibirNuevoProceso(p); // Intenta volver a RAM
            } else {
                colaBloqueados.enqueue(p);
            }
        }
    }

    private void checkExpropiacion() {
        if (colaListos.isEmpty()) return;
        
        PCB candidato = colaListos.peek();
        PCB actual = cpu.getCurrentProcess();
        boolean debeExpropiar = false;

        switch (algoritmoActual) {
            case "SRT": 
                if (candidato.getCiclosRestantes() < actual.getCiclosRestantes()) debeExpropiar = true;
                break;
            case "Prioridad": 
                if (candidato.getPrioridad() < actual.getPrioridad()) debeExpropiar = true;
                break;
            case "EDF":
                if (candidato.getDeadline() < actual.getDeadline()) debeExpropiar = true;
                break;
        }

        if (debeExpropiar) {
            cpu.releaseProcess();
            insertarEnColaListos(actual);
            // El dispatcher en el siguiente ciclo meterá al candidato
        }
    }

    /**
     * Reordena la cola si el usuario cambia el algoritmo a mitad de simulación.
     */
    public void reordenarSegunAlgoritmo(String nuevoAlgo) {
        this.algoritmoActual = nuevoAlgo;
        if (colaListos.isEmpty() || nuevoAlgo.equals("FCFS") || nuevoAlgo.equals("Round Robin")) return;

        // Pasamos a arreglo para ordenar (Burbuja simple)
        int n = colaListos.getSize();
        PCB[] temp = new PCB[n];
        for (int i = 0; i < n; i++) temp[i] = colaListos.dequeue();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (compararPrioridad(temp[j], temp[j + 1])) {
                    PCB aux = temp[j];
                    temp[j] = temp[j + 1];
                    temp[j + 1] = aux;
                }
            }
        }
        for (PCB p : temp) colaListos.enqueue(p);
    }

    private boolean compararPrioridad(PCB p1, PCB p2) {
        switch (algoritmoActual) {
            case "SRT": return p1.getCiclosRestantes() > p2.getCiclosRestantes();
            case "Prioridad": return p1.getPrioridad() > p2.getPrioridad();
            case "EDF": return p1.getDeadline() > p2.getDeadline();
            case "SPN": return p1.getCiclosTotales() > p2.getCiclosTotales();
            default: return false;
        }
    }

    public void interrupcionManual() {
        if (cpu.isBusy()) {
            PCB p = cpu.releaseProcess();
            insertarEnColaListos(p);
            contadorQuantum = 0;
        }
    }

    // --- Getters para la Interfaz ---
    public int getOcupacionMemoria() {
        return colaListos.getSize() + colaBloqueados.getSize() + (cpu.isBusy() ? 1 : 0);
    }

    public int getReloj() { return cicloReloj; }
    public CPU getCpu() { return cpu; }
    public Queue<PCB> getColaListos() { return colaListos; }
    public Queue<PCB> getColaBloqueados() { return colaBloqueados; }
    public Queue<PCB> getColaListosSusp() { return colaListosSusp; }
    public Queue<PCB> getColaBloqSusp() { return colaBloqSusp; }
    public void setAlgoritmo(String algo) { this.algoritmoActual = algo; }
}