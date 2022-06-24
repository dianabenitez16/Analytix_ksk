/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analytix;

import formularios.comprobantes.CFReimpresion;
import formularios.informes.ICLibroVenta;
import formularios.comprobantes.CFVerificador;
import java.awt.Color;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import system.Consola;

/**
 *
 * @author juan.bogado
 */
public class Analytix extends javax.swing.JFrame {
    public final static boolean DEBUG = true;
    
    public static double JAVA_VERSION;
    public static int JAVA_MODEL;
    public boolean JAVA_VALID = true;
    
    public boolean DVY_VALID = true;
    
    public Connection conexion = null;
    public Statement sentencia = null;
    public ResultSet resultado = null;
    
    CFVerificador cfv;
    CFReimpresion cfr;
    ICLibroVenta iclv;
            
    public Analytix() {
        initComponents();
        
        menu.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F10"), "none");
        
        version();
        
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            conexion = DriverManager.getConnection("jdbc:odbc:Discovery", "", "");
            if(DEBUG){
                Consola.out(Color.green, "Conectado!");
                System.out.println("Catalog: "+conexion.getCatalog());
                System.out.println("StringFunctions: "+conexion.getMetaData().getStringFunctions());
                System.out.println("SQLKeywords: "+conexion.getMetaData().getSQLKeywords());
                System.out.println("SearchStringEscape: "+conexion.getMetaData().getSearchStringEscape());
                System.out.println("CatalogTerm: "+conexion.getMetaData().getCatalogTerm());
                System.out.println("Catalog: "+conexion.getMetaData().getConnection().getCatalog());
                System.out.println("DatabaseProductName: "+conexion.getMetaData().getDatabaseProductName());
                System.out.println("SystemFunctions: "+conexion.getMetaData().getSystemFunctions());
                System.out.println("StringFunctions: "+conexion.getMetaData().getStringFunctions());
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Analytix.class.getName()).log(Level.SEVERE, null, ex);
            eDiscovery.setText("Error");
            eDiscovery.setIcon(null);
            eDiscovery.setToolTipText(ex.getMessage());
        }
        
        /*
        if(DEBUG){
            cfr = new CFReimpresion();
            agregar(cfr);
        }
        */
        
    }
    
    public void agregar(JInternalFrame ventana){
        if(!ventana.isDisplayable())
            dp.add(ventana);
        
        int x = (dp.getWidth()/2) - ventana.getWidth()/2;
        int y = (dp.getHeight()/2) - ventana.getHeight()/2;
        ventana.setLocation(x,y);
        ventana.toFront();
        ventana.setVisible(true);
        try {
            ventana.setSelected(true);
        } catch (PropertyVetoException ex) {
            System.out.println("No se pudo seleccionar");
        }
    }
    
    private void version() {
        String version = System.getProperty("java.version");
        int pos = version.indexOf('.');
        pos = version.indexOf('.', pos+1);
        JAVA_VERSION = Double.parseDouble (version.substring (0, pos));
        JAVA_MODEL = Integer.parseInt(System.getProperty("sun.arch.data.model"));
        
        eJavaVersion.setText("JRE "+String.valueOf(JAVA_VERSION));
        eJavaArchitecture.setText("x"+String.valueOf(JAVA_MODEL));
        
        if(JAVA_VERSION > 1.8){
            eMensaje.setText("Version no compatible con ODBC.");
            JAVA_VALID = false;
        }else if(JAVA_VERSION < 1.7){
            eMensaje.setText("Version desactualizada de JRE.");
        }
        
        if(JAVA_MODEL !=32){
            eMensaje.setText("Arquitectura no compatible.");
            JAVA_VALID = false;
        }
        
        //JAVA_VALID = true; // debug
        
        if(JAVA_VALID){
            menu.getMenu(0).setEnabled(true);
            menu.getMenu(1).setEnabled(true);
            
            if(DEBUG)
                Consola.out(Color.green, "Version de java valido");
        }else{
            JOptionPane.showMessageDialog(this, "La version de java o la arquitectura no son válidos, instale unicamente JRE7x32", "Version no compatible: JRE"+JAVA_VERSION+" x"+JAVA_MODEL, JOptionPane.ERROR_MESSAGE);
        }
        
        if(DEBUG)
        System.out.println(System.getProperties());
        
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dp = new javax.swing.JDesktopPane();
        estado = new javax.swing.JPanel();
        eMensaje = new javax.swing.JLabel();
        eJavaVersion = new javax.swing.JLabel();
        eDiscovery = new javax.swing.JLabel();
        eJavaArchitecture = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Analytix - Discovery");
        setMinimumSize(new java.awt.Dimension(640, 480));

        javax.swing.GroupLayout dpLayout = new javax.swing.GroupLayout(dp);
        dp.setLayout(dpLayout);
        dpLayout.setHorizontalGroup(
            dpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1024, Short.MAX_VALUE)
        );
        dpLayout.setVerticalGroup(
            dpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 727, Short.MAX_VALUE)
        );

        eMensaje.setText("Analytix");

        eJavaVersion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/media/ico/java16.png"))); // NOI18N
        eJavaVersion.setText("JRE 1.8");
        eJavaVersion.setPreferredSize(new java.awt.Dimension(40, 14));

        eDiscovery.setIcon(new javax.swing.ImageIcon(getClass().getResource("/media/ico/DISCV5x16.png"))); // NOI18N
        eDiscovery.setPreferredSize(new java.awt.Dimension(40, 14));

        eJavaArchitecture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/media/ico/database_gear.png"))); // NOI18N
        eJavaArchitecture.setText("x64");
        eJavaArchitecture.setPreferredSize(new java.awt.Dimension(40, 14));

        javax.swing.GroupLayout estadoLayout = new javax.swing.GroupLayout(estado);
        estado.setLayout(estadoLayout);
        estadoLayout.setHorizontalGroup(
            estadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(estadoLayout.createSequentialGroup()
                .addComponent(eMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(eJavaVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(eJavaArchitecture, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(eDiscovery, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        estadoLayout.setVerticalGroup(
            estadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, estadoLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(estadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(eDiscovery, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(estadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(eMensaje)
                        .addComponent(eJavaVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(eJavaArchitecture, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jMenu1.setText("Comprobantes");
        jMenu1.setEnabled(false);

        jMenu3.setText("Facturas");

        jMenuItem1.setText("Verificador");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem1);

        jMenuItem3.setText("Reimpresión");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem3);

        jMenu1.add(jMenu3);

        menu.add(jMenu1);

        jMenu2.setText("Informes");
        jMenu2.setEnabled(false);

        jMenu4.setText("Contables");

        jMenuItem2.setText("Libro de ventas");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem2);

        jMenu2.add(jMenu4);

        menu.add(jMenu2);

        setJMenuBar(menu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dp)
            .addComponent(estado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(dp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(estado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        cfv = new CFVerificador();
        agregar(cfv);
        
            }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        iclv = new ICLibroVenta();
        agregar(iclv);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        cfr = new CFReimpresion();
        agregar(cfr);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

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
            java.util.logging.Logger.getLogger(Analytix.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Analytix.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Analytix.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Analytix.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Analytix().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane dp;
    private javax.swing.JLabel eDiscovery;
    private javax.swing.JLabel eJavaArchitecture;
    private javax.swing.JLabel eJavaVersion;
    private javax.swing.JLabel eMensaje;
    private javax.swing.JPanel estado;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuBar menu;
    // End of variables declaration//GEN-END:variables
}
