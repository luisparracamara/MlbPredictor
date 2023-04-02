package com.example.mlbpredictor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pitcher {

    private int totalPartidos;
    private int totalPartidosCasa;
    private int totalPartidosVisitante;
    private int totalVictorias;
    private int totalDerrotas;
    private int totalDerrotasCasa;
    private int totalDerrotasVisitante;
    private int totalVictoriasCasa;
    private int totalVictoriasVisitante;

    private double totalIp;
    private int totalHitsPermitidos;
    private int totalCarrerasPermitidas;
    private int totalBasePorBola;
    private int totalPonches;
    private int totalHomeRunsPermitidos;

    private double promedioEra;
    private int promedioCalificacionGsc;
    private double promedioHitsPorPartido;
    private double promedioCarrerasPorPartido;
    private double promedioBasesPorBolaPorPartido;
    private double promedioPonchesPorPartido;
    private double promedioHomeRunsPorPartido;
    private double promedioIpPorPartido;

}
