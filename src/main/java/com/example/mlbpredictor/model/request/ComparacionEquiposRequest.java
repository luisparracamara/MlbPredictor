package com.example.mlbpredictor.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComparacionEquiposRequest {

    private String equipoLocal;
    private String equipoVisitante;
}
