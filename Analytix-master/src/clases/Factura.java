/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.util.Date;

/**
 *
 * @author Juan Bogado
 */
public class Factura {
    private Talonario talonario;
    private Integer numero;
    private Cliente cliente;
    private Double total;
    private Date fecha;
    private FacturaDetalle[] detalle;
    private Mozo mozo;
    private String mesa;
    private String cubiertos;
    private String hora;

    public Factura() {
    }

    public Talonario getTalonario() {
        return talonario;
    }

    public void setTalonario(Talonario talonario) {
        this.talonario = talonario;
    }


    

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public FacturaDetalle[] getDetalle() {
        return detalle;
    }

    public void setDetalle(FacturaDetalle[] detalle) {
        this.detalle = detalle;
    }

    public Mozo getMozo() {
        return mozo;
    }

    public void setMozo(Mozo mozo) {
        this.mozo = mozo;
    }

    public String getMesa() {
        return mesa;
    }

    public void setMesa(String mesa) {
        this.mesa = mesa;
    }

    public String getCubiertos() {
        return cubiertos;
    }

    public void setCubiertos(String cubiertos) {
        this.cubiertos = cubiertos;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    
}
