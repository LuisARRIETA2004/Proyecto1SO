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
        int instrucciones = 10 + random.nextInt(41); 
        int prioridad = 1 + random.nextInt(3);
        int deadline = (int) (instrucciones * (1.5 + random.nextDouble() * 1.5));
        
        // --- Lógica de E/S ---
        int cicloBloqueo = -1; // -1 significa "No se bloquea"
        int tiempoBloqueo = 0;
        
        // 50% de probabilidad de tener bloqueo
        if (random.nextDouble() > 0.5 && instrucciones > 5) { 
            // Se bloqueará en algún momento intermedio
            cicloBloqueo = 2 + random.nextInt(instrucciones - 4); 
            tiempoBloqueo = 3 + random.nextInt(5); // Duración de 3 a 7 ciclos
        }
        
        // Llamamos al constructor nuevo de PCB
        return new PCB(id, instrucciones, prioridad, deadline, cicloBloqueo, tiempoBloqueo);
    }
}
