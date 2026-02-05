/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.satellite.Helper;

import main.classes.PCB;
import java.util.Random;

public class GeneradorProcesos {
    
    private static final Random random = new Random();

    /**
     * Crea un proceso con valores aleatorios según reglas del PDF.
     * @param id El ID único que tendrá el proceso.
     * @return Un objeto PCB nuevo.
     */
    public static PCB generarProcesoAleatorio(int id) {
        // 1. Instrucciones (Ciclos): Aleatorio entre 10 y 50
        int instrucciones = 10 + random.nextInt(41); 
        
        // 2. Prioridad: Aleatorio entre 1 (Alta) y 3 (Baja)
        int prioridad = 1 + random.nextInt(3);
        
        // 3. Deadline: Debe ser mayor que las instrucciones.
        // Aquí decimos que el deadline es entre el 150% y 300% del tiempo de ejecución.
        // Ejemplo: Si dura 10 ciclos, el deadline será entre el ciclo 15 y 30 desde ahora.
        int deadline = (int) (instrucciones * (1.5 + random.nextDouble() * 1.5));
        
        return new PCB(id, instrucciones, prioridad, deadline);
    }
}
