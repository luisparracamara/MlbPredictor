package com.example.mlbpredictor.model.response;

import com.example.mlbpredictor.model.Pitcher;
import com.example.mlbpredictor.model.PitcherResultados;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PitcherResponse {

    private String nombre;
    private String equipo;
    private Pitcher pitcher;

    //@JsonIgnore
    private List<PitcherResultados> pitcherResultados;
}
