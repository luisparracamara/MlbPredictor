package com.example.mlbpredictor.controller;

import com.example.mlbpredictor.model.response.EquipoResponse;
import com.example.mlbpredictor.model.request.ComparacionEquiposRequest;
import com.example.mlbpredictor.model.request.ComparacionPitcherRequest;
import com.example.mlbpredictor.model.request.PrediccionRequest;
import com.example.mlbpredictor.model.response.PitcherResponse;
import com.example.mlbpredictor.model.response.PrediccionResponse;
import com.example.mlbpredictor.service.ComparacionEquiposService;
import com.example.mlbpredictor.service.ComparacionPitcherService;
import com.example.mlbpredictor.service.PredecirResultadoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class Controller {

    private final ComparacionEquiposService comparacionEquiposService;

    private final ComparacionPitcherService comparacionPitcherService;

    private final PredecirResultadoService predecirResultadoService;

    public Controller(ComparacionEquiposService comparacionEquiposService, ComparacionPitcherService comparacionPitcherService, PredecirResultadoService predecirResultadoService) {
        this.comparacionEquiposService = comparacionEquiposService;
        this.comparacionPitcherService = comparacionPitcherService;
        this.predecirResultadoService = predecirResultadoService;
    }

    @PostMapping("/dataTeam")
    public ResponseEntity<List<EquipoResponse>> compararEquipos(@RequestBody ComparacionEquiposRequest equiposRequest) {

        List<EquipoResponse> response = comparacionEquiposService.compararEquipos(equiposRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/pitcherPrediction")
    public ResponseEntity<List<PitcherResponse>> compararPitchers(@RequestBody ComparacionPitcherRequest comparacionPitcherRequest) {

        List<PitcherResponse> response = comparacionPitcherService.compararPitchers(comparacionPitcherRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/matchPrediction")
    public ResponseEntity<PrediccionResponse> predecirResultado(@RequestBody PrediccionRequest prediccionRequest) {

        PrediccionResponse response =  predecirResultadoService.predecirResultado(prediccionRequest);

        return ResponseEntity.ok(response);
    }

}
