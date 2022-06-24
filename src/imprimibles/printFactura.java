/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imprimibles;

import clases.Factura;
import clases.FacturaDetalle;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import tools.NumeroALetra;

/**
 *
 * @author Juan Bogado
 */
public class printFactura implements Printable{
    private Factura factura;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private static final NumberFormat dmf = new DecimalFormat("#,##0"); 
    private static final NumeroALetra num = new NumeroALetra();
    private Integer fC; //factor de Columna de duplicado
    private Integer lC; //linea de Cuerpo
    private Integer lP; //linea de Pie
    private Integer lN; //linea Nueva
    
    private Double cantidad;
    private Double precioUnitario;
    private Double precioGravado;
    private Double precioIva;
    private Double importeExento;
    private Double importeGravado5;
    private Double importeGravado10;
    private Double importeIva;
    private Double totalIva;
    private Double totalExento;
    private Double totalGravado5;
    private Double totalGravado10;
    private String sangria;

    public printFactura(Factura factura) {
        super();
        this.factura = factura;
        fC = 418;
        lC = 100; //120
        lP = 445;
        lN = 10;
        
        
        
    }
    
    
    public static String fmtNum(Double victima, int n){
        return String.format("%" + n + "s", dmf.format(victima));  
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex == 0) {
            totalIva = 0.0;
            totalExento = 0.0;
            totalGravado5 = 0.0;
            totalGravado10 = 0.0;
            
            
            
            graphics.setFont(new Font("MONOSPACED", Font.PLAIN, 6));
            
            graphics.drawString(factura.getTalonario().getPrefijo().toString(), 240,55);
            graphics.drawString(factura.getTalonario().getPrefijo().toString(), fC+240,55);
            
            graphics.drawString(factura.getNumero().toString(), 290,55);
            graphics.drawString(factura.getNumero().toString(), fC+290,55);
            
            graphics.setFont(new Font("MONOSPACED", Font.PLAIN, 8));
            
            graphics.drawString(sdf.format(factura.getFecha()), 130,90);
            graphics.drawString(sdf.format(factura.getFecha()), fC+130,90);
            
            graphics.drawString(factura.getCliente().getNombre(), 100,103);
            graphics.drawString(factura.getCliente().getNombre(), fC+100,103);
            
            graphics.drawString(factura.getCliente().getRuc(), 100,116);
            graphics.drawString(factura.getCliente().getRuc(), fC+100,116);
            
            graphics.drawString(factura.getCliente().getDireccion(), 100,130);
            graphics.drawString(factura.getCliente().getDireccion(), fC+100,130);
            
            graphics.drawString(factura.getCliente().getTelefono(), 300,130);
            graphics.drawString(factura.getCliente().getTelefono(), fC+300,130);
            
            graphics.setFont(new Font("MONOSPACED", Font.PLAIN, 6));
            
            graphics.drawString(factura.getMozo().getNombre(), 200,138);
            graphics.drawString(factura.getMozo().getNombre(), fC+200,138);
            
            graphics.drawString(factura.getMesa(), 308,138);
            graphics.drawString(factura.getMesa(), fC+308,138);
            
            graphics.drawString(factura.getCubiertos(), 350,138);
            graphics.drawString(factura.getCubiertos(), fC+350,138);
            
            graphics.drawString(factura.getHora(), 400,138);
            graphics.drawString(factura.getHora(), fC+400,138);
            
            graphics.setFont(new Font("MONOSPACED", Font.PLAIN, 8));
            
            for (FacturaDetalle facturaDetalle : factura.getDetalle()) {
                cantidad = facturaDetalle.getCantidad();
                precioUnitario = facturaDetalle.getPrecioUnitario();
                precioIva = precioUnitario / 21.0;
                precioGravado = precioUnitario - precioIva;
                importeIva = cantidad * precioIva;
                importeExento = cantidad * precioGravado / 2;
                importeGravado5 = 0.0;
                importeGravado10 = (cantidad * precioUnitario) - importeExento;
                totalIva += importeIva;
                totalExento += importeExento;
                totalGravado5 += importeGravado5;
                totalGravado10 += importeGravado10;
                                
                if(!facturaDetalle.getProducto().getCodigoSeleccion().contains("SA")){
                    if(!facturaDetalle.getProducto().getCodigo().trim().equals("X")){
                        if(!facturaDetalle.getProducto().getCodigo().trim().contains("/")){
                            graphics.drawString(cantidad.toString(), 48,lC);
                            graphics.drawString(cantidad.toString(), fC+48,lC);

                            graphics.drawString(facturaDetalle.getProducto().getCodigo(), 77,lC);
                            graphics.drawString(facturaDetalle.getProducto().getCodigo(), fC+77,lC);
                        }
                    }
                }
                
                if(!facturaDetalle.getProducto().getCodigo().trim().equals("X")){
                    if(facturaDetalle.getProducto().getCodigoSeleccion().contains("SA")){
                        sangria = "  ";
                        //graphics.setFont(new Font("MONOSPACED", Font.PLAIN, 6));
                    }else{
                        sangria = "";
                        //graphics.setFont(new Font("MONOSPACED", Font.PLAIN, 8));
                    }
                    graphics.drawString(sangria+facturaDetalle.getProducto().getDescripcion(), 105,lC);
                    graphics.drawString(sangria+facturaDetalle.getProducto().getDescripcion(), fC+105,lC);
                }
                
                if(precioUnitario > 0.0){
                    graphics.drawString(fmtNum(precioUnitario,10), 240,lC);
                    graphics.drawString(fmtNum(precioUnitario,10), fC+240,lC);

                    graphics.drawString(fmtNum(importeExento,10), 290,lC);
                    graphics.drawString(fmtNum(importeExento,10), fC+290,lC);

                    graphics.drawString(fmtNum(importeGravado10,10), 380,lC);
                    graphics.drawString(fmtNum(importeGravado10,10), fC+380,lC);
                }
                /*
                if(sangria.equals("")){
                    lC += lN;
                }else{
                    lC += lN-2;
                }
                */
                lC += lN;
            }
            
            graphics.drawString(fmtNum(totalExento,10), 290,lP+87);
            graphics.drawString(fmtNum(totalExento,10), fC+290,lP+87);
            
            graphics.drawString(fmtNum(totalGravado10,10), 380,lP+87);
            graphics.drawString(fmtNum(totalGravado10,10), fC+380,lP+87);
            
            graphics.drawString(num.doubleToString(factura.getTotal()), 110,lP+100);
            graphics.drawString(num.doubleToString(factura.getTotal()), fC+110,lP+100);
            
            graphics.drawString(fmtNum(factura.getTotal(),10), 380,lP+100);
            graphics.drawString(fmtNum(factura.getTotal(),10), fC+380,lP+100);
            
            //CORREGIR EL ACUMULADOR POR TIPO DE IVA
            graphics.drawString(fmtNum(totalIva,10), 250,lP+115);
            graphics.drawString(fmtNum(totalIva,10), fC+250,lP+115);
            
            
            graphics.drawString(fmtNum(totalIva,10), 370,lP+115);
            graphics.drawString(fmtNum(totalIva,10), fC+370,lP+115);
            
            
            
            //graphics.drawString("TEST", COLUMNA,LINEA);
            //graphics.drawString("580", 10,580);
            //graphics.drawString("590", 10,590);
            //graphics.drawString("600", 10,600);
            //graphics.drawString("610", 10,610); //llegan todos, este es el maximo
            
            System.out.println("PAGINADOR");
            return PAGE_EXISTS;
        }
        else{
            //System.out.println("no imprime");
            return NO_SUCH_PAGE;
        }
           
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }
    
    
    
}
