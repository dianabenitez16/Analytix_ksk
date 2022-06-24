/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

import java.util.Date;

/**
 *
 * @author junju
 */
public class Timbrado {
    private Integer tipoComprobate;
    private String numeroDesde;
    private String numeroHasta;
    private String numeroTimbrado;
    private Date fechaDesde;
    private Date fechaHasta;

    public Timbrado() {
    }

    public Integer getTipoComprobate() {
        return tipoComprobate;
    }

    public void setTipoComprobate(Integer tipoComprobate) {
        this.tipoComprobate = tipoComprobate;
    }

    public String getNumeroDesde() {
        return numeroDesde;
    }

    public void setNumeroDesde(String numeroDesde) {
        this.numeroDesde = numeroDesde;
    }

    public String getNumeroHasta() {
        return numeroHasta;
    }

    public void setNumeroHasta(String numeroHasta) {
        this.numeroHasta = numeroHasta;
    }

    public String getNumeroTimbrado() {
        return numeroTimbrado;
    }

    public void setNumeroTimbrado(String numeroTimbrado) {
        this.numeroTimbrado = numeroTimbrado;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }
    
    
}
