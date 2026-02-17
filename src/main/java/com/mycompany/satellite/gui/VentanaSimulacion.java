package com.mycompany.satellite.gui;

// Importaciones necesarias
import com.mycompany.satellite.Helper.Queue;
import com.mycompany.satellite.Helper.GeneradorProcesos;
import main.classes.PCB;
import javax.swing.DefaultListModel;
import java.awt.Color;

/**
 * Ventana Principal del Simulador RTOS
 */
public class VentanaSimulacion extends javax.swing.JFrame {
    
    private String algoritmoActual = "FCFS";

    // --- ESTRUCTURAS DE DATOS (Backend) ---
    private Queue<PCB> colaListos;
    private Queue<PCB> colaBloqueados;
    private Queue<PCB> colaListosSusp;
    private Queue<PCB> colaBloqSusp;
    
    // --- MODELOS VISUALES (Frontend) ---
    private DefaultListModel<String> modeloListos;
    private DefaultListModel<String> modeloBloqueados;
    private DefaultListModel<String> modeloListosSusp;
    private DefaultListModel<String> modeloBloqSusp;
    private PCB procesoEnCPU = null;
    
    // --- VARIABLES DE CONTROL ---
    private int contadorIds = 1;
    private int cicloReloj = 0;
    private final int MAX_MEMORIA = 10; // Capacidad máxima de la RAM simulada
    
    // --- VARIABLES DEL MOTOR (HILOS) ---
    private boolean ejecutando = false; // Bandera para saber si corre o no
    private Thread hiloSimulacion;      // El hilo que actualizará el reloj
    /**
     * Constructor: Inicializa todo
     */
    public VentanaSimulacion() {
        // 1. Inicializar Colas
        colaListos = new Queue<>();
        colaBloqueados = new Queue<>();
        colaListosSusp = new Queue<>();
        colaBloqSusp = new Queue<>();

        // 2. Inicializar Modelos de Listas
        modeloListos = new DefaultListModel<>();
        modeloBloqueados = new DefaultListModel<>();
        modeloListosSusp = new DefaultListModel<>();
        modeloBloqSusp = new DefaultListModel<>();

        // 3. Cargar Diseño Visual
        initComponents();
        personalizarDiseño();

        // 4. Conectar Modelos a las Listas Visuales
        listReadyQueue.setModel(modeloListos);
        listBlockedQueue.setModel(modeloBloqueados);
        listListosSuspendidos.setModel(modeloListosSusp);
        listBloqueadosSuspendidos.setModel(modeloBloqSusp);

        // 5. Carga Inicial Automática (Requisito PDF)
        cargarProcesosIniciales();
    }
    
    private void personalizarDiseño() {
        this.setLocationRelativeTo(null); // Centrar ventana
        // Configurar colores básicos si no se hizo en el diseñador
        this.getContentPane().setBackground(new Color(0, 0, 51));
    }

    // ---------------------------------------------------------
    // LÓGICA DE NEGOCIO
    // ---------------------------------------------------------

    private void cargarProcesosIniciales() {
        // Crear 5 procesos al inicio
        for (int i = 0; i < 5; i++) {
            PCB nuevo = GeneradorProcesos.generarProcesoAleatorio(contadorIds++);
            nuevo.setEstado("Listo");
            colaListos.enqueue(nuevo);
        }
        actualizarInterfaz();
    }

    /**
     * Refresca TODOS los elementos visuales basándose en las colas reales
     */
    private void llenarModelo(DefaultListModel<String> modelo, Queue<PCB> cola) {
        modelo.clear();
        for (int i = 0; i < cola.getSize(); i++) {
            PCB p = cola.get(i);
            if (p != null) {
                modelo.addElement(p.toString());
            }
        }
    }
    public void actualizarInterfaz() {
        // 1. Actualizar Reloj
        lblReloj.setText("MISSION CLOCK: Cycle " + cicloReloj);

        // 2. Actualizar Listas
        llenarModelo(modeloListos, colaListos);
        llenarModelo(modeloBloqueados, colaBloqueados);
        llenarModelo(modeloListosSusp, colaListosSusp);
        llenarModelo(modeloBloqSusp, colaBloqSusp);

        // 3. Actualizar Barra de Memoria RAM
        int ocupados = colaListos.getSize() + colaBloqueados.getSize();
        if (procesoEnCPU != null) ocupados++; // Contar también el de CPU
        
        int porcentaje = (ocupados * 100) / MAX_MEMORIA;
        barraMemoria.setValue(porcentaje);
        barraMemoria.setString(ocupados + "/" + MAX_MEMORIA + " Procesos (" + porcentaje + "%)");
        
        if (porcentaje >= 100) barraMemoria.setForeground(Color.RED);
        else barraMemoria.setForeground(Color.GREEN);
        
        // 4. ACTUALIZAR PANEL CPU (Running Process)
        if (procesoEnCPU != null) {
            lblCpuId.setText(String.valueOf(procesoEnCPU.getId()));
            lblCpuEstado.setText(procesoEnCPU.getEstado());
            lblCpuPC.setText(String.valueOf(procesoEnCPU.getProgramCounter()));
            lblCpuMAR.setText(String.valueOf(procesoEnCPU.getMar()));
            // lblCpuCiclos.setText(String.valueOf(procesoEnCPU.getCiclosRestantes())); // Si tienes este label
        } else {
            lblCpuId.setText("---");
            lblCpuEstado.setText("IDLE"); // Ocioso
            lblCpuPC.setText("---");
            lblCpuMAR.setText("---");
        }
    }
    // ---------------------------------------------------------
    // ACCIONES DE BOTONES (Conectar en Design)
    // ---------------------------------------------------------
    // ---------------------------------------------------------
    // ACCIONES DE BOTONES (Conectar en Design)
    // ---------------------------------------------------------


    private void btnEmergenciaActionPerformed(java.awt.event.ActionEvent evt) {                                              
        PCB nuevo = GeneradorProcesos.generarProcesoAleatorio(contadorIds++);
        
        // Calcular espacio en RAM (Listos + Bloqueados + El que esté en CPU)
        int ocupados = colaListos.getSize() + colaBloqueados.getSize();
        if (procesoEnCPU != null) ocupados++;

        // Lógica de Memoria (Swap)
        if (ocupados < MAX_MEMORIA) {
            nuevo.setEstado("Listo");
            colaListos.enqueue(nuevo);
        } else {
            // Si la RAM está llena, la emergencia va a Disco (o podrías programar expulsión)
            nuevo.setEstado("Listo-Suspendido");
            colaListosSusp.enqueue(nuevo);
        }
        
        actualizarInterfaz();
    }                                             

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblReloj = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listReadyQueue = new javax.swing.JList<>();
        jPanel4 = new javax.swing.JPanel();
        barraMemoria = new javax.swing.JProgressBar();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listBlockedQueue = new javax.swing.JList<>();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        listListosSuspendidos = new javax.swing.JList<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        listBloqueadosSuspendidos = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblCpuId = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblCpuEstado = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblCpuPC = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblCpuMAR = new javax.swing.JLabel();
        btnStart = new javax.swing.JButton();
        btnGenerar20 = new javax.swing.JButton();
        btnEmergencia = new javax.swing.JButton();
        comboAlgoritmos = new javax.swing.JComboBox<>();
        spinnerVelocidad = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 51));

        jPanel1.setBackground(new java.awt.Color(0, 0, 51));

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));

        lblReloj.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblReloj.setForeground(new java.awt.Color(0, 255, 0));
        lblReloj.setText("MISSION CLOCK: Cycle 0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addComponent(lblReloj)
                .addContainerGap(87, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(lblReloj)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Ready Queue", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 14))); // NOI18N

        listReadyQueue.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(listReadyQueue);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 8, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Main Memory", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 14))); // NOI18N

        barraMemoria.setStringPainted(true);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(barraMemoria, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(barraMemoria, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Blocked Queue", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 14))); // NOI18N

        listBlockedQueue.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(listBlockedQueue);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Swap Space", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 14))); // NOI18N

        listListosSuspendidos.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(listListosSuspendidos);

        listBloqueadosSuspendidos.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(listBloqueadosSuspendidos);

        jLabel1.setText("Ready-Suspended");

        jLabel2.setText("Blocked-Suspended");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(96, 96, 96)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(85, 85, 85))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Running Process", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 14))); // NOI18N

        jLabel3.setText("ID:");

        lblCpuId.setText("---");

        jLabel5.setText("Estado:");

        lblCpuEstado.setText("---");

        jLabel4.setText("PC:");

        lblCpuPC.setText("---");

        jLabel6.setText("MAR:");

        lblCpuMAR.setText("---");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCpuId))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCpuEstado))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCpuPC))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCpuMAR)))
                .addContainerGap(208, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(lblCpuId))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblCpuEstado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblCpuPC))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblCpuMAR))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        btnStart.setText("INICIAR");
        btnStart.setMinimumSize(new java.awt.Dimension(70, 20));
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        btnGenerar20.setText("GENERAR 20");
        btnGenerar20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerar20ActionPerformed(evt);
            }
        });

        btnEmergencia.setText("EMERGENCIA");

        comboAlgoritmos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FCFS", "Round Robin", "SPN", "SRT", "HRRN" }));
        comboAlgoritmos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboAlgoritmosActionPerformed(evt);
            }
        });

        spinnerVelocidad.setModel(new javax.swing.SpinnerNumberModel(1000, 100, null, 100));

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Velocidad (ms)");

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Politica");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(182, 182, 182))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(comboAlgoritmos, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGenerar20, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnStart, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .addComponent(spinnerVelocidad))
                .addGap(95, 95, 95)
                .addComponent(btnEmergencia, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(76, 76, 76))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(265, 265, 265))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(9, 9, 9)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGenerar20, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEmergencia, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinnerVelocidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboAlgoritmos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(53, 53, 53))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGenerar20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerar20ActionPerformed
         System.out.println("--- Generando lote de 20 procesos ---");

        for (int i = 0; i < 20; i++) {
            // 1. Crear el proceso usando la clase Helper
            main.classes.PCB nuevo = com.mycompany.satellite.Helper.GeneradorProcesos.generarProcesoAleatorio(contadorIds++);

            // 2. Calcular cuántos procesos hay actualmente en RAM
            int procesosEnRAM = colaListos.getSize() + colaBloqueados.getSize();

            // 3. Decidir dónde guardar el proceso
            if (procesosEnRAM < MAX_MEMORIA) {
                // Si hay espacio en RAM (menor a 10), va a la cola de Listos
                nuevo.setEstado("Listo");
                colaListos.enqueue(nuevo);
            } else {
                // Si la RAM está llena, va al Disco (Swap) -> Listo-Suspendido
                nuevo.setEstado("Listo-Suspendido");
                colaListosSusp.enqueue(nuevo);
            }
        }

        // 4. Actualizar toda la interfaz visual
        actualizarInterfaz();
    }//GEN-LAST:event_btnGenerar20ActionPerformed

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        if (!ejecutando) {
            iniciarMotor();
        } else {
            detenerMotor();
        }        // TODO add your handling code here:
    }//GEN-LAST:event_btnStartActionPerformed

    private void comboAlgoritmosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboAlgoritmosActionPerformed
         // 1. Capturar qué seleccionó el usuario
        algoritmoActual = (String) comboAlgoritmos.getSelectedItem(); // <--- CORREGIDO
        System.out.println(">>> Cambio de Política: " + algoritmoActual);
        
        switch (algoritmoActual) {
            case "FCFS":
                break;
                
            case "Round Robin":
                break;
                
            case "SPN": // Shortest Process Next
                break;
                
            case "SRT":
                break;
                
            case "HRRN":
                break;
        }
        
        // Refrescar la pantalla para ver si el orden cambió
        actualizarInterfaz();
    }//GEN-LAST:event_comboAlgoritmosActionPerformed
        private void iniciarMotor() {
        if (ejecutando) return; 

        ejecutando = true;
        btnStart.setText("DETENER");

        hiloSimulacion = new Thread(() -> {
            while (ejecutando) {
                try {
                    // 1. Avanzar reloj
                    cicloReloj++;
                    
                    // --- LÓGICA DEL KERNEL (SIMULADA) ---
                    
                    // A. Si no hay nadie en CPU, buscamos en la cola de Listos
                    if (procesoEnCPU == null) {
                        if (!colaListos.isEmpty()) {
                            procesoEnCPU = colaListos.dequeue();
                            procesoEnCPU.setEstado("Ejecucion");
                        }
                    }
                    
                    // B. Si hay alguien en CPU, lo procesamos
                    if (procesoEnCPU != null) {
                        // Simular trabajo: Aumentar PC y MAR
                        procesoEnCPU.setProgramCounter(procesoEnCPU.getProgramCounter() + 1);
                        procesoEnCPU.setMar(procesoEnCPU.getMar() + 1);
                        procesoEnCPU.setCiclosRestantes(procesoEnCPU.getCiclosRestantes() - 1);
                        
                        // C. Verificar si terminó
                        if (procesoEnCPU.getCiclosRestantes() <= 0) {
                            procesoEnCPU.setEstado("Terminado");
                            // Aquí podrías guardarlo en una lista de terminados o archivo
                            procesoEnCPU = null; // Liberar CPU
                            
                            // D. Intentar traer alguien del Swap (Disco) a RAM si hay espacio
                            if (!colaListosSusp.isEmpty()) {
                                PCB recuperado = colaListosSusp.dequeue();
                                recuperado.setEstado("Listo");
                                colaListos.enqueue(recuperado);
                            }
                        }
                    }
                    // -------------------------------------

                    // 2. Actualizar visuales
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        actualizarInterfaz();
                    });

                    int velocidad = 1000; // Valor por defecto
                    try {
                        // Obtenemos el valor del diseño visual
                        velocidad = (Integer) spinnerVelocidad.getValue();
                    } catch (Exception e) {
                        velocidad = 1000; // Si falla, usamos 1 seg
                    }

                    Thread.sleep(velocidad); 

                } catch (InterruptedException e) {
                    System.out.println("Simulación interrumpida");
                }
            }
        });
        
        hiloSimulacion.start();
    }

    /**
     * Apaga el motor
     */
    private void detenerMotor() {
        ejecutando = false;
        btnStart.setText("INICIAR");
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaSimulacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaSimulacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaSimulacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaSimulacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaSimulacion().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barraMemoria;
    private javax.swing.JButton btnEmergencia;
    private javax.swing.JButton btnGenerar20;
    private javax.swing.JButton btnStart;
    private javax.swing.JComboBox<String> comboAlgoritmos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblCpuEstado;
    private javax.swing.JLabel lblCpuId;
    private javax.swing.JLabel lblCpuMAR;
    private javax.swing.JLabel lblCpuPC;
    private javax.swing.JLabel lblReloj;
    private javax.swing.JList<String> listBlockedQueue;
    private javax.swing.JList<String> listBloqueadosSuspendidos;
    private javax.swing.JList<String> listListosSuspendidos;
    private javax.swing.JList<String> listReadyQueue;
    private javax.swing.JSpinner spinnerVelocidad;
    // End of variables declaration//GEN-END:variables
}
