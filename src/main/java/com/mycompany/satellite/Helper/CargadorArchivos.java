/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.satellite.Helper;

import java.io.BufferedReader;
import java.io.FileReader;
import main.classes.PCB;
import main.classes.Planificador;

public class CargadorArchivos {

    /**
     * Lee un archivo CSV y mete los procesos en el Kernel.
     * Formato esperado: ID,Ciclos,Prioridad,Deadline,CicloIrrupcion,LongitudES
     */
    public static void cargarDesdeCSV(String rutaAbsoluta, Planificador kernel) {
        System.out.println("Intentando cargar archivo: " + rutaAbsoluta);
        
        try (BufferedReader br = new BufferedReader(new FileReader(rutaAbsoluta))) {
            String linea;
            boolean primeraLinea = true;
            
            while ((linea = br.readLine()) != null) {
                // Saltar la primera línea si son los encabezados (ej: "id,ciclos,prioridad...")
                if (primeraLinea) { 
                    primeraLinea = false; 
                    continue; 
                }
                
                String[] datos = linea.split(",");
                
                // Asegurarnos de que la línea tiene los 6 datos necesarios
                if (datos.length >= 6) {
                    try {
                        int id = Integer.parseInt(datos[0].trim());
                        int ciclos = Integer.parseInt(datos[1].trim());
                        int prio = Integer.parseInt(datos[2].trim());
                        int dead = Integer.parseInt(datos[3].trim());
                        int irrup = Integer.parseInt(datos[4].trim());
                        int longES = Integer.parseInt(datos[5].trim());
                        
                        // Crear el PCB y dárselo al Kernel
                        PCB nuevoPcb = new PCB(id, ciclos, prio, dead, irrup, longES);
                        kernel.agregarProceso(nuevoPcb);
                        
                    } catch (NumberFormatException ex) {
                        System.out.println("Error en los números de la línea: " + linea);
                    }
                }
            }
            System.out.println("¡Archivo cargado con éxito!");
            
        } catch (Exception e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }
}