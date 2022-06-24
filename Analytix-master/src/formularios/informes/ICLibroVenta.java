/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formularios.informes;

import analytix.Configuracion;
import informes.contables.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.threeten.bp.LocalDate;
import swing.*;
import system.Consola;
import worker.SWDiscovery;

/**
 *
 * @author juan.bogado
 */
public class ICLibroVenta extends javax.swing.JInternalFrame implements java.beans.PropertyChangeListener {
    public Properties configuracion;
    String talonariosfactura;
    String talonariosncr;
    String talonariosrecibo;
    String cuentaventas10;
    String cuentaventas5;
    String cuentaventasexe;
    Boolean esRestaurante;
    
    
    SWDiscovery SWDVY;
    XLibroVenta XLV;
    String query;
    
    KeyListener ikl, bkl;

    public ICLibroVenta() {
        if(loadConfig()){
            initComponents();
            initDatePickers();
            initListeners();

            SWDVY = new SWDiscovery(eMensaje);
            XLV = new XLibroVenta(eMensaje, configuracion);
        }
        
    }
    
    private boolean loadConfig(){
        configuracion = new Properties();
        try{
            String error = "";
            Configuracion.loadProperties(configuracion, "hechauka");
            talonariosfactura = configuracion.getProperty("talonariosfactura");
            talonariosncr = configuracion.getProperty("talonariosncr");
            talonariosrecibo = configuracion.getProperty("talonariosrecibo");
            cuentaventas10 = configuracion.getProperty("cuentaventas10");
            cuentaventas5 = configuracion.getProperty("cuentaventas5");
            cuentaventasexe = configuracion.getProperty("cuentaventasexe");
            esRestaurante = (configuracion.getProperty("esrestaurante") == "si");
            
            error += talonariosfactura == null ? "talonariosfactura": "";
            error += talonariosncr == null ? "talonariosncr": "";
            error += talonariosrecibo == null ? "talonariosrecibo": "";
            error += cuentaventas10 == null ? "cuentaventas10": "";
            error += cuentaventas5 == null ? "cuentaventas5": "";
            error += cuentaventasexe == null ? "cuentaventasexe": "";
            
            if(error.isEmpty()){
                return true;
            }else{
                JOptionPane.showMessageDialog(this, "Error al cargar archivo de configuracion de Hechauka, verifique la variable "+error+".");
                return false;
            }
            
            
        }catch (Exception ex){
            System.out.println("Error al cargar configuracion.");
            Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public void limpiar(Boolean full){
        if(full){
            fechaDesde.setText("");
            fechaHasta.setText("");
            //fechaSelector.setSelectedIndex(0);
        }
        
        eMensaje.setText("");
        eMensaje.setForeground(Color.BLACK);
        
        fechaDesde.requestFocus();
    }
    
    
    public void buscar(){
        limpiar(false);
        
        //SPLIT DE TALONARIOS 
        if(tipoReporte.getSelectedIndex() == 0){
            
            query = esRestaurante ?  
                    "SELECT  "
                + "'I' as ven_tipimp, 0 as ven_gra05,0 as ven_iva05,'' as ven_disg05, '' as cta_iva05, '' as ven_rubgra, '' as ven_rubg05, '' as ven_disexe, "
                + "CONCAT(CONVERT(nro_suc,SQL_VARCHAR),CONCAT('-',CONVERT(mov_nro,SQL_VARCHAR))) AS ven_numero, "
                + "0 as ven_imputa, LEFT(CONVERT(nro_suc,SQL_VARCHAR),1) as ven_sucurs, 0 as generar, mov_tip AS form_pag, '' as ven_centro, "
                + "cli_cui as ven_provee, '' as ven_cuenta, cli_nom as ven_prvnom, mov_tip AS ven_tipofa, mov_fec as ven_fecha, "
                + "mov_tot as ven_totfac, ROUND(((mov_tot-mov_iva)/2),0) as ven_exenta, ROUND((mov_tot-mov_iva),0)-ROUND(((mov_tot-mov_iva)/2),0) as ven_gravad, ROUND(mov_iva,0) as ven_iva, 0 as ven_retenc, '' as ven_aux, '' as ven_ctrl, '' as ven_con, 0 as ven_cuota, "
                + "mov_vto as ven_fecven, 0 as cant_dias, "
                + "'LI' as origen, 0 as cambio, mov_can as valor, '' as moneda, 0 as exen_dolar, '' as concepto, '' as cta_iva, '' as cta_caja, "
                + "0 as tkdesde, 0 as tkhasta, mov_caj as caja, '' as ven_disgra, 1 as forma_devo, "
                + "'' as ven_cuense, mov_anu as anular, '' as reproceso, '' as cuenta_exe, '' as usu_ide ,tmp_cui, tmp_nom, 'last_col' as last_col "
                + " "
                + "FROM COMPROBANTES_VENTAS_ENCABEZADOS INNER JOIN TALONARIOS ON mov_tal = nro_nro LEFT OUTER JOIN CLIENTES ON mov_cli = cli_cod LEFT OUTER JOIN CLIENTES_OCASIONALES ON mov_tmp = tmp_cod "
                + "WHERE mov_tip <> 80 AND mov_tip <> 88 AND nro_nro IN ("+talonariosfactura+","+talonariosncr+","+talonariosrecibo+") " :
                    "SELECT  "
                + "'I' as ven_tipimp, 0 as ven_gra05,0 as ven_iva05,'' as ven_disg05, '' as cta_iva05, '' as ven_rubgra, '' as ven_rubg05, '' as ven_disexe, "
                + "CONCAT(CONVERT(nro_suc,SQL_VARCHAR),CONCAT('-',CONVERT(mov_nro,SQL_VARCHAR))) AS ven_numero, "
                + "0 as ven_imputa, LEFT(CONVERT(nro_suc,SQL_VARCHAR),1) as ven_sucurs, 0 as generar, mov_tip AS form_pag, '' as ven_centro, "
                + "cli_cui as ven_provee, '' as ven_cuenta, cli_nom as ven_prvnom, mov_tip AS ven_tipofa, mov_fec as ven_fecha, "
                + "mov_tot as ven_totfac, 0 as ven_exenta, ROUND((mov_tot-ROUND(mov_iva,0)),0) as ven_gravad, ROUND(mov_iva,0) as ven_iva, 0 as ven_retenc, '' as ven_aux, '' as ven_ctrl, '' as ven_con, 0 as ven_cuota, "
                + "mov_vto as ven_fecven, 0 as cant_dias, "
                + "'LI' as origen, 0 as cambio, mov_can as valor, '' as moneda, 0 as exen_dolar, '' as concepto, '' as cta_iva, '' as cta_caja, "
                + "0 as tkdesde, 0 as tkhasta, mov_caj as caja, '' as ven_disgra, 1 as forma_devo, "
                + "'' as ven_cuense, mov_anu as anular, '' as reproceso, '' as cuenta_exe, '' as usu_ide ,tmp_cui, tmp_nom, 'last_col' as last_col "
                + " "
                + "FROM COMPROBANTES_VENTAS_ENCABEZADOS INNER JOIN TALONARIOS ON mov_tal = nro_nro LEFT OUTER JOIN CLIENTES ON mov_cli = cli_cod LEFT OUTER JOIN CLIENTES_OCASIONALES ON mov_tmp = tmp_cod "
                + "WHERE mov_tip <> 80 AND mov_tip <> 88 AND nro_nro IN ("+talonariosfactura+","+talonariosncr+","+talonariosrecibo+") " ;
        }else{
            // hacer codigo para cuando es o no es restaurante
            query = "SELECT  " +
                    "'I' as ven_tipimp, 0 as ven_gra05, 0 as ven_iva05, '' as ven_disg05, '' as cta_iva05, '' as ven_rubgra, '' as ven_rubg05, '' as ven_disexe, '' as ven_numero, 0 as ven_imputa, " +
                    "1 as ven_sucurs, 0 as generar, 'Contado' AS form_pag, '' as ven_centro, '44444401-7' as ven_provee, '' as ven_cuenta, 'IMPORTES CONSOLIDADOS' as ven_prvnom, mov_tal AS ven_tipofa, " +
                    "mov_fec as ven_fecha, SUM(mov_tot) as ven_totfac, 0 as ven_exenta, SUM(mov_tot-mov_iva) as ven_gravad, SUM(mov_iva) as ven_iva, 0 as ven_retenc, '' as ven_aux, '' as ven_ctrl, " +
                    "'' as ven_con, 0 as ven_cuota, '' as ven_fecven, 0 as cant_dias, 'LI' as origen, 0 as cambio, 0 as valor, '' as moneda, 0 as exen_dolar, '' as concepto, '' as cta_iva, '' as cta_caja, " +
                    "0 as tkdesde, 0 as tkhasta, 1 as caja, '' as ven_disgra, 1 as forma_devo, '' as ven_cuense, 0 as anular, '' as reproceso, '' as cuenta_exe, '' as usu_ide, " +
                    "nro_suc as Prefijo, MIN(mov_nro) as MinNumero, MAX(mov_nro) as MaxNumero " +
                    "FROM COMPROBANTES_VENTAS_ENCABEZADOS INNER JOIN TALONARIOS ON mov_tal = nro_nro "
                    + "WHERE mov_tip <> 80 AND mov_tip <> 88 AND mov_anu = 0 AND nro_nro IN ("+talonariosfactura+","+talonariosncr+", "+talonariosrecibo+") ";
                
        }
        
        // DOCUMENTACION: https://docs.faircom.com/doc/sqlref/
        
        
        if(!fechaDesde.getText().isEmpty()){
            query += "AND mov_fec >= {d'"+fechaDesde.getText()+"'} ";
        }
        if(!fechaHasta.getText().isEmpty()){
            query += "AND mov_fec <= {d'"+fechaHasta.getText()+"'}";
        }
        
        if(tipoReporte.getSelectedIndex() == 0){
            query = query + " ORDER BY mov_fec, mov_tal, mov_nro";
        }else{
            query = query + " GROUP BY mov_fec, mov_tal, mov_civ, nro_suc ORDER BY mov_fec, mov_tal, mov_civ";
        }
        
        
        SWDVY.consultar(query);
        SWDVY.consultar.addPropertyChangeListener(this);
        SWDVY.consultar.execute();
        
        System.out.println("");
        System.out.println("QUERY: "+query);
        System.out.println("");
    
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pBusqueda = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        fechaSelector = new JDateSetter(fechaDesde, fechaHasta);
        fechaDesde = new com.github.lgooddatepicker.components.DatePicker();
        fechaHasta = new com.github.lgooddatepicker.components.DatePicker();
        bBuscar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tipoReporte = new javax.swing.JComboBox<>();
        pMenu = new javax.swing.JPanel();
        mBuscar = new javax.swing.JButton();
        mLimpiar = new javax.swing.JButton();
        mAyuda = new javax.swing.JButton();
        eMensaje = new javax.swing.JLabel();

        setClosable(true);
        setTitle("Libro de ventas Starsoft");

        pBusqueda.setBackground(new java.awt.Color(218, 234, 242));

        jLabel2.setText("Periodo");
        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jLabel1.setText("Desde");
        jLabel1.setPreferredSize(new java.awt.Dimension(60, 14));

        fechaSelector.setEnabled(false);

        fechaDesde.setPreferredSize(new java.awt.Dimension(110, 25));

        fechaHasta.setPreferredSize(new java.awt.Dimension(110, 25));

        bBuscar.setText("Buscar");
        bBuscar.setPreferredSize(new java.awt.Dimension(65, 25));
        bBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBuscarActionPerformed(evt);
            }
        });

        jLabel3.setText("Hasta");
        jLabel3.setPreferredSize(new java.awt.Dimension(60, 14));

        jLabel4.setText("Periodo");
        jLabel4.setPreferredSize(new java.awt.Dimension(60, 14));

        jLabel5.setText("Tipo");
        jLabel5.setPreferredSize(new java.awt.Dimension(60, 14));

        tipoReporte.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Detallado", "Consolidado" }));
        tipoReporte.setPreferredSize(new java.awt.Dimension(140, 25));

        javax.swing.GroupLayout pBusquedaLayout = new javax.swing.GroupLayout(pBusqueda);
        pBusqueda.setLayout(pBusquedaLayout);
        pBusquedaLayout.setHorizontalGroup(
            pBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pBusquedaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2)
                    .addGroup(pBusquedaLayout.createSequentialGroup()
                        .addGroup(pBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fechaSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pBusquedaLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tipoReporte, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(160, Short.MAX_VALUE))
        );
        pBusquedaLayout.setVerticalGroup(
            pBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pBusquedaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(pBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fechaSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tipoReporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(bBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pMenu.setPreferredSize(new java.awt.Dimension(0, 20));

        mBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/media/ico/Search.png"))); // NOI18N
        mBuscar.setOpaque(false);
        mBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mBuscarActionPerformed(evt);
            }
        });

        mLimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/media/ico/clear.png"))); // NOI18N
        mLimpiar.setOpaque(false);
        mLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mLimpiarActionPerformed(evt);
            }
        });

        mAyuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/media/ico/help.png"))); // NOI18N
        mAyuda.setOpaque(false);
        mAyuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mAyudaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pMenuLayout = new javax.swing.GroupLayout(pMenu);
        pMenu.setLayout(pMenuLayout);
        pMenuLayout.setHorizontalGroup(
            pMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pMenuLayout.createSequentialGroup()
                .addComponent(mBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(mAyuda, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pMenuLayout.setVerticalGroup(
            pMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pMenuLayout.createSequentialGroup()
                .addGroup(pMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mAyuda, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addComponent(eMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mBuscarActionPerformed
        buscar();
    }//GEN-LAST:event_mBuscarActionPerformed

    private void bBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBuscarActionPerformed
        buscar();
    }//GEN-LAST:event_bBuscarActionPerformed

    private void mLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mLimpiarActionPerformed
        limpiar(true);
    }//GEN-LAST:event_mLimpiarActionPerformed

    private void mAyudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mAyudaActionPerformed
        String htmlOpen = "<html><body style='width: 300px'>";
        String htmlClose = "</body></html>";
        
        
        URL u;
        u = getClass().getResource("/imagenes/informes/CLibroVenta_01.png" ); 
        
        String htmlBody = "<h1>Ayuda</h1><br /><br /><h2>Cambiar formato de Excel</h2> <p>El actual sistema, genera un archivo en Excel, con las especificaciones requeridas para el sistema de migracion de Starsoft, salvo la version del formato del mismo, Starsoft requiere que los archivos se encuentren con la extension XLS de la version 5.0, es decir, debe de guardar el archivo en modo compatibilidad 1995.</p><p><img src='" + u + "'>";
        JOptionPane.showMessageDialog(this, htmlOpen + htmlBody + htmlClose);
    }//GEN-LAST:event_mAyudaActionPerformed

    private void initDatePickers(){
        // Limites de fechas
        LocalDate max = LocalDate.now();
        LocalDate min = LocalDate.of(2010, 05, 17);

        // Fecha desde
        fechaDesde.getComponentDateTextField().setPreferredSize(new Dimension(80, 25));
        fechaDesde.getComponentToggleCalendarButton().setText("");
        fechaDesde.getComponentToggleCalendarButton().setIcon(new javax.swing.ImageIcon(getClass().getResource("/media/ico/calendar.png"))); // NOI18N
        fechaDesde.getComponentToggleCalendarButton().setPreferredSize(new Dimension(25, 25));
        fechaDesde.getComponentDateTextField().setMargin(new Insets(0, 0, 0, 0));
        fechaDesde.getSettings().setFormatForDatesCommonEra("yyyy-MM-dd");
        fechaDesde.getSettings().setDateRangeLimits(min, max);
        
        // Fecha hasta
        fechaHasta.getComponentDateTextField().setPreferredSize(new Dimension(80, 25));
        fechaHasta.getComponentToggleCalendarButton().setText("");
        fechaHasta.getComponentToggleCalendarButton().setIcon(new javax.swing.ImageIcon(getClass().getResource("/media/ico/calendar.png"))); // NOI18N
        fechaHasta.getComponentToggleCalendarButton().setPreferredSize(new Dimension(25, 25));
        fechaHasta.getComponentDateTextField().setMargin(new Insets(0, 0, 0, 0));
        fechaHasta.getSettings().setFormatForDatesCommonEra("yyyy-MM-dd");
        fechaHasta.getSettings().setDateRangeLimits(min, max);
    }
    
    private void initListeners(){
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
        
        fechaDesde.addKeyListener(ikl);
        fechaHasta.addKeyListener(ikl);
        bBuscar.addKeyListener(bkl);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bBuscar;
    private javax.swing.JLabel eMensaje;
    private com.github.lgooddatepicker.components.DatePicker fechaDesde;
    private com.github.lgooddatepicker.components.DatePicker fechaHasta;
    private swing.JDateSetter fechaSelector;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JButton mAyuda;
    private javax.swing.JButton mBuscar;
    private javax.swing.JButton mLimpiar;
    private javax.swing.JPanel pBusqueda;
    private javax.swing.JPanel pMenu;
    private javax.swing.JComboBox<String> tipoReporte;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String source = evt.getSource().toString().substring(evt.getSource().toString().lastIndexOf("$")+1, evt.getSource().toString().indexOf("@"));
        String value = evt.getNewValue().toString();
        String id = (String) evt.getPropagationId();
        
        Consola.out(JColor.MAGENTA,"Evento: "+source+": ["+value+"]");
        //System.out.println("Valor: "+value);
        //System.out.println("ID: "+id);
        
        switch(source){
            case "Consultar":
                if(value.equals("STARTED")){
                    bBuscar.setEnabled(false);
                }else{
                    bBuscar.setEnabled(true);
                    try {
                        Object[][] res;
                        res = SWDVY.consultar.get();
                        
                        if(tipoReporte.getSelectedIndex() == 0){
                            XLV.detallado(res);
                            XLV.detallado.addPropertyChangeListener(this);
                            XLV.detallado.execute();
                            
                            //XLibroVentaDetallado xlv = new XLibroVentaDetallado(res);
                            //xlv.addPropertyChangeListener(this);
                        }else{
                            XLV.consolidado(res);
                            XLV.consolidado.addPropertyChangeListener(this);
                            XLV.consolidado.execute();
                        }
                        
                    } catch (InterruptedException | ExecutionException ex) {
                        System.out.println("No se pudo listar.");
                    }
                }
                break;
            case "Detallado":
                if(value.equals("STARTED")){
                    bBuscar.setEnabled(false);
                }else{
                    bBuscar.setEnabled(true);
                }
                break;
            case "Consolidado":
                if(value.equals("STARTED")){
                    bBuscar.setEnabled(false);
                }else{
                    bBuscar.setEnabled(true);
                }
                break;
        }
    }
}