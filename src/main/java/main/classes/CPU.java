/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;

public class CPU {
    private PCB currentProcess;
    
    public void assignProcess(PCB pcb) {
        this.currentProcess = pcb;
        if (pcb != null) pcb.setEstado("Ejecucion");
    }

    public PCB releaseProcess() {
        PCB temp = currentProcess;
        currentProcess = null;
        if(temp != null) temp.setEstado("Listo");
        return temp;
    }

    public boolean isBusy() { return currentProcess != null; }
    public PCB getCurrentProcess() { return currentProcess; }
    
    // Ejecuta 1 ciclo de instrucción
    public void ejecutarInstruccion() {
        if (currentProcess != null) {
            currentProcess.setProgramCounter(currentProcess.getProgramCounter() + 1);
            currentProcess.setMar(currentProcess.getMar() + 1);
            currentProcess.setCiclosRestantes(currentProcess.getCiclosRestantes() - 1);
        }
    }
}