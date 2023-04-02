package com.example.mlbpredictor.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrediccionResponse {

    private String equipoGanador;
    private double puntaje;
    private String detalles;
}
