/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formularios.comprobantes;

import analytix.Configuracion;
import clases.Cliente;
import clases.Factura;
import clases.FacturaDetalle;
import clases.Mozo;
import imprimibles.printFactura;
import clases.Producto;
import clases.ReferenciaContable;
import clases.Talonario;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.swing.JOptionPane;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import swing.JColor;
import system.Consola;
import tools.Validador;
import worker.SWDiscovery;

/**
 *
 * @author Juan Bogado
 */
public class CFReimpresion extends javax.swing.JInternalFrame implements java.beans.PropertyChangeListener {

    private PrintService[] serviciosImpresion;
    private PrintService impresora;
    private KeyListener klNumero;

    public Properties configuracion;

    private SWDiscovery SWDVY;
    private SWDiscovery swTalonarios;
    private SWDiscovery swProductos;
    private SWDiscovery swClientes;
    private SWDiscovery swFacturas;
    private SWDiscovery swFacturaDetalles;
    private SWDiscovery swReferencias;
    
    private List<Talonario> lsTalonarios;
    private List<ReferenciaContable> lsReferencias;
    
    private Talonario[] arTalonarios;
    private ReferenciaContable[] arReferencias;
    private Producto[] arProductos;
    private Factura factura;

    public CFReimpresion() {
        initComponents();

        SWDVY = new SWDiscovery(eMensaje);
        swTalonarios = new SWDiscovery(eMensaje);
        swFacturas = new SWDiscovery(eMensaje);
        swFacturaDetalles = new SWDiscovery(eMensaje);
        swProductos = new SWDiscovery(eMensaje);
        swClientes = new SWDiscovery(eMensaje);
        swReferencias = new SWDiscovery(eMensaje);

        iniciarListeners();
        cargarConfiguracion();

        buscarImpresoras();
        buscarTalonarios();
        buscarReferencias();
        buscarClientes();

        /*
        Praa hacer mas ligero o simplemente que funcione, hay que precargar un listado de productos, porque no se puede hacer el query-
         */
    }

    /* INICIADORES, CONFIGURACIONES Y LISTENERS */
    public void limpiarFormulario(Boolean full) {
        eMensaje.setText("");
        eMensaje.setForeground(Color.BLACK);

        txNumero.requestFocus();
    }
    
    private void iniciarListeners() {
        klNumero = new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                switch (evt.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                        limpiarFormulario(true);
                        break;
                    case KeyEvent.VK_ENTER:
                        buscarUltimaFactura();
                        break;
                    case KeyEvent.VK_F10:
                        buscarDetallesFactura();
                        break;
                }
            }
        };

        txNumero.addKeyListener(klNumero);
    }

    private void cargarConfiguracion() {
        configuracion = new Properties();
        Configuracion.loadProperties(configuracion, "reimpresion");
        cbAutoImpresion.setSelected(configuracion.getProperty("autoimpresion").equals("SI"));
        txUltimoImpreso.setText(configuracion.getProperty("ultimoimpreso"));
    }

    private void guardarConfiguracion(Boolean pregunta) {
        Integer op;
        if(pregunta){
            op = JOptionPane.showConfirmDialog(this, "Se cambiará los valores por defecto. \nDesea continuar?", "Reimpresion de Facturas", 2);
        }else{
            op = JOptionPane.OK_OPTION;
        }
        
        if (op == JOptionPane.OK_OPTION) {
            configuracion = new Properties();
            configuracion.setProperty("impresora", cbImpresoras.getSelectedItem().toString());
            configuracion.setProperty("talonario", cbTalonarios.getSelectedItem().toString());
            configuracion.setProperty("autoimpresion", (cbAutoImpresion.isSelected() ? "SI" : "NO"));
            configuracion.setProperty("ultimoimpreso", txUltimoImpreso.getText());
            Configuracion.saveProperties(configuracion, "reimpresion");
        }

    }

    /* IMPRESORAS */
    private void buscarImpresoras() {
        serviciosImpresion = PrintServiceLookup.lookupPrintServices(null, null);
        cargarImpresoras();
    }

    private void cargarImpresoras() {
        if (serviciosImpresion.length > 0) {
            String[] items = new String[serviciosImpresion.length];
            Integer i = 0;
            Integer defaultIndex = -1;

            for (PrintService printService : serviciosImpresion) {
                items[i] = printService.getName();
                if (configuracion.getProperty("impresora").equals(items[i])) {
                    impresora = printService;
                    
                    System.out.println("A"+impresora.getAttributes());
                    
                    for (Attribute attribute : impresora.getAttributes().toArray()) {
                        System.out.println("Atributo: "+attribute.getName()+" "+attribute.toString());
                    }
                    
                    
                    defaultIndex = i;
                }
                i++;
            }

            if (defaultIndex < 0) {
                eMensaje.setText("No se pudo seleccionar la impresora por defecto.");
                defaultIndex = 0;
            }

            cbImpresoras.setModel(new javax.swing.DefaultComboBoxModel<>(items));
            cbImpresoras.setSelectedIndex(defaultIndex);
        }
    }
    
    private void setImpresora(String impre) {
        if (serviciosImpresion.length > 0) {
            Integer i = 0;

            for (PrintService printService : serviciosImpresion) {
                if (printService.getName().equals(impre)) {
                    impresora = printService;
                }
                i++;
            }
        }
    }

    /* TALONARIOS */
    private void buscarTalonarios() {
        swTalonarios.consultar("SELECT * FROM TALONARIOS WHERE nro_suc <> 0");
        swTalonarios.consultar.proceso = "TALONARIOS";
        swTalonarios.consultar.addPropertyChangeListener(this);
        swTalonarios.consultar.execute();

    }

    private void cargarTalonarios(Object[][] resultado) {
        Integer cantidadTalonarios = resultado.length-1;
        String[] dbTalonariosItems = new String[cantidadTalonarios];
        
        Integer i = -1;
        Integer defaultIndex = -1;

        if (cantidadTalonarios > 0) {
            arTalonarios = new Talonario[cantidadTalonarios];
            
            for (Object[] item : resultado) {
                if (i >= 0) {
                    arTalonarios[i] = new Talonario((int) item[0],(int) item[1]);
                    dbTalonariosItems[i] = String.valueOf(item[0]);
                    if (configuracion.getProperty("talonario").equals(dbTalonariosItems[i])) {
                        defaultIndex = i;
                    }
                }
                i++;
            }

            if (defaultIndex < 0) {
                eMensaje.setText("No se pudo seleccionar el talonario por defecto.");
                defaultIndex = 0;
            }
            cbTalonarios.setModel(new javax.swing.DefaultComboBoxModel<>(dbTalonariosItems));
            cbTalonarios.setSelectedIndex(defaultIndex);
        }else{
            eMensaje.setText("No se encontraron talonarios.");
        }
    }
    
    private Talonario getTalonario(Integer codigo){
        for (Talonario talonario : arTalonarios) {
            if(talonario.getCodigo().equals(codigo)){
                return talonario;
            }
        }
        return new Talonario(codigo, 0);
    }
    
    /* REFERENCIAS CONTABLES */
    private void buscarReferencias() {
        swReferencias.consultar("SELECT * FROM REFERENCIAS_CONTABLES");
        swReferencias.consultar.proceso = "REFERENCIAS_CONTABLES";
        swReferencias.consultar.addPropertyChangeListener(this);
        swReferencias.consultar.execute();

    }

    private void cargarReferencias(Object[][] resultado) {
        Integer i = -1;
        Double dIva = 0.0;
        Integer iIva = 0;

        if (resultado.length > 1) {
            arReferencias = new ReferenciaContable[resultado.length-1];
            
            for (Object[] item : resultado) {
                if (i >= 0) {
                    dIva = (Double) item[2];
                    iIva = (int)Math.round(iIva);
                    arReferencias[i] = new ReferenciaContable((String) item[0],(String) item[1], iIva);
                }
                i++;
            }

        }else{
            eMensaje.setText("No se encontraron talonarios.");
        }
    }
    
    private ReferenciaContable getReferencia(String codigo){
        for (ReferenciaContable referencia : arReferencias) {
            if(referencia.getCodigo().equals(codigo)){
                return referencia;
            }
        }
        return new ReferenciaContable(codigo, "", 0);
    }

    /* ULTIMA FACTURA EMITIDA */
    private void buscarUltimaFactura() {
        String talonario = cbTalonarios.getSelectedItem().toString();

        LocalDate ldHoy;
        //ldHoy = LocalDate.now(); //volver a activar en produccion, pero algo orientado a los ultimos 30 dias
        ldHoy = LocalDate.of(2021, 03, 01);
        String sHoy = ldHoy.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        //System.out.println("sHoy: "+sHoy);
        //String query = "SELECT * FROM COMPROBANTES_VENTAS_ENCABEZADOS WHERE mov_tip <> 80 AND mov_tip <> 82 AND mov_tip <> 88 AND mov_anu = 0 AND mov_tal = 71";
        String query = "SELECT * FROM COMPROBANTES_VENTAS_ENCABEZADOS WHERE mov_tip <> 80 AND mov_tip <> 88 AND mov_anu = 0 AND mov_tal = " + talonario + " AND mov_fec >= {d'" + sHoy + "'} ";
        swFacturas.consultar(query);
        swFacturas.consultar.proceso = "ULTIMA_FACTURA";
        swFacturas.consultar.addPropertyChangeListener(this);
        swFacturas.consultar.execute();
    }

    private void cargarUltimaFactura(Object[][] resultado) {
        //System.out.println("RESULTADO: "+resultado.length);
        Integer[] items = new Integer[resultado.length - 1];
        Integer i = -1;
        Integer maxFactura = -1;

        if (resultado.length > 1) {
            for (Object[] item : resultado) {
                if (i >= 0) {
                    items[i] = Integer.valueOf(item[1].toString());
                    if (maxFactura < items[i]) {
                        maxFactura = items[i];
                    }
                }
                i++;
            }

            if (maxFactura < 0) {
                eMensaje.setText("No se pudo seleccionar la ultima factura.");
                maxFactura = 0;
            }

            txNumero.setText(maxFactura.toString());
        }
    }

    /* PRODUCTOS */
    private void buscarProductos() {
        swProductos.consultar("SELECT * FROM PRODUCTOS");
        swProductos.consultar.proceso = "PRODUCTOS";
        swProductos.consultar.addPropertyChangeListener(this);
        swProductos.consultar.execute();
    }

    private void cargarProductos(Object[][] resultado) {
        Integer i = -1;

        if (resultado.length > 1) {
            arProductos = new Producto[resultado.length-1];
            Producto producto;
            for (Object[] item : resultado) {
                if (i >= 0) {
                    producto = new Producto();
                    producto.setCodigo((String) item[0]);
                    producto.setDescripcion((String) item[1]);
                    producto.setReferenciaContable(getReferencia((String) item[7]));
                    producto.setCodigoSeleccion((item[5] != null ?(String)item[5]:""));
                    
                    arProductos[i] = producto;
                }
                i++;
            }

        }else{
            eMensaje.setText("No se encontraron talonarios.");
        }
    }
    
    private Producto getProducto(String codigo){
        if(codigo != null){
            if(codigo.equals("")){
                codigo = "/TXT";
            }else{
                for (Producto producto : arProductos) {
                    if(producto.getCodigo().trim().equals(codigo.trim())){
                        return producto;
                    }
                }
            }
            
        }else{
            codigo = "/TXT";
        }
        
        return new Producto(codigo, "CONSUMISION", getReferencia("MER"),"");
    }

    /* CLIENTES */
    private void buscarClientes(){
        swClientes.consultar("SELECT * FROM CLIENTES");
        swClientes.consultar.proceso = "CLIENTES";
        swClientes.consultar.addPropertyChangeListener(this);
        swClientes.consultar.execute();
    }

    private void cargarClientes() {

    }
    
    /* FACTURA */
    public void buscarFactura() {
        String talonario = cbTalonarios.getSelectedItem().toString();
        String numero = txNumero.getText();
        
        String query = "SELECT " +
                "mov_fec, mov_tal, mov_nro, mov_cli, cli_nom, cli_cui, mov_tmp, tmp_nom, tmp_cui, mov_tot, mov_iva, mov_mes, mov_hin, mov_hfi, mov_cub, mov_ven, ven_nom " +
                "FROM COMPROBANTES_VENTAS_ENCABEZADOS " +
                "LEFT OUTER JOIN TALONARIOS ON mov_tal = nro_nro " +
                "LEFT OUTER JOIN CLIENTES ON mov_cli = cli_cod " +
                "LEFT OUTER JOIN CLIENTES_OCASIONALES ON mov_tmp = tmp_cod " +
                "LEFT OUTER JOIN VENDEDORES ON mov_ven = ven_cod " +
                "WHERE mov_tip <> 80 AND mov_tip <> 88 AND mov_anu = 0  AND mov_tal = " + talonario + " AND mov_nro = " + numero  ;
                
        
        swFacturas.consultar(query);
        swFacturas.consultar.proceso = "FACTURA_ENCABEZADO";
        swFacturas.consultar.addPropertyChangeListener(this);
        swFacturas.consultar.execute();
    }
    
    private void cargarFactura(Object[][] resultado) {
        Integer l = 0;
        List<String> encabezado = new ArrayList<String>();
        Cliente cliente = new Cliente();
        Cliente clienteOcasional = new Cliente();
        
                
        if (resultado.length > 1) {
            factura = new Factura();
            
            for (Object[] linea : resultado) {
                if(l == 0){
                    for (Object columna : linea) {
                        encabezado.add(columna.toString());
                    }
                }else if(l == 1){
                    try {       
                        for (Talonario talonario : arTalonarios) {
                            
                            if(talonario.getCodigo() == (int) linea[encabezado.indexOf("mov_tal")]){
                                factura.setTalonario(talonario);
                                break;
                            }
                        }
                        
                        factura.setNumero((Integer) linea[encabezado.indexOf("mov_nro")]);
                        SimpleDateFormat sdf_discv = new SimpleDateFormat("yyyy-MM-dd");
                        Date convertedCurrentDate = sdf_discv.parse(linea[encabezado.indexOf("mov_fec")].toString());
                        factura.setFecha(convertedCurrentDate);
                        
                        if(linea[encabezado.indexOf("mov_cli")] == null){
                            clienteOcasional.setCodigo(linea[encabezado.indexOf("mov_tmp")].toString());
                            clienteOcasional.setNombre(linea[encabezado.indexOf("tmp_nom")].toString());
                            clienteOcasional.setRuc(linea[encabezado.indexOf("tmp_cui")].toString());
                            clienteOcasional.setDireccion("");
                            clienteOcasional.setTelefono("");
                            factura.setCliente(clienteOcasional);
                        }else{
                            cliente.setCodigo(linea[encabezado.indexOf("mov_cli")].toString());
                            cliente.setNombre(linea[encabezado.indexOf("cli_nom")].toString());
                            cliente.setRuc(linea[encabezado.indexOf("cli_cui")].toString());
                            cliente.setDireccion("");
                            cliente.setTelefono("");
                            factura.setCliente(cliente);
                        }
                            
                        factura.setTotal((Double) linea[encabezado.indexOf("mov_tot")]);
                        
                        factura.setMesa(Validador.validaString(linea[encabezado.indexOf("mov_mes")]));
                        factura.setHora(Validador.validaString(linea[encabezado.indexOf("mov_hfi")]));
                        factura.setCubiertos(Validador.validaString(linea[encabezado.indexOf("mov_cub")]));
                        factura.setMozo(new Mozo(
                                Validador.validaInteger(linea[encabezado.indexOf("mov_ven")]), 
                                Validador.validaString(linea[encabezado.indexOf("ven_nom")]))
                        );
                        
                    } catch (ParseException ex) {
                        System.out.println("Error al procesar los talonarios.");
                        //Logger.getLogger(CFReimpresion.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else{
                    System.out.println("Error, hay mas de una linea.");
                }
                l++;
            }
        }
    }
    
    /* DETALLES DE FACTURA */
    public void buscarDetallesFactura() {
        String talonario = cbTalonarios.getSelectedItem().toString();
        String numero = txNumero.getText();
        
        String query = 
                "SELECT mli_tal, mli_nro, mli_seq, mli_ca2, mli_itm, mli_uni, mli_dt1, mli_dt2, mli_po1, mli_po2, mli_dgi, mli_tot, txt_cod, txt_txt  " +
                "FROM COMPROBANTES_LINEAS_PRODUCTOS " +
                "LEFT OUTER JOIN TEXTOS_TEMPORARIOS ON mli_txt = txt_cod " +
                "WHERE mli_tal = " + talonario + " AND mli_nro = " + numero + " AND mli_seq < 2000 " +
                "ORDER BY mli_seq";
        
        swFacturas.consultar(query);
        swFacturas.consultar.proceso = "FACTURA_DETALLE";
        swFacturas.consultar.addPropertyChangeListener(this);
        swFacturas.consultar.execute();
    }
    
    private void cargarDetallesFactura(Object[][] resultado) {
        Integer cantidadLineas = resultado.length-1;
        Integer l = 0;
        
        List<String>encabezado = new ArrayList<String>();
        List<String>detalle = new ArrayList<String>();
        
        
        if (cantidadLineas > 1) {
            factura.setDetalle(new FacturaDetalle[cantidadLineas]);
            
            for (Object[] linea : resultado) {
                if(l == 0){
                    for (Object columna : linea) {
                        encabezado.add(columna.toString());
                    }
                }else if(l >= 1){
                    if(Integer.valueOf(linea[encabezado.indexOf("mli_seq")].toString()) < 2000){
                        Producto producto = new Producto();
                        Double cantidad = Validador.validaDouble(linea[encabezado.indexOf("mli_ca2")]);
                        Double precioUnitario = Validador.validaDouble(linea[encabezado.indexOf("mli_uni")]);
                        Integer secuencia = Validador.validaInteger(linea[encabezado.indexOf("mli_seq")]);
                        
                        if(Validador.validaProductoCodigo(linea[encabezado.indexOf("mli_itm")])){
                            producto = getProducto(linea[encabezado.indexOf("mli_itm")].toString());
                        }else{
                            producto = new Producto(
                                Validador.validaString(linea[encabezado.indexOf("mli_itm")]), 
                                Validador.validaString(linea[encabezado.indexOf("txt_txt")]), 
                                getReferencia(Validador.validaString(linea[encabezado.indexOf("mli_dgi")])), 
                                "");
                        }
                        
                        try {
                            //System.out.println("LINEA: "+l+" CODIGO: "+producto.getCodigo());
                            factura.getDetalle()[l-1] = new FacturaDetalle();
                            factura.getDetalle()[l-1].setCantidad(cantidad);
                            factura.getDetalle()[l-1].setPrecioUnitario(precioUnitario);
                            factura.getDetalle()[l-1].setSecuencia(secuencia);
                            factura.getDetalle()[l-1].setProducto(producto);

                        }catch (Exception ex) {
                            System.out.println("Error al procesar el detalle.");
                            Logger.getLogger(CFReimpresion.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                l++;
                
            }
            imprimirFactura();
        }else{
            eMensaje.setText("No se encuentra una factura con ese numero.");
        }
        
    }
    
    private void imprimirFactura(){
        try {
            //2021  factura 210*310
            Double fPpM = 0.3528;
            Double fPpP = 0.0138;
            Double ancho = 8.2677;//8.5;
            Double largo = 12.2835;//13.0;
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintService(impresora);
            
            Paper paper = new Paper();
            paper.setSize(ancho/fPpP,largo/fPpP);
            paper.setImageableArea(0, 0, ancho/fPpP, largo/fPpP);
            
            PageFormat pageFormat = new PageFormat();
            pageFormat.setOrientation(pageFormat.REVERSE_LANDSCAPE);
            pageFormat.setPaper(paper);
                      
            

            
            job.setPrintable(new printFactura(factura),pageFormat);
            //job.setPrintService(impresora);
            
            //job.setCopies(1);
            
            job.print();
            //printServices

            //HORIZONTAL
            //PageFormat.LANDSCAPE;


            /*
            job.setPrintable(new Impresion(factura));
            
            try {
                // Diálogo para elegir el formato de impresión
                PageFormat pageFormat = new PageFormat();
                pageFormat.setOrientation(pageFormat.LANDSCAPE);
                
                job.pageDialog(pageFormat);

                // Diálogo para confirmar impresion.
                // Devuelve true si el usuario decide imprimir.
                if (job.printDialog())
                    job.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
            */
            txUltimoImpreso.setText(factura.getNumero().toString());
            guardarConfiguracion(false);
            eMensaje.setText("Impresión finalizada");
            eMensaje.setForeground(JColor.GREEN);
        } catch (PrinterException ex) {
            eMensaje.setText("Error al imprimir");
            eMensaje.setForeground(JColor.RED);
            Logger.getLogger(CFReimpresion.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        btImprimir.setEnabled(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pMenu = new javax.swing.JPanel();
        mImprimir = new javax.swing.JButton();
        mLimpiar = new javax.swing.JButton();
        mAyuda = new javax.swing.JButton();
        mDefecto = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txNumero = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cbAutoImpresion = new javax.swing.JCheckBox();
        mImprimir1 = new javax.swing.JButton();
        btImprimir = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        eMensaje = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cbTalonarios = new javax.swing.JComboBox<>();
        txUltimoImpreso = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbImpresoras = new javax.swing.JComboBox<>();

        setClosable(true);
        setTitle("Reimpresion de Factura");
        setPreferredSize(new java.awt.Dimension(430, 380));

        pMenu.setPreferredSize(new java.awt.Dimension(0, 20));

        mImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/media/ico/printer.png"))); // NOI18N
        mImprimir.setOpaque(false);
        mImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mImprimirActionPerformed(evt);
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

        mDefecto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/media/ico/module.png"))); // NOI18N
        mDefecto.setOpaque(false);
        mDefecto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mDefectoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pMenuLayout = new javax.swing.GroupLayout(pMenu);
        pMenu.setLayout(pMenuLayout);
        pMenuLayout.setHorizontalGroup(
            pMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pMenuLayout.createSequentialGroup()
                .addComponent(mImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 293, Short.MAX_VALUE)
                .addComponent(mDefecto, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mAyuda, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pMenuLayout.setVerticalGroup(
            pMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pMenuLayout.createSequentialGroup()
                .addGroup(pMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mAyuda, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mDefecto, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jLabel4.setText("Numero");
        jLabel4.setPreferredSize(new java.awt.Dimension(100, 25));

        txNumero.setPreferredSize(new java.awt.Dimension(100, 25));

        jLabel5.setText("Auto Impresión");
        jLabel5.setPreferredSize(new java.awt.Dimension(100, 25));

        cbAutoImpresion.setText("Imprimir automáticamente");

        mImprimir1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/media/ico/search.png"))); // NOI18N
        mImprimir1.setOpaque(false);
        mImprimir1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mImprimir1ActionPerformed(evt);
            }
        });

        btImprimir.setText("Imprimir");
        btImprimir.setPreferredSize(new java.awt.Dimension(100, 25));
        btImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btImprimirActionPerformed(evt);
            }
        });

        jLabel6.setText("Ultimo impreso");
        jLabel6.setPreferredSize(new java.awt.Dimension(100, 25));

        eMensaje.setPreferredSize(new java.awt.Dimension(100, 25));

        jLabel3.setText("Talonario");
        jLabel3.setPreferredSize(new java.awt.Dimension(100, 25));

        cbTalonarios.setPreferredSize(new java.awt.Dimension(200, 25));

        txUltimoImpreso.setEditable(false);
        txUltimoImpreso.setEnabled(false);
        txUltimoImpreso.setPreferredSize(new java.awt.Dimension(100, 25));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(eMensaje, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                .addGap(388, 388, 388))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbTalonarios, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbAutoImpresion)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mImprimir1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txUltimoImpreso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbTalonarios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(mImprimir1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txUltimoImpreso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbAutoImpresion))
                .addGap(18, 18, 18)
                .addComponent(btImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65)
                .addComponent(eMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Reimpresión", jPanel1);

        jLabel1.setText("Impresora");
        jLabel1.setPreferredSize(new java.awt.Dimension(100, 25));

        cbImpresoras.setPreferredSize(new java.awt.Dimension(200, 25));
        cbImpresoras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbImpresorasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbImpresoras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(68, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbImpresoras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(237, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Impresora", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(11, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(pMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mImprimirActionPerformed
        buscarDetallesFactura();
    }//GEN-LAST:event_mImprimirActionPerformed

    private void mLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mLimpiarActionPerformed
        limpiarFormulario(true);
    }//GEN-LAST:event_mLimpiarActionPerformed

    private void mAyudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mAyudaActionPerformed
        String htmlOpen = "<html><body style='width: 300px'>";
        String htmlClose = "</body></html>";

        URL u;
        u = getClass().getResource("/imagenes/informes/CLibroVenta_01.png");

        String htmlBody = "<h1>Ayuda</h1><br /><br /><h2>Cambiar formato de Excel</h2> <p>El actual sistema, genera un archivo en Excel, con las especificaciones requeridas para el sistema de migracion de Starsoft, salvo la version del formato del mismo, Starsoft requiere que los archivos se encuentren con la extension XLS de la version 5.0, es decir, debe de guardar el archivo en modo compatibilidad 1995.</p><p><img src='" + u + "'>";
        JOptionPane.showMessageDialog(this, htmlOpen + htmlBody + htmlClose);
    }//GEN-LAST:event_mAyudaActionPerformed

    private void mDefectoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mDefectoActionPerformed
        guardarConfiguracion(true);
    }//GEN-LAST:event_mDefectoActionPerformed

    private void mImprimir1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mImprimir1ActionPerformed
        buscarUltimaFactura();
    }//GEN-LAST:event_mImprimir1ActionPerformed

    private void btImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btImprimirActionPerformed
        btImprimir.setEnabled(false);
        buscarFactura();
        /*
        try {
            Factura factura_testing = new Factura();
            factura_testing.setNumero(99999);
            SimpleDateFormat sdf_discv = new SimpleDateFormat("yyyy-MM-dd");
            Date convertedCurrentDate = sdf_discv.parse("2021-03-15");
            factura_testing.setFecha(convertedCurrentDate);
            factura_testing.setCliente(new Cliente("2495991", "JUAN BOGADO SUAREZ", "2495991-0"));
            
            factura_testing.setDetalle(new FacturaDetalle[1]);
            factura_testing.getDetalle()[0] = new FacturaDetalle();
            factura_testing.getDetalle()[0].setCantidad(1.0);
            factura_testing.getDetalle()[0].setPrecioUnitario(110000.0);
            factura_testing.getDetalle()[0].setSecuencia(1);
            factura_testing.getDetalle()[0].setProducto(new Producto("1025", "LECHE", getReferencia("MER")));
            
            
            factura=factura_testing;
            imprimirFactura();
            
        } catch (ParseException ex) {
            Logger.getLogger(CFReimpresion.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }//GEN-LAST:event_btImprimirActionPerformed

    private void cbImpresorasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbImpresorasActionPerformed
       setImpresora(cbImpresoras.getSelectedItem().toString());
    }//GEN-LAST:event_cbImpresorasActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btImprimir;
    private javax.swing.JCheckBox cbAutoImpresion;
    private javax.swing.JComboBox<String> cbImpresoras;
    private javax.swing.JComboBox<String> cbTalonarios;
    private javax.swing.JLabel eMensaje;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton mAyuda;
    private javax.swing.JButton mDefecto;
    private javax.swing.JButton mImprimir;
    private javax.swing.JButton mImprimir1;
    private javax.swing.JButton mLimpiar;
    private javax.swing.JPanel pMenu;
    private javax.swing.JTextField txNumero;
    private javax.swing.JTextField txUltimoImpreso;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String clase = getClass().getName().substring(getClass().getName().lastIndexOf(".") + 1, getClass().getName().length()).toUpperCase();
        String source = evt.getSource().toString().substring(evt.getSource().toString().lastIndexOf(".") + 1, evt.getSource().toString().indexOf("@")).replace("$", ".");
        String value = evt.getNewValue().toString();
        String id = (evt.getPropagationId() == null ? "" : evt.getPropagationId().toString());
        //evt.setPropagationId(clase);

        Consola.out(JColor.MAGENTA, "[CLASE]: " + clase + " [SOURCE]: " + source + " [VALUE]: " + value + " [PID]: " + id);

        if ("SWDiscovery.Consultar".equals(source)) {
            SWDiscovery.Consultar consulta = (SWDiscovery.Consultar) evt.getSource();
            if ("STARTED".equals(value)) {
                evt.setPropagationId(consulta.tabla);
            } else if ("DONE".equals(value)) {
                try {
                    switch (consulta.proceso) {
                        case "TALONARIOS":
                            cargarTalonarios(consulta.get());
                            buscarUltimaFactura();
                            cbTalonarios.setEnabled(true);
                            break;
                        case "REFERENCIAS_CONTABLES":
                            cargarReferencias(consulta.get());
                            buscarProductos();
                            break;
                        case "ULTIMA_FACTURA":
                            cargarUltimaFactura(consulta.get());
                            break;
                        case "FACTURA_ENCABEZADO":
                            cargarFactura(consulta.get());
                            buscarDetallesFactura();
                            break;
                        case "FACTURA_DETALLE":
                            cargarDetallesFactura(consulta.get());
                            break;
                        case "PRODUCTOS":
                            cargarProductos(consulta.get());
                            break;
                        case "CLIENTES":
                            cargarClientes();
                            break;
                        default:
                            System.out.println("No se encuentra regla para :" + consulta.tabla);
                            break;
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(CFReimpresion.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

    }
}
