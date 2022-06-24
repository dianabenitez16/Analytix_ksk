/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author juan.bogado
 */
public class FacturaX {
    public Date fecha;
    public Boolean anulado;
    public Integer caja;
    public String tipo;
    public String clienteRUC;
    public String vendedor;
    public Double total;
    public SimpleDateFormat formatoDB = new SimpleDateFormat("yyyy-MM-dd");
    public SimpleDateFormat formatoUSR = new SimpleDateFormat("dd/MM/yyyy");
    public NumberFormat formatoTOT = new DecimalFormat("#0,000.00");

    public FacturaX(Date fecha, Boolean anulado, Integer caja, String tipo, String clienteRUC, String vendedor, Double total) {
        this.fecha = fecha;
        this.anulado = anulado;
        this.caja = caja;
        this.tipo = tipo;
        this.clienteRUC = clienteRUC;
        this.vendedor = vendedor;
        this.total = total;
    }
    
    public FacturaX(String fecha, String anulado, String caja, String tipo, String clienteRUC, String vendedor, String total) {
        
        try {
            this.fecha = formatoDB.parse(fecha);
        } catch (ParseException ex) {
            Logger.getLogger(FacturaX.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.anulado = !anulado.equals("0");
        this.caja = Integer.parseInt(caja);
        
        if(tipo.equals("86")){
            this.tipo = "Contado";
        }else if(tipo.equals("70")){
            this.tipo = "Crédito";
        }else{
            this.tipo = "Otro";
        }
        
        this.clienteRUC = clienteRUC;
        this.vendedor = vendedor;
        this.total = Double.parseDouble(total);
    }

    public Date getFecha() {
        return fecha;
    }
    
    public String getFechaString() {
        return formatoUSR.format(fecha);
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Boolean getAnulado() {
        return anulado;
    }

    public void setAnulado(Boolean anulado) {
        this.anulado = anulado;
    }

    public Integer getCaja() {
        return caja;
    }

    public void setCaja(Integer caja) {
        this.caja = caja;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getClienteRUC() {
        return clienteRUC;
    }

    public void setClienteRUC(String clienteRUC) {
        this.clienteRUC = clienteRUC;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public Double getTotal() {
        return total;
    }
    
    public String getTotalString() {
        return formatoTOT.format(total);
    }

    public void setTotal(Double total) {
        this.total = total;
    }
    
    
}
