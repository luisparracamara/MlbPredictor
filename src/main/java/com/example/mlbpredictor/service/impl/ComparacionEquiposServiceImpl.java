package com.example.mlbpredictor.service.impl;

import com.example.mlbpredictor.client.BaseballReference;
import com.example.mlbpredictor.mapper.PartidosResultadosMapper;
import com.example.mlbpredictor.model.response.EquipoResponse;
import com.example.mlbpredictor.model.PartidoResultados;
import com.example.mlbpredictor.model.Series;
import com.example.mlbpredictor.model.Equipo;
import com.example.mlbpredictor.model.request.ComparacionEquiposRequest;
import com.example.mlbpredictor.properties.MlbProperties;
import com.example.mlbpredictor.service.ComparacionEquiposService;
import com.example.mlbpredictor.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class ComparacionEquiposServiceImpl implements ComparacionEquiposService {

    private final BaseballReference baseballReference;

    private final Utils utils;

    private final PartidosResultadosMapper partidoResultadosMapper;

    private final MlbProperties mlbProperties;

    public ComparacionEquiposServiceImpl(BaseballReference baseballReference, Utils utils,
            PartidosResultadosMapper partidoResultadosMapper, MlbProperties mlbProperties) {
        this.baseballReference = baseballReference;
        this.utils = utils;
        this.partidoResultadosMapper = partidoResultadosMapper;
        this.mlbProperties = mlbProperties;
    }

    @Override
    public List<EquipoResponse> compararEquipos(ComparacionEquiposRequest equiposRequest) {

        Document equipoLocal = baseballReference.getHtml("https://www.baseball-reference.com/teams/"
                +equiposRequest.getEquipoLocal().toUpperCase()+"/"+mlbProperties.getSeason()+"-schedule-scores.shtml");

        Document equipoVisitante = baseballReference.getHtml("https://www.baseball-reference.com/teams/"
                +equiposRequest.getEquipoVisitante().toUpperCase()+"/"+mlbProperties.getSeason()+"-schedule-scores.shtml");

        responseEquipo(equipoLocal, equiposRequest.getEquipoLocal().toUpperCase());
        responseEquipo(equipoVisitante, equiposRequest.getEquipoVisitante().toUpperCase());

        return Arrays.asList(
                responseEquipo(equipoLocal, equiposRequest.getEquipoLocal()),
                responseEquipo(equipoVisitante, equiposRequest.getEquipoVisitante())
        );
    }

    private EquipoResponse responseEquipo(Document equipoData, String nombreEquipo) {

        List<PartidoResultados> partidoResultados = obtenerResultadosEquipo(equipoData);
        Equipo equipoResponse = calcularPartidosLocalVisitante(partidoResultados);
        Series series = obtenerDatosSeries(
                partidoResultados, equipoResponse.getTotalVictorias(),
                equipoResponse.getTotalDerrotas());

        return EquipoResponse.builder()
                .nombre(nombreEquipo)
                .equipo(equipoResponse)
                .series(series)
                .build();
    }

    private List<PartidoResultados> obtenerResultadosEquipo(Document resultadosEquipo) {

        Elements resultados = resultadosEquipo.select("tbody").select("tr");

        List<PartidoResultados> partidoResultados = new ArrayList<>();

        for (String resultado : resultados.eachText()) {

            String[] partidos = resultado.split(" ");

            if (!partidos[0].equals("Gm#")) {
                Map<String, String> resultadosMap = getResultadosMap(partidos);

                if (resultadosMap.isEmpty()){break;}

                partidoResultados.add(partidoResultadosMapper.partidosResultadosMapper(resultadosMap));

                LocalDate fechaLimite = LocalDate.of(mlbProperties.getSeason(), mlbProperties.getLimitMonth(),
                        mlbProperties.getLimitDay());
                if (partidoResultados.get(partidoResultados.size()-1).getFecha().isAfter(fechaLimite)) {
                    partidoResultados.remove(partidoResultados.size()-1);
                    return partidoResultados;
                }
            }
        }

        return partidoResultados;
    }


    private Map<String, String> getResultadosMap(String[] partido) {

        Map<String, String> resultados = new HashMap<>();

        if(partido[4].equals("preview")){
            return resultados;
        }

        String fecha = (partido[2]+" "+partido[3]).replace(",", " ");

        resultados.put("numeroJuego", partido[0]);
        resultados.put("fecha", fecha);

        if (partido[5].equals("boxscore") && partido[7].equals("@")) {
            return crearResultadosMap(resultados, partido[6], partido[8], "visitante",
                    utils.resultadoFormato(partido[9]), partido[10], partido[11]);
        }

        if (partido[4].equals("boxscore") && partido[6].equals("@")) {
            return crearResultadosMap(resultados, partido[5], partido[7], "visitante",
                    utils.resultadoFormato(partido[8]), partido[9], partido[10]);
        }

        if (partido[5].equals("boxscore")) {
            return crearResultadosMap(resultados, partido[6], partido[7], "local",
                    utils.resultadoFormato(partido[8]), partido[9], partido[10] );
        }

        return crearResultadosMap(resultados, partido[5], partido[6], "local",
                utils.resultadoFormato(partido[7]), partido[8], partido[9]);
    }


    private Map<String, String> crearResultadosMap(Map<String, String> resultados, String equipoPrincipal,
            String equipoOponente, String sede, String resultado, String carrerasRealizadas,
            String carrerasPermitidas) {

        resultados.put("equipoPrincipal", equipoPrincipal);
        resultados.put("equipoOponente", equipoOponente);
        resultados.put("sede", sede);
        resultados.put("resultado", resultado);
        resultados.put("carrerasRealizadas", carrerasRealizadas);
        resultados.put("carrerasPermitidas", carrerasPermitidas);

        return resultados;
    }


    private Equipo calcularPartidosLocalVisitante(List<PartidoResultados> partidoResultados) {

        long partidosGanados = partidoResultados.stream()
                .filter(resultado -> resultado.getResultado().equals("W"))
                .count();

        long partidosPerdidos = partidoResultados.stream()
                .filter(resultado ->  resultado.getResultado().equals("L"))
                .count();

        long partidosLocalGanados = partidoResultados.stream()
                .filter(resultado -> resultado.getSede().equals("local") && resultado.getResultado().equals("W"))
                .count();

        long partidosVisitantesGanados = partidoResultados.stream()
                .filter(resultado -> resultado.getSede().equals("visitante") && resultado.getResultado().equals("W"))
                .count();

        long partidosVisitantePerdidos = partidoResultados.stream()
                .filter(resultado -> resultado.getSede().equals("visitante") && resultado.getResultado().equals("L"))
                .count();

        long partidosLocalPerdidos = partidoResultados.stream()
                .filter(resultado -> resultado.getSede().equals("local") && resultado.getResultado().equals("L"))
                .count();

        int totalSumaCarrerasRealizadas = partidoResultados.stream().mapToInt(PartidoResultados::getCarrerasRealizadas).sum();

        int totalSumaCarrerasPermitidas= partidoResultados.stream().mapToInt(PartidoResultados::getCarrerasPermitidas).sum();

        long partidoGanadoDiferenciaDeUno = partidoResultados.stream()
                .filter(resultado -> resultado.getResultado().equals("W"))
                .filter(resultado -> (resultado.getCarrerasRealizadas() - resultado.getCarrerasPermitidas()) == 1 )
                .count();

        return Equipo.builder()
                .totalPartidos((int) (partidosGanados+partidosPerdidos))
                .totalPartidosCasa((int) (partidosLocalGanados+partidosLocalPerdidos))
                .totalPartidosVisitante((int) (partidosVisitantesGanados+partidosVisitantePerdidos))
                .totalVictorias((int)partidosGanados)
                .totalDerrotas((int)partidosPerdidos)
                .totalDerrotasCasa((int)partidosLocalPerdidos)
                .totalDerrotasVisitante((int)partidosVisitantePerdidos)
                .totalVictoriasCasa((int)partidosLocalGanados)
                .totalVictoriasVisitante((int)partidosVisitantesGanados)
                .partidoResultados(partidoResultados)
                .totalSumaCarrerasRealizadas(totalSumaCarrerasRealizadas)
                .totalSumaCarrerasPermitidas(totalSumaCarrerasPermitidas)
                .totalPartidoGanadoDiferenciaDeUno((int) partidoGanadoDiferenciaDeUno)
                .build();

    }

    private Series obtenerDatosSeries(List<PartidoResultados> partidoResultados, int totalVictorias, int totalDerrotas) {

        String equipoActual = "";
        int contador = 0;

        for (PartidoResultados resultado: partidoResultados) {

            if (!equipoActual.equals(resultado.getEquipoOponente())){
                equipoActual = resultado.getEquipoOponente();
                contador++;
            }
        }

        if (contador == 0) contador = 1;

        return Series.builder()
                .totalSeries(contador)
                .promedioVictoriasPorSerie(((double) totalVictorias / contador))
                .promedioDerrotasPorSerie(((double) totalDerrotas / contador))
                .build();

    }

}
