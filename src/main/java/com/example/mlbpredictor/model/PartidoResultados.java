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
public class PartidoResultados {

    private int numeroJuego;
    private LocalDate fecha;
    private String equipoPrincipal;
    private String equipoOponente;
    private String sede;
    private String resultado;
    private int carrerasRealizadas;
    private int carrerasPermitidas;




}
