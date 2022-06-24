/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package swing;

import com.github.lgooddatepicker.components.DatePicker;
/*
version 8
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
*/
import javax.swing.JComboBox;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

/**
 *
 * @author juan.bogado
 */
public class JDateSetter extends JComboBox {
    protected DatePicker desde, hasta;
    protected LocalDate hoy;
    protected DateTimeFormatter dtf;
    
    public JDateSetter(){
        super();
    }
    public JDateSetter(DatePicker fechaDesde, DatePicker fechaHasta){
        super();
        desde = fechaDesde;
        hasta = fechaHasta;
        hoy = LocalDate.now(ZoneId.of("-04:00"));
        dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        init();
        //fechas();
        
    }
    
    private void init(){
        
        setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hoy", "Ayer", "Mes actual", "Mes ultimo", "Mes anterior", "Año actual", "Año ultimo", "Año anterior", "Sin limites" }));
        setSelectedIndex(8);
        //setPreferredSize(new java.awt.Dimension(110, 30));
        
        addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                if(analytix.Analytix.DEBUG){
                    // HOY
                    System.out.println("HOY:"+hoy.toString());
                    System.out.println("HOY formateado:"+hoy.format(dtf));
                }
                
                
                desde.setText(hoy.format(dtf));
                hasta.setText(hoy.format(dtf));
                
                switch(getSelectedIndex()){
                    case 1: //AYER
                        desde.setText(hoy.minusDays(1).format(dtf));
                        hasta.setText(hoy.minusDays(1).format(dtf));
                        break;
                    case 2: //MES ACTUAL
                        desde.setText(hoy.withDayOfMonth(1).format(dtf));
                        hasta.setText(hoy.withDayOfMonth(hoy.lengthOfMonth()).format(dtf));
                        break;
                    case 3: //MES ULTIMO
                        desde.setText(hoy.minusMonths(1).format(dtf));
                        hasta.setText(hoy.format(dtf));
                        break;
                    case 4: //MES ANTERIOR
                        desde.setText(hoy.minusMonths(1).withDayOfMonth(1).format(dtf));
                        hasta.setText(hoy.withDayOfMonth(1).minusDays(1).format(dtf));
                        break;
                    case 5: //AÑO ACTUAL
                        desde.setText(hoy.withDayOfYear(1).format(dtf));
                        hasta.setText(hoy.withDayOfMonth(hoy.lengthOfMonth()).format(dtf));
                        break;
                    case 6: //AÑO ULTIMO
                        desde.setText(hoy.minusYears(1).format(dtf));
                        hasta.setText(hoy.format(dtf));
                        break;
                    case 7: //AÑO ANTERIOR
                        desde.setText(hoy.minusYears(1).withDayOfYear(1).format(dtf));
                        hasta.setText(hoy.minusYears(1).withMonth(12).withDayOfMonth(31).format(dtf)); //Esto tiene un error, esta bien hecho pero calcula mal, no resta al año anterior.
                        break;
                    case 8:
                        desde.setText("");
                        hasta.setText("");
                        break;
                }

            }
        });
    }
}
