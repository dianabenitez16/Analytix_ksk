/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

/**
 *
 * @author Juan Bogado
 */
public class Validador {
    public static String validaString(Object objeto){
        String resultado = "";
        
        if(objeto != null){
            resultado = objeto.toString();
        }
        
        return resultado;
    }
    /*
    public static Double validaDouble(Object objeto){
        Double resultado = 0.0;
        Double factor = 1.0;
        String valor;
        
        if(objeto != null){
            valor = objeto.toString();
            if(valor.contains("-")){
                factor = -1.0;
                valor = valor.replace("-", "");
            }
            resultado =  Double.parseDouble(valor)*factor;
        }
        
        return resultado;
    }*/
    
    public static Double validaDouble(Object objeto){
        Double resultado = 0.0;
        
        if(objeto != null){
            resultado =  Double.parseDouble(objeto.toString());
        }
        
        return resultado;
    }
    
    public static Integer validaInteger(Object objeto){
        Integer resultado = 0;
        
        if(objeto != null){
            resultado =  Integer.parseInt(objeto.toString().trim());
        }
        
        return resultado;
    }
    
    public static Boolean validaProductoCodigo(Object objeto){
        if(objeto != null){
            if(objeto.toString().contains("/"))
                return false;
            
            if(objeto.toString().equals(""))
                return false;
            
            return true;
        }
        return false;
    }
    
}
