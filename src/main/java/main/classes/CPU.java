/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

/**
 *
 * @author truenno
 */
public class CPU {
    
    private PCB currentProcess;
    private boolean busy;

    public CPU() {
        this.currentProcess = null;
        this.busy = false;
    }

    public void assignProcess(PCB pcb) {
        this.currentProcess = pcb;
        this.busy = true;
        
        // Actualizar el estado del proceso
        this.currentProcess.setEstado("Ejecucion");
        
        System.out.println("CPU: Loaded Process ID " + pcb.getId());
    }

    // Verifica si el proceso finalizo
    public boolean tick() {
        if (currentProcess == null) {
            return false;
        }
        return false; 
    }

    // Funcion de interrupciones para cuando llega un proceso con mayor prioridad
    public PCB releaseProcess() {
        if (currentProcess == null) {
            return null;
        }
        
        // Guardado de estado (WIP)
        PCB pcb = this.currentProcess;
        pcb.setEstado("Listo"); // Go back to Ready Queue
        
        System.out.println("CPU: Preempting Process ID " + pcb.getId());

        // vaciar CPU
        this.currentProcess = null;
        this.busy = false;
        
        return pcb; 
    }

    public boolean isBusy() {
        return busy;
    }

    public PCB getCurrentProcess() {
        return currentProcess;
    }
}	
