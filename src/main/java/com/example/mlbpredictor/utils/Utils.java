package com.example.mlbpredictor.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class Utils {

    public String resultadoFormato(String resultado) {
        if (resultado.length() > 1) {
            resultado = String.valueOf(resultado.charAt(0));
        }
        return resultado;
    }

    public LocalDate formatearFecha(String[] fecha) {

        StringBuilder dia = new StringBuilder();

       if (fecha[1].length() > 2) {
           for (int i = 0; i < fecha[1].length(); i++) {
               if (fecha[1].charAt(i) == '('){
                   break;
               }
               dia.append(fecha[1].charAt(i));
           }
           fecha[1] = dia.toString();
       }

        Map<String, Integer> meses = new HashMap<>();

        meses.put("Jan", 1);
        meses.put("Feb", 2);
        meses.put("Mar", 3);
        meses.put("Apr", 4);
        meses.put("May", 5);
        meses.put("Jun", 6);
        meses.put("Jul", 7);
        meses.put("Aug", 8);
        meses.put("Sep", 9);
        meses.put("Oct", 10);
        meses.put("Nov", 11);
        meses.put("Dec", 12);

        return LocalDate.of(2022, meses.get(fecha[0]), Integer.parseInt(fecha[1]));
    }

}
