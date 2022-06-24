/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formularios.comprobantes;

import clases.FacturaX;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import system.Consola;

/**
 *
 * @author juan.bogado
 */
public class CFVerificador extends javax.swing.JInternalFrame {
    Connection conexion = null;
    Statement sentencia = null;
    ResultSet resultado = null;
    String consulta;
    Integer contador;
    
    FacturaX factura;
    
    KeyListener ikl, bkl;

    public CFVerificador() {
        initComponents();
        
        ikl = new java.awt.event.KeyAdapter() {
                @Override
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    switch (evt.getKeyCode()) {
                        case KeyEvent.VK_ESCAPE:
                            limpiar(true);
                            break;
                        case KeyEvent.VK_ENTER:
                            evt.getComponent().transferFocus();
                            break;
                        case KeyEvent.VK_F10:
                            buscar();
                            break;
                    }
                }
            };
        
        bkl = new java.awt.event.KeyAdapter() {
                @Override
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    switch (evt.getKeyCode()) {
                        case KeyEvent.VK_ESCAPE:
                            limpiar(true);
                            break;
                        case KeyEvent.VK_ENTER:
                            buscar();
                            break;
                        case KeyEvent.VK_F10:
                            buscar();
                            break;
                    }
                }
            };
        
        iTalonario.addKeyListener(ikl);
        iNumero.addKeyListener(ikl);
        mBuscar.addKeyListener(bkl);
        
    }
    
    public void limpiar(Boolean full){
        if(full){
            iTalonario.setText("");
            iNumero.setText("");
            iTalonario.requestFocus();
        }
        vFecha.setText("");
        vAnulado.setText("");
        vCaja.setText("");
        vTipo.setText("");
        vCliente.setText("");
        vClienteNombre.setText("");
        vVendedor.setText("");
        vImporte.setText("");
        
        eMensaje.setText("");
        eMensaje.setForeground(Color.BLACK);
    }
    
    public boolean conectar (){
        try{
            Consola.out(Color.blue, "Conectando...");
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            conexion = DriverManager.getConnection("jdbc:odbc:Discovery", "", "");
            Consola.out(Color.green, "Conectado!");
            return true;
        }catch (Exception e){
            Consola.out(Color.red, "Error al conectar.");
            System.err.println(e.getMessage());
            eMensaje.setText("Error al conectar.");
            eMensaje.setForeground(Color.red);
            eMensaje.setToolTipText(e.toString());
            return false;
        }
    }
    
    public void buscar(){
        limpiar(false);
        if(!iTalonario.getText().isEmpty()){
            if(!iNumero.getText().isEmpty()){
                if(conectar()){
                    try {
                        contador = 0;
                        
                        Consola.out(Color.magenta, "Consultando...");
                        consulta = "SELECT  mov_fec as Fecha, mov_tip as Tipo, mov_tal as Talonario, mov_suc as Prefijo, mov_nro as Numero, mov_cli as Cliente, mov_ven as Vendedor, mov_caj as Caja, mov_tot as Total, mov_fpa as Condicion, mov_anu as Anulado "+
                                "FROM COMPROBANTES_VENTAS_ENCABEZADOS "+
                                "WHERE mov_tal = "+iTalonario.getText()+" AND mov_nro = "+iNumero.getText()+"";
                        Consola.out(consulta);

                        sentencia = conexion.createStatement();
                        resultado= sentencia.executeQuery(consulta);
                        
                        while (resultado.next()) {
                            factura = new FacturaX(resultado.getString("Fecha"), resultado.getString("Anulado"), resultado.getString("Caja"), resultado.getString("Tipo"), resultado.getString("Cliente"), resultado.getString("Vendedor"), resultado.getString("Total"));
                            
                            vFecha.setText(factura.getFechaString());
                            vAnulado.setText(factura.getAnulado()?"Si":"No");
                            vCaja.setText(factura.getCaja().toString());
                            vTipo.setText(factura.getTipo());
                            vCliente.setText(factura.getClienteRUC());
                            vVendedor.setText(factura.getVendedor());
                            vImporte.setText(factura.getTotalString());
                            contador++;
                        }
                        
                        if(contador == 1){
                            Consola.out(Color.magenta, "Consultando datos cliente");
                            consulta = "SELECT cli_nom as Nombre FROM CLIENTES WHERE cli_cod = '"+factura.getClienteRUC()+"'";
                            Consola.out(consulta);

                            sentencia = conexion.createStatement();
                            resultado= sentencia.executeQuery(consulta);

                            while (resultado.next()) {
                                vClienteNombre.setText(resultado.getString("Nombre"));
                            }
                        }
                        
                        if(contador>1){
                            JOptionPane.showMessageDialog(null, "Se encontraron "+contador+" resultados, comunique al administrador de sistema.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                        }
                        
                        resultado.close();
                        sentencia.close();
                        conexion.close();
                        Consola.out(Color.green, "Encontrado "+contador+" registro(s).");
                        eMensaje.setText("Encontrado "+contador+" registro(s).");
                        eMensaje.setForeground(Color.green);
                    } catch (SQLException ex) {
                        Logger.getLogger(CFVerificador.class.getName()).log(Level.SEVERE, null, ex);
                        Consola.out(Color.red, "Error al consultar.");
                        eMensaje.setText("Error al consultar.");
                        eMensaje.setForeground(Color.red);
                    }
                }
            }else{
                JOptionPane.showMessageDialog(null, "Ingrese un numero de comprobante.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null, "Ingrese un numero de talonario.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pBusqueda = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        iTalonario = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        iNumero = new javax.swing.JTextField();
        pResultado = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        vFecha = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        vCaja = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        vAnulado = new javax.swing.JTextField();
        vTipo = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        vCliente = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        vImporte = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        vVendedor = new javax.swing.JTextField();
        vClienteNombre = new javax.swing.JTextField();
        pMenu = new javax.swing.JPanel();
        mBuscar = new javax.swing.JButton();
        eMensaje = new javax.swing.JLabel();

        setClosable(true);
        setTitle("Verificador");

        pBusqueda.setBackground(new java.awt.Color(218, 234, 242));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Buscar comprobante");

        jLabel1.setText("Talonario");
        jLabel1.setPreferredSize(new java.awt.Dimension(60, 14));

        jLabel3.setText("Numero");
        jLabel3.setPreferredSize(new java.awt.Dimension(60, 14));

        javax.swing.GroupLayout pBusquedaLayout = new javax.swing.GroupLayout(pBusqueda);
        pBusqueda.setLayout(pBusquedaLayout);
        pBusquedaLayout.setHorizontalGroup(
            pBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pBusquedaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pBusquedaLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pBusquedaLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iTalonario, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34))))
        );
        pBusquedaLayout.setVerticalGroup(
            pBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pBusquedaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(iTalonario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(iNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        pResultado.setBackground(new java.awt.Color(218, 242, 205));

        jLabel4.setText("Fecha");
        jLabel4.setPreferredSize(new java.awt.Dimension(60, 14));

        vFecha.setDisabledTextColor(new java.awt.Color(0, 102, 102));
        vFecha.setEnabled(false);

        jLabel5.setText("Caja");
        jLabel5.setPreferredSize(new java.awt.Dimension(60, 14));

        vCaja.setDisabledTextColor(new java.awt.Color(0, 102, 102));
        vCaja.setEnabled(false);

        jLabel6.setText("Anulado");
        jLabel6.setPreferredSize(new java.awt.Dimension(60, 14));

        vAnulado.setDisabledTextColor(new java.awt.Color(0, 102, 102));
        vAnulado.setEnabled(false);

        vTipo.setDisabledTextColor(new java.awt.Color(0, 102, 102));
        vTipo.setEnabled(false);

        jLabel7.setText("Tipo");
        jLabel7.setPreferredSize(new java.awt.Dimension(60, 14));

        jLabel8.setText("Cliente");
        jLabel8.setPreferredSize(new java.awt.Dimension(60, 14));

        vCliente.setDisabledTextColor(new java.awt.Color(0, 102, 102));
        vCliente.setEnabled(false);

        jLabel9.setText("Importe");
        jLabel9.setPreferredSize(new java.awt.Dimension(60, 14));

        vImporte.setDisabledTextColor(new java.awt.Color(0, 102, 102));
        vImporte.setEnabled(false);

        jLabel10.setText("Vendedor");
        jLabel10.setPreferredSize(new java.awt.Dimension(60, 14));

        vVendedor.setDisabledTextColor(new java.awt.Color(0, 102, 102));
        vVendedor.setEnabled(false);

        vClienteNombre.setDisabledTextColor(new java.awt.Color(0, 102, 102));
        vClienteNombre.setEnabled(false);

        javax.swing.GroupLayout pResultadoLayout = new javax.swing.GroupLayout(pResultado);
        pResultado.setLayout(pResultadoLayout);
        pResultadoLayout.setHorizontalGroup(
            pResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pResultadoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pResultadoLayout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vImporte, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pResultadoLayout.createSequentialGroup()
                        .addGroup(pResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pResultadoLayout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(vCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(pResultadoLayout.createSequentialGroup()
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(vCliente))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pResultadoLayout.createSequentialGroup()
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(vFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(pResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pResultadoLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                                .addGroup(pResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pResultadoLayout.createSequentialGroup()
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(vAnulado, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(pResultadoLayout.createSequentialGroup()
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(vTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(24, 24, 24))
                            .addGroup(pResultadoLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(vClienteNombre)
                                .addContainerGap())))))
        );
        pResultadoLayout.setVerticalGroup(
            pResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pResultadoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vAnulado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vCaja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vClienteNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(vVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(vImporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(44, Short.MAX_VALUE))
        );

        pMenu.setPreferredSize(new java.awt.Dimension(0, 20));

        mBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/media/ico/Search.png"))); // NOI18N
        mBuscar.setOpaque(false);
        mBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mBuscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pMenuLayout = new javax.swing.GroupLayout(pMenu);
        pMenu.setLayout(pMenuLayout);
        pMenuLayout.setHorizontalGroup(
            pMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pMenuLayout.createSequentialGroup()
                .addComponent(mBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pMenuLayout.setVerticalGroup(
            pMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pMenuLayout.createSequentialGroup()
                .addComponent(mBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        eMensaje.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pResultado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pBusqueda, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pMenu, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(eMensaje, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(pMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pResultado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(eMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mBuscarActionPerformed
        buscar();
    }//GEN-LAST:event_mBuscarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel eMensaje;
    private javax.swing.JTextField iNumero;
    private javax.swing.JTextField iTalonario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JButton mBuscar;
    private javax.swing.JPanel pBusqueda;
    private javax.swing.JPanel pMenu;
    private javax.swing.JPanel pResultado;
    private javax.swing.JTextField vAnulado;
    private javax.swing.JTextField vCaja;
    private javax.swing.JTextField vCliente;
    private javax.swing.JTextField vClienteNombre;
    private javax.swing.JTextField vFecha;
    private javax.swing.JTextField vImporte;
    private javax.swing.JTextField vTipo;
    private javax.swing.JTextField vVendedor;
    // End of variables declaration//GEN-END:variables
}