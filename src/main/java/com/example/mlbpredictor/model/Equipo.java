package com.example.mlbpredictor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Equipo {

    private int totalPartidos;
    private int totalPartidosCasa;
    private int totalPartidosVisitante;
    private int totalVictorias;
    private int totalDerrotas;
    private int totalDerrotasCasa;
    private int totalDerrotasVisitante;
    private int totalVictoriasCasa;
    private int totalVictoriasVisitante;
    private int totalSumaCarrerasRealizadas;
    private int totalSumaCarrerasPermitidas;
    private int totalPartidoGanadoDiferenciaDeUno;

    //@JsonIgnore
    private List<PartidoResultados> partidoResultados;

}
