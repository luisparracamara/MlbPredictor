package com.example.mlbpredictor.service.impl;

import com.example.mlbpredictor.model.Puntaje;
import com.example.mlbpredictor.model.request.ComparacionEquiposRequest;
import com.example.mlbpredictor.model.request.ComparacionPitcherRequest;
import com.example.mlbpredictor.model.request.PrediccionRequest;
import com.example.mlbpredictor.model.response.EquipoResponse;
import com.example.mlbpredictor.model.response.PitcherResponse;
import com.example.mlbpredictor.model.response.PrediccionResponse;
import com.example.mlbpredictor.service.ComparacionEquiposService;
import com.example.mlbpredictor.service.ComparacionPitcherService;
import com.example.mlbpredictor.service.PredecirResultadoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PredecirResultadoServiceImpl implements PredecirResultadoService {

    private final ComparacionEquiposService comparacionEquiposService;

    private final ComparacionPitcherService comparacionPitcherService;

    public PredecirResultadoServiceImpl(ComparacionEquiposService comparacionEquiposService, ComparacionPitcherService comparacionPitcherService) {
        this.comparacionEquiposService = comparacionEquiposService;
        this.comparacionPitcherService = comparacionPitcherService;
    }

    @Override
    public PrediccionResponse predecirResultado(PrediccionRequest prediccionRequest) {

        ComparacionEquiposRequest comparacionEquiposRequest = ComparacionEquiposRequest.builder()
                .equipoLocal(prediccionRequest.getEquipoLocal())
                .equipoVisitante(prediccionRequest.getEquipoVisitante())
                .build();

        List<EquipoResponse> equipos = comparacionEquiposService.compararEquipos(comparacionEquiposRequest);

        ComparacionPitcherRequest comparacionPitcherRequest = ComparacionPitcherRequest.builder()
                .pitcherLocal(prediccionRequest.getPitcherLocal())
                .pitcherVisitante(prediccionRequest.getPitcherVisitante())
                .build();

        List<PitcherResponse> pitchers = comparacionPitcherService.compararPitchers(comparacionPitcherRequest);

        if (equipos.size() == 2 && pitchers.size() == 2 ) {
            Puntaje equipoLocal= calcularPuntaje(equipos.get(0), pitchers.get(0), "local");
            Puntaje equipoVisitante= calcularPuntaje(equipos.get(1), pitchers.get(1), "visitante");

            double puntosTotalesLocal = sumarPuntos(equipoLocal);
            double puntosTotalesVisitante = sumarPuntos(equipoVisitante);

            log.info("Comparación de la predicción: Equipo local {} Puntos {}", equipos.get(0).getNombre(),
                    puntosTotalesLocal);
            log.info("Comparación de la predicción: Equipo visitante {} Puntos {}", equipos.get(1).getNombre(),
                    puntosTotalesVisitante);

            if (puntosTotalesLocal > puntosTotalesVisitante) {

                PrediccionResponse prediccionResponseCasa = PrediccionResponse.builder()
                        .equipoGanador(equipos.get(0).getNombre())
                        .puntaje(puntosTotalesLocal).build();

                if (puntosTotalesLocal-puntosTotalesVisitante <= 5) {
                    prediccionResponseCasa.setDetalles("La diferencia es mínima, pronóstico arriesgado");
                }

                return prediccionResponseCasa;
            }

            PrediccionResponse prediccionResponseVisitante =  PrediccionResponse.builder()
                    .equipoGanador(equipos.get(1).getNombre())
                    .puntaje(puntosTotalesVisitante).build();

            if (puntosTotalesVisitante-puntosTotalesLocal <= 5) {
                prediccionResponseVisitante.setDetalles("La diferencia es mínima, pronóstico arriesgado");
            }

            return prediccionResponseVisitante;
        }

        //tirar excepcion
        return PrediccionResponse.builder().equipoGanador("no hay ganador").build();
    }

    public Puntaje calcularPuntaje(EquipoResponse equipo, PitcherResponse pitcher, String sede) {

        double porcentajeVictoriasEquipo = (double) (100 * equipo.getEquipo().getTotalVictorias())/equipo.getEquipo().getTotalPartidos();

        double porcentajeVictoriasPitcher = (double) (100 * pitcher.getPitcher().getTotalVictorias())/pitcher.getPitcher().getTotalPartidos();

        if (sede.equals("local")) {

            return Puntaje.builder()
                    .puntosEquipo(obtenerPuntajeEquipo(porcentajeVictoriasEquipo,
                            equipo.getEquipo().getTotalPartidosCasa(),
                            equipo.getEquipo().getTotalVictoriasCasa()))
                    .puntosPitcher(obtenerPuntajePitcher(porcentajeVictoriasPitcher,
                            equipo.getEquipo().getTotalPartidosCasa(), equipo.getEquipo().getTotalVictoriasCasa(),
                            pitcher.getPitcher().getPromedioEra()))
                    .puntosSeries(obtenerPuntajeSeries(equipo.getSeries().getPromedioVictoriasPorSerie()))
                    .build();
        }else{

            return Puntaje.builder()
                    .puntosEquipo(obtenerPuntajeEquipo(porcentajeVictoriasEquipo,
                            equipo.getEquipo().getTotalPartidosVisitante(),
                            equipo.getEquipo().getTotalVictoriasVisitante()))
                    .puntosPitcher(obtenerPuntajePitcher(porcentajeVictoriasPitcher,
                            equipo.getEquipo().getTotalPartidosVisitante(),
                            equipo.getEquipo().getTotalVictoriasVisitante(),
                            pitcher.getPitcher().getPromedioEra()))
                    .puntosSeries(obtenerPuntajeSeries(equipo.getSeries().getPromedioVictoriasPorSerie()))
                    .build();
        }
    }

    private double obtenerPuntajeEquipo(double porcentajeVictoriasEquipo, int totalPartidosSede,
            int totalVictoriasSede) {

        double porcentajeVictorias = (double) (100 * totalVictoriasSede)/totalPartidosSede;
        double puntajeVictorias = (10 * (porcentajeVictoriasEquipo)) /100;
        double puntajeVictoriasSede = (20 * porcentajeVictorias )/100;

        return puntajeVictorias + puntajeVictoriasSede;
    }

    private double obtenerPuntajePitcher(double porcentajeVictoriasPitcher, int totalPartidosSede,
            int totalVictoriasSede, double era) {

        double porcentajeVictoriasSedePitcher = (double) (100 * totalVictoriasSede)/totalPartidosSede;
        double puntajeVictoriasPitcher = (5 * porcentajeVictoriasPitcher)/100;
        double puntajeVictoriasPitcherSede = (15 * porcentajeVictoriasSedePitcher)/100;
        double puntajeEra = obtenerPuntajeEra(era);

        return puntajeVictoriasPitcher + puntajeVictoriasPitcherSede + puntajeEra;
    }

    private double obtenerPuntajeSeries(double promedioVictoriasPorSerie) {
        if (promedioVictoriasPorSerie > 2) {
            return 35;
        }else if (promedioVictoriasPorSerie > 1.5) {
            return 25;
        }else if (promedioVictoriasPorSerie > 1) {
            return 17.5;
        }

        return 10;
    }

    private double obtenerPuntajeEra(double era) {

        if (era < 1.0) {
            return 10;
        } else if (era < 2.0) {
            return 8;
        }else if (era < 3.0) {
            return 6;
        }else if (era < 4.0) {
            return 5;
        }else if (era > 5.0 && era < 10.0) {
            return 4;
        }

        return 1;
    }

    private double sumarPuntos(Puntaje puntaje) {
        return puntaje.getPuntosEquipo() + puntaje.getPuntosPitcher() + puntaje.getPuntosSeries();
    }

}
