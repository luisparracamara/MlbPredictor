package com.example.mlbpredictor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Series {

    private int totalSeries;
    private double promedioVictoriasPorSerie;
    private double promedioDerrotasPorSerie;

}
