package main.classes;

import com.mycompany.satellite.Helper.GeneradorProcesos;
import com.mycompany.satellite.Helper.Queue;
import com.mycompany.satellite.gui.VentanaSimulacion;
import javax.swing.SwingUtilities;

// 1. IMPORTAMOS EL SEMÁFORO (Requisito del PDF)
import java.util.concurrent.Semaphore; 

public class Planificador {
    
    // --- Referencia a la GUI ---
    private VentanaSimulacion ventana;

    // --- SEMÁFORO PARA EXCLUSIÓN MUTUA ---
    // El '1' significa que solo 1 hilo puede acceder a las colas al mismo tiempo
    private Semaphore mutex = new Semaphore(1);

    // --- Recursos del Sistema ---
    private CPU cpu;
    private final int MAX_MEMORIA = 10;
    
    // --- Colas ---
    private Queue<PCB> colaListos;
    private Queue<PCB> colaBloqueados;
    private Queue<PCB> colaListosSusp;
    private Queue<PCB> colaBloqSusp;
    
    // --- Control de Simulación ---
    private boolean ejecutando = false;
    private Thread hiloSimulacion;
    private int cicloReloj = 0;
    private int contadorIds = 1;
    private int velocidadSimulacion = 1000; // ms
    
    // --- Configuración Algoritmos ---
    private String algoritmoActual = "FCFS";
    private int quantum = 5;
    private int contadorQuantum = 0;

    // --- Estadísticas (Para la Tasa de Éxito del PDF) ---
    private int procesosExitosos = 0;
    private int procesosFallidos = 0;
    
    public Planificador() {
        this.cpu = new CPU();
        this.colaListos = new Queue<>();
        this.colaBloqueados = new Queue<>();
        this.colaListosSusp = new Queue<>();
        this.colaBloqSusp = new Queue<>();
    }
    
    public void setVentana(VentanaSimulacion ventana) {
        this.ventana = ventana;
    }

    // =========================================================
    // MOTOR DE HILOS (Con Semáforos)
    // =========================================================
    public void toggleSimulacion() {
        if (ejecutando) detener();
        else iniciar();
    }

    private void iniciar() {
        if (ejecutando) return;
        ejecutando = true;
        if (ventana != null) ventana.setEstadoBotonStart("DETENER");

        hiloSimulacion = new Thread(() -> {
            while (ejecutando) {
                if (cicloReloj % 20 == 0) {
                        // Parámetros: ID, Ciclos Totales (5), Prioridad (1), Deadline (CicloActual + 15), E/S (-1, 0)
                        PCB monitoreo = new PCB(contadorIds++, 5, 1, cicloReloj + 15, -1, 0);
                        monitoreo.setEstado("Listo");
                        System.out.println("SISTEMA: Tarea periódica de monitoreo generada.");
                        agregarProceso(monitoreo);
                    }
                try {
                    cicloReloj++;
                    
                    // 🔒 CERRAMOS EL CANDADO (Exclusión mutua)
                    mutex.acquire();
                    ejecutarCicloLogico();
                    mutex.release(); 
                    // 🔓 ABRIMOS EL CANDADO
                    
                    if (ventana != null) {
                        SwingUtilities.invokeLater(() -> ventana.actualizarInterfaz());
                    }

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
    
    // =========================================================
    // LÓGICA DE GESTIÓN (Eventos de botones protegidos)
    // =========================================================
    
    public void crearProcesosIniciales() {
        try {
            mutex.acquire(); // 🔒
            for (int i = 0; i < 5; i++) generarProcesoAleatorio();
            mutex.release(); // 🔓
        } catch (Exception e) {}
    }
    
    public void generarProcesosMasivos() {
        try {
            mutex.acquire(); // 🔒
            for (int i = 0; i < 20; i++) generarProcesoAleatorio();
            mutex.release(); // 🔓
        } catch (Exception e) {}
    }
    
    public void generarEmergencia() {
        try {
            mutex.acquire();
            PCB nuevo = GeneradorProcesos.generarProcesoAleatorio(contadorIds++);
            nuevo.setPrioridad(0); // Máxima prioridad
            nuevo.setEstado("INTERRUPCIÓN");

            // 1. Si hay alguien en CPU, lo sacamos a la fuerza
            if (cpu.isBusy()) {
                PCB expulsado = cpu.releaseProcess();
                expulsado.setEstado("Listo");
                colaListos.enqueue(expulsado); // El viejo vuelve a la cola
            }

            // 2. Metemos la emergencia DIRECTO a la CPU
            cpu.assignProcess(nuevo);
            
            System.out.println("!!! EMERGENCIA !!! Proceso " + nuevo.getId() + " tomó la CPU.");
            mutex.release();
        } catch (Exception e) {}
    }
    public void setAlgoritmo(String algo) { 
        try {
            mutex.acquire(); // 🔒
            this.algoritmoActual = algo; 
            reordenarColaListos();
            mutex.release(); // 🔓
        } catch (Exception e) {}
    }

    // =========================================================
    // LÓGICA INTERNA (Ya está protegida por los métodos de arriba)
    // =========================================================

    private void generarProcesoAleatorio() {
        PCB nuevo = GeneradorProcesos.generarProcesoAleatorio(contadorIds++);
        agregarProceso(nuevo);
    }

    private void ejecutarCicloLogico() {
        gestionarBloqueados();
        
        if (cpu.isBusy()) {
            PCB p = cpu.getCurrentProcess();
            
            // 1. Bloqueo por E/S
            if (p.getCicloIrrupccionES() == p.getCiclosRestantes()) {
                p.setEstado("Bloqueado");
                colaBloqueados.enqueue(p);
                cpu.releaseProcess();
                contadorQuantum = 0;
            } 
            else {
                // 2. Ejecutar instrucción
                cpu.ejecutarInstruccion();
                if (algoritmoActual.equals("Round Robin")) contadorQuantum++;

                // 3. Chequeo de Terminación
                if (p.getCiclosRestantes() <= 0) {
                    p.setEstado("Terminado");
                    
                    // REQUISITO PDF: Evaluar Deadline
                    if (cicloReloj <= p.getDeadline()) {
                        procesosExitosos++;
                        System.out.println("Éxito: Proceso " + p.getId() + " cumplió el deadline.");
                    } else {
                        procesosFallidos++;
                        System.out.println("Fallo: Proceso " + p.getId() + " rompió el deadline.");
                    }
                    
                    cpu.releaseProcess();
                    contadorQuantum = 0;
                    checkSwapping();
                }
                // 4. Chequeo Round Robin
                else if (algoritmoActual.equals("Round Robin") && contadorQuantum >= quantum) {
                    p.setEstado("Listo");
                    encolarSegunAlgoritmo(p);
                    cpu.releaseProcess();
                    contadorQuantum = 0;
                }
                // 5. Chequeo SRT / Prioridad / EDF
                else {
                    checkExpropiacion(); // ¡AQUÍ ESTÁ EL METODO QUE FALTABA!
                }
            }
        }
        
        // Dispatcher
        if (!cpu.isBusy() && !colaListos.isEmpty()) {
            PCB siguiente = colaListos.dequeue();
            cpu.assignProcess(siguiente);
            contadorQuantum = 0;
        }
    }
    
    public void agregarProceso(PCB nuevo) {
        int ocupados = getOcupacionMemoria();
        
        if (ocupados < MAX_MEMORIA) {
            // Hay espacio normal
            nuevo.setEstado("Listo");
            encolarSegunAlgoritmo(nuevo);
        } else {
            // RAM LLENA: El Planificador de Mediano Plazo entra en acción
            if (!colaBloqueados.isEmpty()) {
                // Inteligencia: Si hay alguien bloqueado, lo botamos al Disco
                // porque de todas formas no puede usar la CPU ahorita.
                PCB victima = colaBloqueados.dequeue();
                victima.setEstado("Bloqueado-Suspendido");
                colaBloqSusp.enqueue(victima);
                System.out.println("Swapping: Proceso " + victima.getId() + " movido a Disco (Bloqueado-Suspendido)");

                // Ahora que liberamos 1 espacio, metemos el nuevo a RAM
                nuevo.setEstado("Listo");
                encolarSegunAlgoritmo(nuevo);
            } else {
                // Si la RAM está llena y nadie está bloqueado, el nuevo se va directo a Disco
                nuevo.setEstado("Listo-Suspendido");
                colaListosSusp.enqueue(nuevo);
            }
        }
    }
    
    private void encolarSegunAlgoritmo(PCB p) {
        if (algoritmoActual.equals("SRT") || algoritmoActual.equals("Prioridad") || algoritmoActual.equals("EDF")) {
            colaListos.enqueueOrdenado(p, algoritmoActual);
        } else {
            colaListos.enqueue(p);
        }
    }
    
    private void checkSwapping() {
        if (getOcupacionMemoria() < MAX_MEMORIA && !colaListosSusp.isEmpty()) {
            agregarProceso(colaListosSusp.dequeue());
        }
    }
    
    private void gestionarBloqueados() {
        // 1. Reducir tiempo de los que están en RAM (Cola Bloqueados)
        if (!colaBloqueados.isEmpty()) {
            int n = colaBloqueados.getSize();
            for(int i=0; i<n; i++) {
                PCB p = colaBloqueados.dequeue();
                p.setLongitudES(p.getLongitudES() - 1);
                if (p.getLongitudES() <= 0) {
                    agregarProceso(p); // Intenta volver a RAM
                } else {
                    colaBloqueados.enqueue(p);
                }
            }
        }

        // 2. NUEVO: Reducir tiempo de los que están en DISCO (Bloqueado-Suspendido)
        if (!colaBloqSusp.isEmpty()) {
            int m = colaBloqSusp.getSize();
            for(int i=0; i<m; i++) {
                PCB p = colaBloqSusp.dequeue();
                p.setLongitudES(p.getLongitudES() - 1);
                
                if (p.getLongitudES() <= 0) {
                    // Terminó su I/O en el disco, pero sigue en el disco. Pasa a Listo-Suspendido.
                    p.setEstado("Listo-Suspendido");
                    colaListosSusp.enqueue(p);
                    checkSwapping(); // Verificamos si de casualidad se liberó espacio en RAM
                } else {
                    colaBloqSusp.enqueue(p); // Sigue bloqueado en disco
                }
            }
        }
    }
    
    // MÉTODO RESTAURADO PARA QUITAR EL ERROR
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
            actual.setEstado("Listo");
            encolarSegunAlgoritmo(actual);
        }
    }

    public void reordenarColaListos() {
        int n = colaListos.getSize();
        Queue<PCB> temp = new Queue<>();
        for(int i=0; i<n; i++) temp.enqueue(colaListos.dequeue());
        while(!temp.isEmpty()) encolarSegunAlgoritmo(temp.dequeue());
    }

    // --- GETTERS ---
    public Queue<PCB> getColaListos() { return colaListos; }
    public Queue<PCB> getColaBloqueados() { return colaBloqueados; }
    public Queue<PCB> getColaListosSusp() { return colaListosSusp; }
    public Queue<PCB> getColaBloqSusp() { return colaBloqSusp; }
    public CPU getCpu() { return cpu; }
    public int getCicloReloj() { return cicloReloj; }
    public int getOcupacionMemoria() { return colaListos.getSize() + colaBloqueados.getSize() + (cpu.isBusy() ? 1 : 0); }
    public boolean isEjecutando() { return ejecutando; }
    public void setVelocidadSimulacion(int ms) { this.velocidadSimulacion = ms; }
    
    public int getProcesosExitosos() { return procesosExitosos; }
    public int getProcesosFallidos() { return procesosFallidos; }
}