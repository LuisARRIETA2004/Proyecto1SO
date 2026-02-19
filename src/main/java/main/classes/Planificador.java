/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;
import com.mycompany.satellite.Helper.Queue;

public class Planificador {
    
    // --- Recursos del Sistema ---
    private CPU cpu;
    private final int MAX_MEMORIA = 10;
    
    // --- Colas de Estado ---
    private Queue<PCB> colaListos;
    private Queue<PCB> colaBloqueados;
    private Queue<PCB> colaListosSusp; // Disco (Swap)
    private Queue<PCB> colaBloqSusp;
    
    // --- Control ---
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
     * Este método se llama CADA CICLO de reloj desde la Ventana.
     * Es el corazón del sistema operativo.
     */
    public void ejecutarCicloDelSistema() {
        
        // 1. Gestionar Procesos Bloqueados (E/S)
        gestionarBloqueados();
        
        // 2. Revisar si el proceso en CPU terminó o se bloqueó
        if (cpu.isBusy()) {
            PCB p = cpu.getCurrentProcess();
            
            // A. Chequeo de fin de proceso
            if (p.getCiclosRestantes() <= 0) {
                p.setEstado("Terminado");
                cpu.releaseProcess();
                contadorQuantum = 0;
                checkSwapping(); // Al liberar RAM, revisamos si alguien puede entrar del disco
            }
            // B. Chequeo de Bloqueo por E/S
            else if (p.getCicloIrrupccionES() == p.getCiclosRestantes()) {
                p.setEstado("Bloqueado");
                colaBloqueados.enqueue(p);
                cpu.releaseProcess();
                contadorQuantum = 0;
            }
            // C. Chequeo de Quantum (Solo para Round Robin)
            else if (algoritmoActual.equals("Round Robin")) {
                contadorQuantum++;
                if (contadorQuantum >= quantum) {
                    p.setEstado("Listo");
                    colaListos.enqueue(p); // Vuelve a la cola
                    cpu.releaseProcess();
                    contadorQuantum = 0;
                } else {
                    cpu.ejecutarInstruccion();
                }
            }
            // D. Ejecución Normal (FCFS, SRT, etc)
            else {
                cpu.ejecutarInstruccion();
                // Check Expropiación para SRT/Prioridad/EDF
                checkExpropiacion(); 
            }
        }
        
        // 3. Dispatcher: Si el CPU está libre, meter a alguien
        if (!cpu.isBusy() && !colaListos.isEmpty()) {
            PCB siguiente = colaListos.dequeue();
            cpu.assignProcess(siguiente);
            contadorQuantum = 0;
        }
    }
    
    // --- Lógica de Interrupciones ---
    public void manejarInterrupcionHardware() {
        if (cpu.isBusy()) {
            PCB p = cpu.releaseProcess();
            // Guardar contexto y devolver a cola de listos
            // OJO: Si usas SRT/Prioridad, usa enqueueOrdenado
            if (algoritmoActual.equals("FCFS") || algoritmoActual.equals("Round Robin")) {
                colaListos.enqueue(p);
            } else {
                colaListos.enqueueOrdenado(p, algoritmoActual);
            }
        }
    }
    
    // --- Gestión de Memoria (Swap) ---
    public void agregarProceso(PCB nuevo) {
        int ocupados = colaListos.getSize() + colaBloqueados.getSize() + (cpu.isBusy() ? 1 : 0);
        
        if (ocupados < MAX_MEMORIA) {
            nuevo.setEstado("Listo");
            // Insertar según algoritmo
            if (algoritmoActual.equals("FCFS") || algoritmoActual.equals("Round Robin")) {
                colaListos.enqueue(nuevo);
            } else {
                colaListos.enqueueOrdenado(nuevo, algoritmoActual);
            }
        } else {
            nuevo.setEstado("Listo-Suspendido");
            colaListosSusp.enqueue(nuevo);
        }
    }
    
    private void checkSwapping() {
        // Si hay hueco en RAM y gente en disco, subirlos
        int ocupados = colaListos.getSize() + colaBloqueados.getSize() + (cpu.isBusy() ? 1 : 0);
        if (ocupados < MAX_MEMORIA && !colaListosSusp.isEmpty()) {
            PCB swapIn = colaListosSusp.dequeue();
            agregarProceso(swapIn); // Reintenta agregarlo (ahora entrará a RAM)
        }
    }
    
    // --- Auxiliares ---
    private void gestionarBloqueados() {
        if (colaBloqueados.isEmpty()) return;
        int n = colaBloqueados.getSize();
        for(int i=0; i<n; i++) {
            PCB p = colaBloqueados.dequeue();
            p.setLongitudES(p.getLongitudES() - 1);
            if (p.getLongitudES() <= 0) {
                agregarProceso(p); // Vuelve a Listos (o Suspendidos si se llenó RAM mientras tanto)
            } else {
                colaBloqueados.enqueue(p);
            }
        }
    }
    
    private void checkExpropiacion() {
        if (colaListos.isEmpty()) return;
        PCB candidato = colaListos.peek();
        PCB actual = cpu.getCurrentProcess();
        boolean cambio = false;
        
        if (algoritmoActual.equals("SRT") && candidato.getCiclosRestantes() < actual.getCiclosRestantes()) cambio = true;
        if (algoritmoActual.equals("Prioridad") && candidato.getPrioridad() < actual.getPrioridad()) cambio = true;
        if (algoritmoActual.equals("EDF") && candidato.getDeadline() < actual.getDeadline()) cambio = true;
        
        if (cambio) {
            cpu.releaseProcess();
            colaListos.enqueueOrdenado(actual, algoritmoActual);
            // El dispatcher meterá al candidato en el siguiente ciclo
        }
    }

    // --- Getters para la GUI ---
    public Queue<PCB> getColaListos() { return colaListos; }
    public Queue<PCB> getColaBloqueados() { return colaBloqueados; }
    public Queue<PCB> getColaListosSusp() { return colaListosSusp; }
    public Queue<PCB> getColaBloqSusp() { return colaBloqSusp; }
    public CPU getCpu() { return cpu; }
    public void setAlgoritmo(String algo) { this.algoritmoActual = algo; }
    public int getOcupacionMemoria() { return colaListos.getSize() + colaBloqueados.getSize() + (cpu.isBusy() ? 1 : 0); }
}