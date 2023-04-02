package com.example.mlbpredictor.mapper;

import com.example.mlbpredictor.model.PartidoResultados;
import com.example.mlbpredictor.utils.Utils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PartidosResultadosMapper {

    private final Utils utils;

    public PartidosResultadosMapper(Utils utils) {
        this.utils = utils;
    }

    public PartidoResultados partidosResultadosMapper(Map<String, String> partido) {
        return PartidoResultados.builder()
                .numeroJuego(Integer.parseInt(partido.get("numeroJuego")))
                .fecha(utils.formatearFecha(partido.get("fecha").split(" ")) )
                .equipoPrincipal(partido.get("equipoPrincipal"))
                .equipoOponente(partido.get("equipoOponente"))
                .sede(partido.get("sede"))
                .resultado(partido.get("resultado"))
                .carrerasRealizadas(Integer.parseInt(partido.get("carrerasRealizadas")))
                .carrerasPermitidas(Integer.parseInt(partido.get("carrerasPermitidas")))
                .build();
    }


}
