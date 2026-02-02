/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.classes;
import java.util.UUID;
/**
 *
 * @author truenno
 */
public class PCB {
	

    public enum ProcessState {
        NEW,
        READY,
        RUNNING,
        BLOCKED,
        FINISHED,
        READY_SUSPENDED,
        BLOCKED_SUSPENDED
    }

    private UUID processID;
    private String processName;
    private String user;

    private ProcessState state;
    private int priority;
    private int cyclesForException; //  I/0 bound 
    private int satisfyExceptionCycles; //  I/0 bound 

    private int programCounter;
    private int remainingInstructions;
    private int timeInCpu;
    private int stackPointer;
    private int totalInstructions;
    private int memoryAddressRegister;
    private String processType;

    private int memSize; //Tamano para cada proceso.
    private int BASE_MEMORY = 64;
    private boolean ioRequestFlag = false;
    private int cyclesSpentBlocked = 0;      // Cronómetro para el tiempo en estado de bloqueo cuando es i/o bound 
}
