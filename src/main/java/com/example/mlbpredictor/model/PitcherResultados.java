package com.example.mlbpredictor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PitcherResultados {

    private int numeroJuego;
    private int numeroJuegoTemporadaEquipo;
    private String nombre;
    private LocalDate fecha;
    private String equipoPrincipal;
    private String equipoRival;
    private String sede;
    private String resultado;
    private String marcador;
    private double ip;
    private int hits;
    private int carrerasPermitidas;
    private int basePorBola;
    private int ponches;
    private int  homeRunsPermitidos;
    private double era;
    private int calificacionGsc;
}
