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
public class Talonario {
    private Integer codigo;
    private Integer prefijo;

    public Talonario(Integer codigo, Integer prefijo) {
        this.codigo = codigo;
        this.prefijo = prefijo;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(Integer prefijo) {
        this.prefijo = prefijo;
    }
    
    
}
