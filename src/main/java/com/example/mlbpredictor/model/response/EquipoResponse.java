package com.example.mlbpredictor.model.response;

import com.example.mlbpredictor.model.Series;
import com.example.mlbpredictor.model.Equipo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EquipoResponse {

    private String nombre;
    private Equipo equipo;
    private Series series;

}
