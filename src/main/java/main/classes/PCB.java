/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

public class PCB {
    private int id;
    private String estado;       
    private int programCounter;  
    private int mar;             

    // Planificación
    private int ciclosTotales;   
    private int ciclosRestantes; 
    private int prioridad;       
    private int deadline;        

    // Entrada/Salida
    private int cicloIrrupccionES; 
    private int longitudES;        

    public PCB(int id, int ciclosTotales, int prioridad, int deadline, int cicloIrrupccionES, int longitudES) {
        this.id = id;
        this.ciclosTotales = ciclosTotales;
        this.ciclosRestantes = ciclosTotales; 
        this.prioridad = prioridad;
        this.deadline = deadline;
        this.cicloIrrupccionES = cicloIrrupccionES;
        this.longitudES = longitudES;
        this.estado = "Nuevo";
        this.programCounter = 0;
        this.mar = 0;
    }

    // Getters y Setters
    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }
    
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
    public int getCicloIrrupccionES() { return cicloIrrupccionES; }
    public int getLongitudES() { return longitudES; }
    public void setLongitudES(int longitud) { this.longitudES = longitud; }

    @Override
    public String toString() {
        if ("Bloqueado".equals(estado)) return "ID:" + id + " (E/S: " + longitudES + ")";
        return "ID:" + id + " | P:" + prioridad + " | Rest:" + ciclosRestantes;
    }
}