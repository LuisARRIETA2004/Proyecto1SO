/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.satellite.Helper;

import main.classes.PCB;
import java.util.Random;

public class GeneradorProcesos {
    
    private static final Random random = new Random();

    public static PCB generarProcesoAleatorio(int id) {
        int instrucciones = 15 + random.nextInt(30); 
        int prioridad = 1 + random.nextInt(3);
        int deadline = (int) (instrucciones * 2);
        
        // --- FORZAMOS E/S AL 80% DE PROBABILIDAD ---
        int cicloBloqueo = 2 + random.nextInt(8); // Se bloquea rápido, entre el ciclo 2 y 10
        int tiempoBloqueo = 5 + random.nextInt(5); // Dura entre 5 y 10 ciclos bloqueado
        
        return new PCB(id, instrucciones, prioridad, deadline, cicloBloqueo, tiempoBloqueo);
    }
}
