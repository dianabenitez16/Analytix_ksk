/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package informes.contables;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import swing.JColor;
import system.Consola;

/**
 *
 * @author juan.bogado
 */
public class XLibroVenta {
    public JLabel estado;
    public String ultimoMensaje;
    
    public Properties configuracion;
    public String[] talonariosncr;
    public String[] importesconsolidados;
    
    
    public File folder = new File("informes\\contables");
    public String fileName;
    public Object[][] resultados;
    public List<String> encabezados;
    
    public Detallado detallado;
    public Consolidado consolidado;
    
    public int rowNum, colNum, reintentar;
    Row row;
    String comprobanteTipo;
    String comprobantePrefijo;
    String comprobanteNumero;
    String formaDePago;
    
    Boolean exento;

    SimpleDateFormat fFecha = new SimpleDateFormat("dd/MM/yyyy");
    XSSFWorkbook workbook;
    XSSFSheet sheet;
    
    public XLibroVenta(JLabel estado, Properties config) {
        this.configuracion = config;
        this.estado = estado;
        this.ultimoMensaje = "";
        
        procesarTalonarios();
    }
    
    private void procesarTalonarios(){
        talonariosncr = configuracion.getProperty("talonariosncr").split(",");
        importesconsolidados = configuracion.getProperty("importesconsolidados").split(",");
        
        
    }
    
    public void detallado(Object[][] res){
        fileName = "informes/contables/LibroVentas_detallado.xlsx";
        
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("LibroVentasDetallado");
    
        detallado = new Detallado(res);
    }
    
    public void consolidado(Object[][] res){
        fileName = "informes/contables/LibroVentas_consolidado.xlsx";
        
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("LibroVentasConsolidado");
    
        consolidado = new Consolidado(res);
    }
    
    public void fin(){
        do{
            reintentar = 1;
            try {
                if (!folder.exists()) { folder.mkdirs();}
                FileOutputStream outputStream = new FileOutputStream(fileName);
                workbook.write(outputStream);
                if (Desktop.isDesktopSupported()){
                    Desktop.getDesktop().edit(new File(fileName));
                }

            } catch (FileNotFoundException e) {
                    reintentar = JOptionPane.showOptionDialog(null, "No se pudo crear el archivo, asegurese de que no este abierto un archivo con el mismo nombre y tenga permisos de escritura. ¿Desea reintentar?", "Error al crear archivo", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null) ;
            } catch (IOException e) {
                    Consola.out(JColor.red,"IO Error");
                    reintentar = -1;
            }
        }while (reintentar == 0);

        try {
            workbook.close();
        } catch (IOException ex) {
            Consola.out(JColor.red,"Error al cerrar, quizas, no habia nada que cerrar." );
        }
    }
    
    public void print(String mensaje){
        if(!ultimoMensaje.equals(mensaje)){
            ultimoMensaje = mensaje;
            estado.setText(mensaje);
            estado.setForeground(JColor.blue);
            if(analytix.Analytix.DEBUG){
                Consola.out(JColor.blue, mensaje);
            }
        }
    }
    
    public class Detallado extends SwingWorker<Void, String>{
        public Detallado(Object[][] res) {
            resultados = res;
            encabezados = new ArrayList<>(resultados.length);
            
            for (Object cabecera : resultados[0]) {
                encabezados.add((cabecera.toString()));
            }

            rowNum = 0;
            colNum = 0;
        }

        @Override
        protected Void doInBackground()  {
            for (Object[] registro : resultados) {
                publish("Elaborando informe "+ rowNum*100/resultados.length + "%");

                row = sheet.createRow(rowNum++);

                colNum = 0;
                comprobanteTipo = "";
                comprobantePrefijo = "";
                comprobanteNumero = "";
                formaDePago = "";
                exento = false;

                for (Object campo : registro) {
                    Cell cell = row.createCell(colNum++);

                    if(rowNum == 1){
                        if(row.getLastCellNum() <=48){
                            cell.setCellValue((String) campo);
                        }
                    }else{
                        switch (encabezados.get(colNum -1)){ // SE RESTA 1 POR BUSQUEDA EN ARRAY QUE ARRANCA EN 0
                            case "ven_numero": // NUMERO DE COMPROBANTE
                                try{
                                    comprobanteNumero = ((String) campo).substring(((String) campo).indexOf("-")+1);
                                    comprobantePrefijo = ((String) campo).substring(0, ((String) campo).indexOf("-"));
                                    while(comprobantePrefijo.length() < 4){ 
                                        comprobantePrefijo = "0"+comprobantePrefijo;
                                    }
                                    
                                    while(comprobanteNumero.length() < 7){ // SE MODIFICA DE 8 A 7, PORQUE DA ERROR EN EL EXPERT
                                        comprobanteNumero = "0"+comprobanteNumero;
                                    }
                                    
                                    // Si el prefijo es tipo 1001,2001,3002, hace lo primero, y si es 0201, 0101, etc, hace lo segundo
                                    if(Integer.valueOf(comprobantePrefijo)>=1000){
                                        cell.setCellValue("00"+comprobantePrefijo.substring(0, 1)+"-"+comprobantePrefijo.substring(1, 4)+"-"+comprobanteNumero);
                                    }else{
                                        cell.setCellValue("0"+comprobantePrefijo.substring(0, 2)+"-0"+comprobantePrefijo.substring(2, 4)+"-"+comprobanteNumero);
                                    }            
                                    
                                    /*
                                    // RECORRER ARRAY DE TIMBRADOS Y COMPARAR EN CADA LINEA PARA IDENTIFICAR QUE NUMERO DE TIMBRADO LE CORRESPONDE, SEGUN NUMEORO, TIPO Y FECHA DEL COMPROBANTE
                                    for (Object Timbrado : Timbrados) {
                                        if(cell.getStringCellValue().compareTo(timbrado.getDesde))
                                            
                                            
                                            
                                    }
                                    */
                                    
                                    
                                }catch (StringIndexOutOfBoundsException ex){
                                    System.out.println("Error: "+ex);
                                    System.out.println("Prefijo: "+comprobantePrefijo + "  Numero: "+comprobanteNumero);
                                }
                                break;
                            case "ven_sucurs":
                                cell.setCellValue("0"+comprobantePrefijo.substring(0, 1));
                                break;
                            case "form_pag": // MEDIO DE PAGO
                                switch((Integer) campo){
                                    case 67:
                                    case 70:
                                        formaDePago = "Crédito";
                                        break;
                                    case 77:
                                    case 86:
                                    case 82:
                                        formaDePago = "Contado";
                                        break;
                                     
                                    default:
                                        formaDePago = "";
                                        break;
                                }
                                cell.setCellValue((String) formaDePago);
                                break;
                            case "ven_tipofa": //TIPO DE COMPROBANTE (CREDITO/CONTADO)
                                switch((Integer) campo){
                                    case 67:
                                    case 77:    
                                        comprobanteTipo = "Nota de crédito";
                                        break;
                                    case 70:
                                    case 86:    
                                        comprobanteTipo = "Factura";
                                        break;     
                                    case 82:
                                        comprobanteTipo = "Recibo";
                                        break;
                                    default:
                                        comprobanteTipo = String.valueOf(campo);
                                        break;
                                  
                                }
                                cell.setCellValue((String) comprobanteTipo);
                                break;
                            case "ven_iva": // VERIFICA IVA PARA EXENTO
                                if((Double) campo == 0){
                                    
                                    cell.getRow().getCell(20).setCellValue(cell.getRow().getCell(21).getNumericCellValue());
                                    cell.getRow().getCell(21).setCellValue(0);
                                }else{
                                    
                                    cell.getRow().getCell(15).setCellValue(configuracion.getProperty("cuentaventas10")); 
                                    /*
                                    if(cell.getRow().getCell(20).getNumericCellValue() > 0){
                                        cell.getRow().getCell(46).setCellValue(configuracion.getProperty("cuentaventasexe")); 
                                    }
                                    */
                                    cell.getRow().getCell(15).setCellValue(configuracion.getProperty("cuentaventas10")); 
                                }
                                cell.setCellValue((Double) campo);
                                break;
                            case "ven_fecven": // VERIFICA VENCIMIENTO PARA CUOTAS
                                if(campo instanceof Date){
                                    cell.getRow().getCell(27).setCellValue(1);
                                    cell.setCellValue((String) fFecha.format(campo));
                                }else{
                                    cell.setCellValue((String) campo);
                                }
                                break;
                            case "forma_devo": // VERIFICA ANULACION
                                if(comprobanteTipo.equals("Nota de crédito")){
                                    cell.setCellValue(1);
                                }else{
                                    cell.setCellValue(0);
                                }
                                break;
                            case "anular": // VERIFICA ANULACION
                                if((Integer) campo == 0){
                                    cell.setCellValue((Integer) 0);
                                }else{
                                    cell.getRow().getCell(12).setCellValue("Anulado");
                                    cell.setCellValue((Integer) 1);
                                }
                                break;
                            case "usu_ide":
                                // EN LA ULTIMA COLUMNA, VERIFICAMOS SI ES EXENTA, ENTONCES INSERTAMOS NUMERO DE CUENTA ENXENTA.
                                if(cell.getRow().getCell(21).getNumericCellValue() == 0){
                                    cell.getRow().getCell(46).setCellValue(configuracion.getProperty("cuentaventasexe")); // OJO AGREGA EN UNA COLUMNA DISTINTA A LA DE GRAVADA
                                    cell.getRow().getCell(14).setCellValue("77777701-0"); 
                                    cell.getRow().getCell(16).setCellValue("AGENTES DIPLOMATICOS"); 
                                }       
                                
                                if(Arrays.asList(importesconsolidados).contains(cell.getRow().getCell(14).getStringCellValue().trim())){
                                    cell.getRow().getCell(14).setCellValue("44444401-7"); 
                                    cell.getRow().getCell(16).setCellValue("IMPORTES CONSOLIDADOS"); 
                                }
                                break;
                            case "Prefijo":
                            case "MinNumero":
                            case "MaxNumero":
                                row.removeCell(cell);
                                break;
                            case "last_col":
                                if(cell.getRow().getCell(14).getStringCellValue().isEmpty() && !cell.getRow().getCell(48).getStringCellValue().isEmpty()){
                                    cell.getRow().getCell(14).setCellValue((String) cell.getRow().getCell(48).getStringCellValue()); 
                                }
                                if(cell.getRow().getCell(16).getStringCellValue().isEmpty() && !cell.getRow().getCell(49).getStringCellValue().isEmpty()){
                                    cell.getRow().getCell(16).setCellValue((String) cell.getRow().getCell(49).getStringCellValue()); 
                                }
                                row.removeCell(row.getCell(48));
                                row.removeCell(row.getCell(49));
                                row.removeCell(row.getCell(50));
                                //row.removeCell(cell);
                                break;
                            default:
                                 if (campo instanceof String) {
                                    cell.setCellValue((String) campo);
                                } else if (campo instanceof Integer) {
                                    cell.setCellValue((Integer) campo);
                                } else if (campo instanceof Date) {
                                    cell.setCellValue((String) fFecha.format(campo));
                                } else if (campo instanceof Double) {
                                    cell.setCellValue((Double) campo);
                                } else {
                                    cell.setCellValue((String) campo);
                                }
                        }
                    }

                }

                if(formaDePago.equals("") && rowNum >1){
                    sheet.removeRow(row);
                    rowNum--;
                }
            }
            return null;
        }
            
        @Override
        protected void done(){
            publish("Abriendo archivo ...");
            fin();
            publish("Listo.");
        }

        @Override
        protected void process(List<String> publish){
            print(publish.get(publish.size()-1));
        }
    }
    
    
    public class Consolidado extends SwingWorker<Void, String>{
        String newFecha, lastFecha;
        Integer newMinNumero, newMaxNumero, newTipoComp, lastMinNumero, lastMaxNumero, lastTipoComp;
        
        public Consolidado(Object[][] res) {
            resultados = res;
            encabezados = new ArrayList<>(resultados.length);
            
            for (Object cabecera : resultados[0]) {
                encabezados.add((cabecera.toString()));
            }

            rowNum = 0;
            colNum = 0;
        }

        @Override
        protected Void doInBackground()  {
            for (Object[] registro : resultados) {
                publish("Elaborando informe "+ rowNum*100/resultados.length + "%");
                
                row = sheet.createRow(rowNum++);

                colNum = 0;
                comprobanteTipo = "";
                comprobantePrefijo = "";
                comprobanteNumero = "";
                formaDePago = "";
                exento = false;

                for (Object campo : registro) {
                    Cell cell = row.createCell(colNum++);

                    if(rowNum == 1){
                        if(row.getLastCellNum() <=48){
                            cell.setCellValue((String) campo);
                        }
                    }else{
                        switch (encabezados.get(colNum -1)){ // SE RESTA 1 POR BUSQUEDA EN ARRAY QUE ARRANCA EN 0
                            case "ven_numero": // NUMERO DE COMPROBANTE

                                try{
                                    comprobantePrefijo = registro[encabezados.indexOf("Prefijo")].toString();
                                    comprobanteNumero = registro[encabezados.indexOf("MinNumero")].toString() + "-" + registro[encabezados.indexOf("MaxNumero")].toString();

                                    if(comprobantePrefijo.equals("0")){
                                        comprobantePrefijo = "0000";
                                    }
                                    while(comprobanteNumero.length() < 8){
                                        comprobanteNumero = "0"+comprobanteNumero;
                                    }

                                    //cell.setCellValue("00"+comprobantePrefijo.substring(0, 1)+"-"+comprobantePrefijo.substring(1, 4)+"-"+comprobanteNumero);
                                    cell.setCellValue(comprobantePrefijo.substring(0, 1)+"-"+comprobanteNumero);
                                }catch (StringIndexOutOfBoundsException ex){
                                    System.out.println("Error: "+ex);
                                    System.out.println("Prefijo: "+comprobantePrefijo + "  Numero: "+comprobanteNumero);
                                }

                                break;
                            case "ven_sucurs":
                                cell.setCellValue("0"+comprobantePrefijo.substring(0, 1));
                                break;
                            case "form_pag": // MEDIO DE PAGO
                                formaDePago = (String) campo;
                                cell.setCellValue((String) formaDePago);
                                break;
                            case "ven_tipofa": //TIPO DE COMPROBANTE (CREDITO/CONTADO)
                                if(Arrays.asList(talonariosncr).contains(campo.toString())){ 
                                        comprobanteTipo = "Nota de Crédito";
                                }else{
                                        comprobanteTipo = "Contado";
                                }
                                cell.setCellValue((String) comprobanteTipo);
                                break;
                            case "ven_iva": // VERIFICA IVA PARA EXENTO
                                if((Double) campo == 0){
                                    cell.getRow().getCell(20).setCellValue(cell.getRow().getCell(21).getNumericCellValue());
                                    cell.getRow().getCell(21).setCellValue(0);
                                }else{
                                    cell.getRow().getCell(15).setCellValue(configuracion.getProperty("cuentaventas10"));
                                }
                                cell.setCellValue((Double) campo);
                                break;
                            case "ven_fecven": // VERIFICA VENCIMIENTO PARA CUOTAS
                                if(campo instanceof Date){
                                    cell.getRow().getCell(27).setCellValue(1);
                                    cell.setCellValue((String) fFecha.format(campo));
                                }else{
                                    cell.setCellValue((String) campo);
                                }
                                break;
                            case "forma_devo": // VERIFICA ANULACION
                                if(comprobanteTipo.equals("Nota de Crédito")){
                                    cell.setCellValue(1);
                                }else{
                                    cell.setCellValue(0);
                                }
                                break;
                            case "anular": // VERIFICA ANULACION
                                if((Integer) campo == 0){
                                    cell.setCellValue((Integer) 0);
                                }else{
                                    cell.getRow().getCell(12).setCellValue("Anulado");
                                    cell.setCellValue((Integer) 1);
                                }
                                break;
                            case "usu_ide": 
                                // EN LA ULTIMA COLUMNA, VERIFICAMOS SI ES EXENTA, ENTONCES INSERTAMOS NUMERO DE CUENTA ENXENTA.
                                if(cell.getRow().getCell(21).getNumericCellValue() == 0){
                                    cell.getRow().getCell(46).setCellValue(configuracion.getProperty("cuentaventasexe"));
                                    cell.getRow().getCell(14).setCellValue("77777701-0"); 
                                    cell.getRow().getCell(16).setCellValue("AGENTES DIPLOMATICOS"); 
                                }
                                break;
                            case "Prefijo":
                            case "MinNumero":
                            case "MaxNumero":
                            case "tmp_nom":
                            case "tmp_cui":
                                row.removeCell(cell);
                                break;
                            default:
                                 if (campo instanceof String) {
                                    cell.setCellValue((String) campo);
                                } else if (campo instanceof Integer) {
                                    cell.setCellValue((Integer) campo);
                                } else if (campo instanceof Date) {
                                    cell.setCellValue((String) fFecha.format(campo));
                                } else if (campo instanceof Double) {
                                    cell.setCellValue((Double) campo);
                                } else {
                                    cell.setCellValue((String) campo);
                                }
                        }
                    }

                }

                if(formaDePago.equals("") && rowNum >1){
                    sheet.removeRow(row);
                    rowNum--;
                }
            }
            return null;
        }

        
        @Override
        protected void done(){
            publish("Abriendo archivo ...");
            fin();
            publish("Listo.");
        }

        @Override
        protected void process(List<String> publish){
            print(publish.get(publish.size()-1));
        }
    }
}
