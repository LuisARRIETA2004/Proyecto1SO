package main.classes;

import com.mycompany.satellite.Helper.Queue;

public class Planificador {
    
    // --- Recursos ---
    private CPU cpu;
    private final int MAX_MEMORIA = 10;
    
    // --- Colas ---
    private Queue<PCB> colaListos;
    private Queue<PCB> colaBloqueados;
    private Queue<PCB> colaListosSusp;
    private Queue<PCB> colaBloqSusp;
    
    // --- Configuración ---
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
    
    // --- Lógica Principal (Se llama cada segundo) ---
    public void ejecutarCicloDelSistema() {
        gestionarBloqueados();
        
        // 1. Verificar proceso en CPU
        if (cpu.isBusy()) {
            PCB p = cpu.getCurrentProcess();
            
            // A. Terminó
            if (p.getCiclosRestantes() <= 0) {
                p.setEstado("Terminado");
                cpu.releaseProcess();
                contadorQuantum = 0;
                checkSwapping(); 
            }
            // B. Bloqueo por E/S
            else if (p.getCicloIrrupccionES() == p.getCiclosRestantes()) {
                p.setEstado("Bloqueado");
                colaBloqueados.enqueue(p);
                cpu.releaseProcess();
                contadorQuantum = 0;
            }
            // C. Round Robin
            else if (algoritmoActual.equals("Round Robin")) {
                cpu.ejecutarInstruccion();
                contadorQuantum++;
                if (contadorQuantum >= quantum) {
                    p.setEstado("Listo");
                    colaListos.enqueue(p);
                    cpu.releaseProcess();
                    contadorQuantum = 0;
                }
            }
            // D. Normal (FCFS, SRT, etc)
            else {
                cpu.ejecutarInstruccion();
                checkExpropiacion(); 
            }
        }
        
        // 2. Dispatcher (Meter a CPU)
        if (!cpu.isBusy() && !colaListos.isEmpty()) {
            PCB siguiente = colaListos.dequeue();
            cpu.assignProcess(siguiente);
            contadorQuantum = 0;
        }
    }
    
    // --- Gestión de Procesos ---
    public void agregarProceso(PCB nuevo) {
        int ocupados = getOcupacionMemoria();
        
        if (ocupados < MAX_MEMORIA) {
            nuevo.setEstado("Listo");
            encolarSegunAlgoritmo(nuevo);
        } else {
            nuevo.setEstado("Listo-Suspendido");
            colaListosSusp.enqueue(nuevo);
        }
    }
    
    public void manejarInterrupcionHardware() {
        if (cpu.isBusy()) {
            PCB p = cpu.releaseProcess();
            p.setEstado("Listo");
            encolarSegunAlgoritmo(p);
            contadorQuantum = 0;
        }
    }
    
    public void setAlgoritmo(String algo) {
        this.algoritmoActual = algo;
        // Si cambiamos algoritmo, reordenamos la cola actual
        reordenarColaListos();
    }

    // --- Auxiliares Privados ---
    
    private void encolarSegunAlgoritmo(PCB p) {
        if (algoritmoActual.equals("SRT") || algoritmoActual.equals("Prioridad") || algoritmoActual.equals("EDF")) {
            colaListos.enqueueOrdenado(p, algoritmoActual);
        } else {
            colaListos.enqueue(p);
        }
    }

    private void reordenarColaListos() {
        // Truco: Sacar todo y volver a meter usando enqueueOrdenado
        int n = colaListos.getSize();
        Queue<PCB> temp = new Queue<>();
        for(int i=0; i<n; i++) temp.enqueue(colaListos.dequeue());
        
        while(!temp.isEmpty()) {
            encolarSegunAlgoritmo(temp.dequeue());
        }
    }

    private void gestionarBloqueados() {
        if (colaBloqueados.isEmpty()) return;
        int n = colaBloqueados.getSize();
        for(int i=0; i<n; i++) {
            PCB p = colaBloqueados.dequeue();
            p.setLongitudES(p.getLongitudES() - 1);
            if (p.getLongitudES() <= 0) {
                agregarProceso(p);
            } else {
                colaBloqueados.enqueue(p);
            }
        }
    }
    
    private void checkSwapping() {
        if (getOcupacionMemoria() < MAX_MEMORIA && !colaListosSusp.isEmpty()) {
            agregarProceso(colaListosSusp.dequeue());
        }
    }
    
    private void checkExpropiacion() {
        if (colaListos.isEmpty()) return;
        PCB candidato = colaListos.peek();
        PCB actual = cpu.getCurrentProcess();
        boolean cambio = false;
        
        if (algoritmoActual.equals("SRT") && candidato.getCiclosRestantes() < actual.getCiclosRestantes()) cambio = true;
        if (algoritmoActual.equals("Prioridad") && candidato.getPrioridad() < actual.getPrioridad()) cambio = true; // Asumiendo menor valor = mayor prioridad
        
        if (cambio) {
            cpu.releaseProcess();
            actual.setEstado("Listo");
            encolarSegunAlgoritmo(actual);
        }
    }

    // --- Getters para la GUI ---
    public Queue<PCB> getColaListos() { return colaListos; }
    public Queue<PCB> getColaBloqueados() { return colaBloqueados; }
    public Queue<PCB> getColaListosSusp() { return colaListosSusp; }
    public Queue<PCB> getColaBloqSusp() { return colaBloqSusp; }
    public CPU getCpu() { return cpu; }
    public int getOcupacionMemoria() { return colaListos.getSize() + colaBloqueados.getSize() + (cpu.isBusy() ? 1 : 0); }
}