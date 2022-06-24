/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analytix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Juan Bogado
 */
public class Configuracion {
    public static FileInputStream fisConfiguracion = null;
    public static OutputStream osConfiguracion = null;
    public static File archivo;
    
    public static String confdir =  "conf";
    public static String subdir =  "/";
    public static String confext =  ".cfg";
    
    
    public static void loadProperties(Properties prop, String file){
        try {
            fisConfiguracion = new FileInputStream(confdir+subdir+file+confext);
            prop.load(fisConfiguracion);
            fisConfiguracion.close();
            
        } catch (FileNotFoundException ex) {
            System.out.println("No existe archivo de configuracion, se crea.");
            newProperties(prop, file);
        } catch (IOException ex) {
            Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, null, ex);
            newProperties(prop, file);
        } catch (NullPointerException ex) {
            System.out.println("El archivo de configuracion de "+file+" esta vacio.");
            newProperties(prop, file);
        } catch (Exception ex) {
            Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void newProperties(Properties prop, String file){
        try {
            archivo = new File(confdir+subdir);
            archivo.mkdirs();
            
            osConfiguracion = new FileOutputStream(confdir+subdir+file+confext);
            
            switch (file) {
                case "hechauka":
                    prop.setProperty("importesconsolidados", "XXX");
                    prop.setProperty("talonariosfactura", "71,72,73,74,75,76");
                    prop.setProperty("talonariosncr", "81,82,83,84,85,86");
                    prop.setProperty("talonariosrecibo", "61,62,63,64");
                    prop.setProperty("cuentaventas10", "41111");
                    prop.setProperty("cuentaventas5", "41112");
                    prop.setProperty("cuentaventasexe", "41110");
                    prop.setProperty("esrestaurante", "si");
                    break;
                case "reimpresion":
                    prop.setProperty("impresora", "Fax");
                    prop.setProperty("talonario", "71");
                    prop.setProperty("autoimpresion","NO");
                    prop.setProperty("ultimoimpreso","0");
                    break;
                default:
                    break;
            }
            
            prop.store(osConfiguracion, null);
            osConfiguracion.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    public static void saveProperties(Properties prop, String file){
        try {
            archivo = new File(confdir+subdir);
            archivo.mkdirs();
            
            osConfiguracion = new FileOutputStream(confdir+subdir+file+confext);
            
            
            prop.store(osConfiguracion, null);
            osConfiguracion.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
