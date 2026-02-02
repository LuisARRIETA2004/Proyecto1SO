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

	public UUID getProcessID() {
		return processID;
	}

	public String getProcessName() {
		return processName;
	}

	public String getUser() {
		return user;
	}

	public ProcessState getState() {
		return state;
	}

	public int getPriority() {
		return priority;
	}

	public int getCyclesForException() {
		return cyclesForException;
	}

	public int getSatisfyExceptionCycles() {
		return satisfyExceptionCycles;
	}

	public int getProgramCounter() {
		return programCounter;
	}

	public int getRemainingInstructions() {
		return remainingInstructions;
	}

	public int getTimeInCpu() {
		return timeInCpu;
	}

	public int getStackPointer() {
		return stackPointer;
	}

	public int getTotalInstructions() {
		return totalInstructions;
	}

	public int getMemoryAddressRegister() {
		return memoryAddressRegister;
	}

	public String getProcessType() {
		return processType;
	}

	public int getMemSize() {
		return memSize;
	}

	public int getBASE_MEMORY() {
		return BASE_MEMORY;
	}

	public boolean isIoRequestFlag() {
		return ioRequestFlag;
	}

	public int getCyclesSpentBlocked() {
		return cyclesSpentBlocked;
	}

	public void setProcessID(UUID processID) {
		this.processID = processID;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setState(ProcessState state) {
		this.state = state;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setCyclesForException(int cyclesForException) {
		this.cyclesForException = cyclesForException;
	}

	public void setSatisfyExceptionCycles(int satisfyExceptionCycles) {
		this.satisfyExceptionCycles = satisfyExceptionCycles;
	}

	public void setProgramCounter(int programCounter) {
		this.programCounter = programCounter;
	}

	public void setRemainingInstructions(int remainingInstructions) {
		this.remainingInstructions = remainingInstructions;
	}

	public void setTimeInCpu(int timeInCpu) {
		this.timeInCpu = timeInCpu;
	}

	public void setStackPointer(int stackPointer) {
		this.stackPointer = stackPointer;
	}

	public void setTotalInstructions(int totalInstructions) {
		this.totalInstructions = totalInstructions;
	}

	public void setMemoryAddressRegister(int memoryAddressRegister) {
		this.memoryAddressRegister = memoryAddressRegister;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public void setMemSize(int memSize) {
		this.memSize = memSize;
	}

	public void setBASE_MEMORY(int BASE_MEMORY) {
		this.BASE_MEMORY = BASE_MEMORY;
	}

	public void setIoRequestFlag(boolean ioRequestFlag) {
		this.ioRequestFlag = ioRequestFlag;
	}

	public void setCyclesSpentBlocked(int cyclesSpentBlocked) {
		this.cyclesSpentBlocked = cyclesSpentBlocked;
	}


}

