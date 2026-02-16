/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;
import com.mycompany.satellite.Helper.Queue;

/**
 *
 * @author truenno
 */
public class Planificador {
private CPU cpu;
    private Queue<PCB> readyQueue;
    private String currentAlgo; // "SRT", "EDF", "FCFS"
    private int systemClock;

    public void simulacion() {
        if (systemClock == 5) {
            readyQueue.enqueueOrdenado(cpu.getCurrentProcess(),currentAlgo);
        }

        if (cpu.isBusy()) {
            
            PCB inCpu = cpu.getCurrentProcess();
            PCB bestInQueue = readyQueue.peek(); 

            if (bestInQueue != null) {
                if (readyQueue.shouldSwap(bestInQueue, inCpu, currentAlgo)) {
                    
                    PCB stopped = cpu.releaseProcess(); 
                    readyQueue.enqueueOrdenado(stopped, currentAlgo); 
                    
                    PCB next = readyQueue.dequeue(); 
                    cpu.assignProcess(next);         
                }
            }
        } else {
            if (!readyQueue.isEmpty()) {
                cpu.assignProcess(readyQueue.dequeue());
            }
        }

        if (cpu.isBusy()) {
            boolean finished = cpu.tick(); 
            if (finished) {
                System.out.println("Process completed!");
            }
        }

        systemClock++; 
    }	
}
