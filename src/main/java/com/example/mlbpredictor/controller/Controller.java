package com.example.mlbpredictor.controller;

import com.example.mlbpredictor.model.request.HistoricalResultsRequest;
import com.example.mlbpredictor.model.response.EquipoResponse;
import com.example.mlbpredictor.model.request.ComparacionEquiposRequest;
import com.example.mlbpredictor.model.request.ComparacionPitcherRequest;
import com.example.mlbpredictor.model.request.PrediccionRequest;
import com.example.mlbpredictor.model.response.GetResults;
import com.example.mlbpredictor.model.response.PitcherResponse;
import com.example.mlbpredictor.model.response.PrediccionResponse;
import com.example.mlbpredictor.service.ComparacionEquiposService;
import com.example.mlbpredictor.service.ComparacionPitcherService;
import com.example.mlbpredictor.service.ObtenerResultadosActualService;
import com.example.mlbpredictor.service.PredecirResultadoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class Controller {

    private final ComparacionEquiposService comparacionEquiposService;

    private final ComparacionPitcherService comparacionPitcherService;

    private final PredecirResultadoService predecirResultadoService;

    private final ObtenerResultadosActualService obtenerResultadosActualService;

    public Controller(ComparacionEquiposService comparacionEquiposService, ComparacionPitcherService comparacionPitcherService, PredecirResultadoService predecirResultadoService, ObtenerResultadosActualService obtenerResultadosActualService) {
        this.comparacionEquiposService = comparacionEquiposService;
        this.comparacionPitcherService = comparacionPitcherService;
        this.predecirResultadoService = predecirResultadoService;
        this.obtenerResultadosActualService = obtenerResultadosActualService;
    }

    @PostMapping("/teamData")
    public ResponseEntity<List<EquipoResponse>> compararEquipos(@RequestBody ComparacionEquiposRequest equiposRequest) {

        List<EquipoResponse> response = comparacionEquiposService.compararEquipos(equiposRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/pitcherData")
    public ResponseEntity<List<PitcherResponse>> compararPitchers(@RequestBody ComparacionPitcherRequest comparacionPitcherRequest) {

        List<PitcherResponse> response = comparacionPitcherService.compararPitchers(comparacionPitcherRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/matchPrediction")
    public ResponseEntity<PrediccionResponse> predecirResultado(@RequestBody PrediccionRequest prediccionRequest) {

        PrediccionResponse response =  predecirResultadoService.predecirResultado(prediccionRequest);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getTodayMatches")
    public ResponseEntity<List<PrediccionResponse>> getTodayMatches() {
        List<PrediccionResponse> response =  obtenerResultadosActualService.getTodayMatches();
        return ResponseEntity.ok(response);
    }

    //api para poder obtener los resultados salidos de las fechas de la temporada 2023, hasta la fecha indicada
    //https://www.baseball-reference.com/boxes/?month=4&day=28&year=2023
    //convertir la fecha a localdate time y restarle un día
    //esto serviría para calcular los resultados de la fecha actual hacia atrás, solo variando en la fecha, de todos los equipos
    @PostMapping("/getAllResults")
    public ResponseEntity<GetResults> getHistoricalResults(@RequestBody HistoricalResultsRequest request) {
        return ResponseEntity.ok(obtenerResultadosActualService.getHistoricalResults(request));
    }

    @GetMapping("/getTodayPrediction")
    public ResponseEntity<List<PrediccionResponse>> getTodayPrediction() {
        return ResponseEntity.ok(obtenerResultadosActualService.getTodayPrediction());
    }

}
