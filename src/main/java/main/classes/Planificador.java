package main.classes;

import com.mycompany.satellite.Helper.GeneradorProcesos;
import com.mycompany.satellite.Helper.Queue;
import com.mycompany.satellite.gui.VentanaSimulacion;
import javax.swing.SwingUtilities;

public class Planificador {
    
    // --- Referencia a la GUI (Para actualizarla) ---
    private VentanaSimulacion ventana;

    // --- Recursos del Sistema ---
    private CPU cpu;
    private final int MAX_MEMORIA = 10;
    
    // --- Colas ---
    private Queue<PCB> colaListos;
    private Queue<PCB> colaBloqueados;
    private Queue<PCB> colaListosSusp;
    private Queue<PCB> colaBloqSusp;
    
    // --- Control de Simulación (Motor) ---
    private boolean ejecutando = false;
    private Thread hiloSimulacion;
    private int cicloReloj = 0;
    private int contadorIds = 1;
    private int velocidadSimulacion = 1000; // ms
    
    // --- Configuración Algoritmos ---
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
    
    // Conectar con la ventana (Se llama desde la ventana al iniciar)
    public void setVentana(VentanaSimulacion ventana) {
        this.ventana = ventana;
    }

    // --- MOTOR: INICIAR / DETENER ---
    public void toggleSimulacion() {
        if (ejecutando) {
            detener();
        } else {
            iniciar();
        }
    }

    private void iniciar() {
        if (ejecutando) return;
        ejecutando = true;
        if (ventana != null) ventana.setEstadoBotonStart("DETENER");

        hiloSimulacion = new Thread(() -> {
            while (ejecutando) {
                try {
                    cicloReloj++;
                    
                    // 1. Ejecutar Lógica del Sistema Operativo
                    ejecutarCicloLogico();
                    
                    // 2. Mandar a pintar la pantalla
                    if (ventana != null) {
                        SwingUtilities.invokeLater(() -> ventana.actualizarInterfaz());
                    }

                    // 3. Esperar (Velocidad)
                    Thread.sleep(velocidadSimulacion);

                } catch (InterruptedException e) {
                    System.out.println("Hilo interrumpido");
                }
            }
        });
        hiloSimulacion.start();
    }

    private void detener() {
        ejecutando = false;
        if (ventana != null) ventana.setEstadoBotonStart("INICIAR");
    }
    
    // --- LÓGICA DE GESTIÓN (LO QUE MOVIMOS DE LA VENTANA) ---
    
    public void crearProcesosIniciales() {
        for (int i = 0; i < 5; i++) {
            generarProcesoAleatorio();
        }
    }
    
    public void generarProcesosMasivos() {
        for (int i = 0; i < 20; i++) {
            generarProcesoAleatorio();
        }
    }
    
    public void generarEmergencia() {
        PCB nuevo = GeneradorProcesos.generarProcesoAleatorio(contadorIds++);
        nuevo.setPrioridad(0); // Prioridad máxima
        manejarInterrupcionHardware(); // Preemption
        agregarProceso(nuevo);
    }
    
    private void generarProcesoAleatorio() {
        PCB nuevo = GeneradorProcesos.generarProcesoAleatorio(contadorIds++);
        agregarProceso(nuevo);
    }

    private void ejecutarCicloLogico() {
        gestionarBloqueados();
        
        // Lógica de CPU y Planificación
        if (cpu.isBusy()) {
            PCB p = cpu.getCurrentProcess();
            
            // Lógica simplificada para el ejemplo (Tu lógica completa va aquí)
            if (p.getCicloIrrupccionES() == p.getCiclosRestantes()) {
                p.setEstado("Bloqueado");
                colaBloqueados.enqueue(p);
                cpu.releaseProcess();
                contadorQuantum = 0;
            } else {
                cpu.ejecutarInstruccion();
                if (p.getCiclosRestantes() <= 0) {
                    p.setEstado("Terminado");
                    cpu.releaseProcess();
                    contadorQuantum = 0;
                    checkSwapping();
                }
                // Aquí iría el chequeo de Quantum para Round Robin
            }
        }
        
        // Dispatcher
        if (!cpu.isBusy() && !colaListos.isEmpty()) {
            PCB siguiente = colaListos.dequeue();
            cpu.assignProcess(siguiente);
            contadorQuantum = 0;
        }
    }
    
    // --- GESTIÓN DE MEMORIA Y COLAS ---
    
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
    
    private void encolarSegunAlgoritmo(PCB p) {
        // Lógica de ordenamiento
        if (algoritmoActual.equals("SRT") || algoritmoActual.equals("Prioridad")) {
            colaListos.enqueueOrdenado(p, algoritmoActual);
        } else {
            colaListos.enqueue(p);
        }
    }
    
    // --- MÉTODOS AUXILIARES ---
    
    private void checkSwapping() {
        if (getOcupacionMemoria() < MAX_MEMORIA && !colaListosSusp.isEmpty()) {
            agregarProceso(colaListosSusp.dequeue());
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
    
    public void manejarInterrupcionHardware() {
        if (cpu.isBusy()) {
            PCB p = cpu.releaseProcess();
            p.setEstado("Listo");
            encolarSegunAlgoritmo(p);
        }
    }

    public void reordenarColaListos() {
        // Lógica para reordenar la cola existente si cambia el algoritmo
        int n = colaListos.getSize();
        Queue<PCB> temp = new Queue<>();
        for(int i=0; i<n; i++) temp.enqueue(colaListos.dequeue());
        while(!temp.isEmpty()) encolarSegunAlgoritmo(temp.dequeue());
    }

    // --- GETTERS Y SETTERS ---
    public Queue<PCB> getColaListos() { return colaListos; }
    public Queue<PCB> getColaBloqueados() { return colaBloqueados; }
    public Queue<PCB> getColaListosSusp() { return colaListosSusp; }
    public Queue<PCB> getColaBloqSusp() { return colaBloqSusp; }
    public CPU getCpu() { return cpu; }
    public int getCicloReloj() { return cicloReloj; }
    public int getOcupacionMemoria() { return colaListos.getSize() + colaBloqueados.getSize() + (cpu.isBusy() ? 1 : 0); }
    public boolean isEjecutando() { return ejecutando; }
    
    public void setVelocidadSimulacion(int ms) { this.velocidadSimulacion = ms; }
    public void setAlgoritmo(String algo) { 
        this.algoritmoActual = algo; 
        reordenarColaListos();
    }
}