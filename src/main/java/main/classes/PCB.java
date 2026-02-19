/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

public class PCB implements Runnable {

    private int id;
    private String estado;       
    private int programCounter;  
    private int mar;             

    // Datos para la planificación
    private int ciclosTotales;   
    private int ciclosRestantes; 
    private int prioridad;       
    private int deadline;        

    // --- NUEVO: Datos para Entrada/Salida ---
    private int cicloIrrupccionES; // En qué ciclo pide E/S
    private int longitudES;        // Cuánto tiempo tarda en E/S

    // Constructor Actualizado (Mantiene tu lógica + E/S)
    public PCB(int id, int ciclosTotales, int prioridad, int deadline, int cicloIrrupccionES, int longitudES) {
        this.id = id;
        this.ciclosTotales = ciclosTotales;
        this.ciclosRestantes = ciclosTotales; 
        this.prioridad = prioridad;
        this.deadline = deadline;
        
        // Inicializamos E/S
        this.cicloIrrupccionES = cicloIrrupccionES;
        this.longitudES = longitudES;

        this.estado = "Nuevo";
        this.programCounter = 0;
        this.mar = 0;
    }

    // --- Getters y Setters (Tus originales + los nuevos) ---
    public int getId() { return id; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public int getProgramCounter() { return programCounter; }
    public void setProgramCounter(int pc) { this.programCounter = pc; }
    public int getMar() { return mar; }
    public void setMar(int mar) { this.mar = mar; }
    public int getCiclosRestantes() { return ciclosRestantes; }
    public void setCiclosRestantes(int ciclos) { this.ciclosRestantes = ciclos; }
    public int getPrioridad() { return prioridad; }
    public int getDeadline() { return deadline; }
    public int getCiclosTotales() { return ciclosTotales; }

    // Nuevos getters para E/S
    public int getCicloIrrupccionES() { return cicloIrrupccionES; }
    public int getLongitudES() { return longitudES; }
    public void setLongitudES(int longitud) { this.longitudES = longitud; }

    @Override
    public String toString() {
        // Modificado para que se vea bonito en la lista de bloqueados
        if (estado.equals("Bloqueado")) {
             return "ID:" + id + " (E/S Restante: " + longitudES + ")";
        }
        return "ID:" + id + " | P:" + prioridad + " | Rest:" + ciclosRestantes;
    }

    // Tu método run (Se mantiene por estructura, pero no lo llamaremos desde el simulador visual)
    @Override
    public void run() {
        try {
            while (this.ciclosRestantes > 0) {
                Thread.sleep(1000);
                this.ciclosRestantes--;
                this.programCounter++;
                System.out.println("Proceso " + id + " Intervalos restantes: " + ciclosRestantes);
            }
            this.estado = "Terminado"; 
        } catch (InterruptedException e) {
            System.out.println("Proceso" + id + " En PAUSA.");
        }
    }
}