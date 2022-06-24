/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

/**
 *
 * @author Juan Bogado
 */
public class Producto {
    private String codigo;
    private String descripcion;
    private ReferenciaContable referenciaContable;
    private String codigoSeleccion;

    public Producto() {
    }

    public Producto(String codigo, String descripcion, ReferenciaContable referenciaContable, String codigoSeleccion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.referenciaContable = referenciaContable;
        this.codigoSeleccion = codigoSeleccion;
    }
     
    
        
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ReferenciaContable getReferenciaContable() {
        return referenciaContable;
    }

    public void setReferenciaContable(ReferenciaContable referenciaContable) {
        this.referenciaContable = referenciaContable;
    }

    public String getCodigoSeleccion() {
        return codigoSeleccion;
    }

    public void setCodigoSeleccion(String codigoSeleccion) {
        this.codigoSeleccion = codigoSeleccion;
    }
    
    
    
    
}
