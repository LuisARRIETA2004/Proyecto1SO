/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.satellite;

import com.mycompany.satellite.gui.VentanaSimulacion;

public class Satellite {

    public static void main(String[] args) {
        
        java.awt.EventQueue.invokeLater(() -> {
            new VentanaSimulacion().setVisible(true);
        });
    }
}
