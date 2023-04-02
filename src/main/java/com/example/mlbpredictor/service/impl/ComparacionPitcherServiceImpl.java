package com.example.mlbpredictor.service.impl;

import com.example.mlbpredictor.client.BaseballReference;
import com.example.mlbpredictor.model.Pitcher;
import com.example.mlbpredictor.model.PitcherResultados;
import com.example.mlbpredictor.model.request.ComparacionPitcherRequest;
import com.example.mlbpredictor.model.response.PitcherResponse;
import com.example.mlbpredictor.properties.MlbProperties;
import com.example.mlbpredictor.service.ComparacionPitcherService;
import com.example.mlbpredictor.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ComparacionPitcherServiceImpl implements ComparacionPitcherService {

    private final BaseballReference baseballReference;

    private final Utils utils;

    private final MlbProperties mlbProperties;

    public ComparacionPitcherServiceImpl(BaseballReference baseballReference, Utils utils, MlbProperties mlbProperties) {
        this.baseballReference = baseballReference;
        this.utils = utils;
        this.mlbProperties = mlbProperties;
    }

    @Override
    public List<PitcherResponse> compararPitchers(ComparacionPitcherRequest comparacionPitcherRequest) {

        Document callPitcherLocal = baseballReference
                .getHtml("https://www.baseball-reference.com/players/gl.fcgi?id="
                        +comparacionPitcherRequest.getPitcherLocal()+"&t=p&year="+mlbProperties.getSeason());
        Document callPitcherVisitante = baseballReference
                .getHtml("https://www.baseball-reference.com/players/gl.fcgi?id="
                        +comparacionPitcherRequest.getPitcherVisitante()+"&t=p&year="+mlbProperties.getSeason());

        return Arrays.asList(responsePitcher(callPitcherLocal), responsePitcher(callPitcherVisitante));
    }

    private PitcherResponse responsePitcher(Document pitcherData) {

        List<PitcherResultados> resultadosPitcher = obtenerResultadosPitcher(pitcherData);
        Pitcher pitcher= obtenerPitcherData(resultadosPitcher);

        return PitcherResponse.builder()
                .nombre(resultadosPitcher.get(0).getNombre())
                .equipo(resultadosPitcher.get(0).getEquipoPrincipal())
                .pitcher(pitcher)
                .pitcherResultados(resultadosPitcher)
                .build();

    }


    private List<PitcherResultados> obtenerResultadosPitcher(Document call) {

        Elements pitcherData = call.select("table").select("tbody").select("tr");

        String nombrePitcher = call.select("h1").select("span").text();

        List<PitcherResultados> resultados = new ArrayList<>();

        pitcherData.eachText().forEach( game -> {

            String[] pitcherResultados = game.split(" ");

            if (!pitcherResultados[0].equals("May") && !pitcherResultados[0].equals("Jun")
                    && !pitcherResultados[0].equals("Jul") && !pitcherResultados[0].equals("Aug")
                    && !pitcherResultados[0].equals("Sep/Oct") && !pitcherResultados[0].equals("Sept")
                    && !pitcherResultados[0].equalsIgnoreCase("player")) {

                //System.out.println(Arrays.asList(pitcherResultados));

                Map<String, String> pitcherMap = crearPitcherResultados(nombrePitcher, pitcherResultados);
                resultados.add(partidosResultadosMapper(pitcherMap));

                LocalDate fechaLimite = LocalDate.of(mlbProperties.getSeason(), mlbProperties.getLimitMonth(),
                        mlbProperties.getLimitDay());

                    if (resultados.get(resultados.size()-1).getFecha().isAfter(fechaLimite) && !resultados.isEmpty()){
                        resultados.remove(resultados.size()-1);
                    }
            }
        });

        return resultados;
    }


    private Map<String, String> crearPitcherResultados(String nombrePitcher, String[] pitcherResultados) {

        Map<String, String> resultados = new HashMap<>();

        resultados.put("numeroJuego", pitcherResultados[0]);
        resultados.put("numeroJuegoTemporadaEquipo", pitcherResultados[2]);
        resultados.put("nombre", nombrePitcher);
        resultados.put("fecha", pitcherResultados[3]+" "+pitcherResultados[4]);
        resultados.put("equipoPrincipal", pitcherResultados[5]);

        if (pitcherResultados[6].equals("@")) {

            if (pitcherResultados[8].charAt(0) == 'L' || pitcherResultados[8].charAt(0) == 'W') {

                //solo el pitcherResultados[11] ip cambia de posicion de ahi en adelante
                if (StringUtils.isNumeric(pitcherResultados[10])) {
                    return crearResultadosMap(resultados, pitcherResultados[7], "visitante",
                            String.valueOf(pitcherResultados[8].charAt(0)), pitcherResultados[8].substring(2),
                            pitcherResultados[11], pitcherResultados[12], pitcherResultados[13], pitcherResultados[15],
                            pitcherResultados[16], pitcherResultados[17], pitcherResultados[19], pitcherResultados[31]);
                }
                return crearResultadosMap(resultados, pitcherResultados[7], "visitante",
                        String.valueOf(pitcherResultados[8].charAt(0)), pitcherResultados[8].substring(2),
                        pitcherResultados[12], pitcherResultados[13], pitcherResultados[14], pitcherResultados[16],
                        pitcherResultados[17], pitcherResultados[18], pitcherResultados[20], pitcherResultados[32]);

            }else{
                return crearResultadosMap(resultados, pitcherResultados[7], "visitante",
                        String.valueOf(pitcherResultados[8].charAt(0)), pitcherResultados[8].substring(2),
                        pitcherResultados[11], pitcherResultados[12], pitcherResultados[13], pitcherResultados[15],
                        pitcherResultados[16], pitcherResultados[17], pitcherResultados[19], pitcherResultados[31]);
            }

        }else{

            if (pitcherResultados[7].charAt(0) == 'L' || pitcherResultados[7].charAt(0) == 'W') {

                //si el dr es numerico
                if (StringUtils.isNumeric(pitcherResultados[9])) {
                    return crearResultadosMap(resultados, pitcherResultados[6], "local",
                            String.valueOf(pitcherResultados[7].charAt(0)), pitcherResultados[7].substring(2),
                            pitcherResultados[10], pitcherResultados[11], pitcherResultados[12], pitcherResultados[14],
                            pitcherResultados[15], pitcherResultados[16], pitcherResultados[18], pitcherResultados[30]);
                }
                    return crearResultadosMap(resultados, pitcherResultados[6], "local",
                        String.valueOf(pitcherResultados[7].charAt(0)), pitcherResultados[7].substring(2),
                        pitcherResultados[11], pitcherResultados[12], pitcherResultados[13], pitcherResultados[15],
                        pitcherResultados[16], pitcherResultados[17], pitcherResultados[19], pitcherResultados[31]);
            }else{
                return crearResultadosMap(resultados, pitcherResultados[6], "local",
                        String.valueOf(pitcherResultados[7].charAt(0)), pitcherResultados[7].substring(2),
                        pitcherResultados[10], pitcherResultados[11], pitcherResultados[12], pitcherResultados[14],
                        pitcherResultados[15], pitcherResultados[16], pitcherResultados[18], pitcherResultados[30]);
            }
        }

    }

    private Map<String, String> crearResultadosMap(
            Map<String, String> resultados, String equipoRival, String sede, String resultado, String marcador,
            String ip, String hits, String carrerasPermitidas, String basePorBola, String ponches,
            String homeRunsPermitidos, String era, String califiacionnGsc) {

        resultados.put("equipoRival", equipoRival);
        resultados.put("sede", sede);
        resultados.put("resultado", resultado);
        resultados.put("marcador", marcador);
        resultados.put("ip", ip);
        resultados.put("hits", hits);
        resultados.put("carrerasPermitidas", carrerasPermitidas);
        resultados.put("basePorBola", basePorBola);
        resultados.put("ponches", ponches);
        resultados.put("homeRunsPermitidos", homeRunsPermitidos);
        resultados.put("era", era);
        resultados.put("califiacionnGsc", califiacionnGsc);

        return resultados;
    }

    private PitcherResultados partidosResultadosMapper(Map<String, String> pitcherMap) {
        return PitcherResultados.builder()
                .numeroJuego(Integer.parseInt(pitcherMap.get("numeroJuego")))
                .numeroJuegoTemporadaEquipo(Integer.parseInt(pitcherMap.get("numeroJuegoTemporadaEquipo")))
                .nombre(pitcherMap.get("nombre"))
                .fecha(utils.formatearFecha(pitcherMap.get("fecha").split(" ")))
                .equipoPrincipal(pitcherMap.get("equipoPrincipal"))
                .equipoRival(pitcherMap.get("equipoRival"))
                .sede(pitcherMap.get("sede"))
                .resultado(pitcherMap.get("resultado"))
                .marcador(pitcherMap.get("marcador"))
                .ip(Double.parseDouble(pitcherMap.get("ip")))
                .hits(Integer.parseInt(pitcherMap.get("hits")))
                .carrerasPermitidas(Integer.parseInt(pitcherMap.get("carrerasPermitidas")))
                .basePorBola(Integer.parseInt(pitcherMap.get("basePorBola")))
                .ponches(Integer.parseInt(pitcherMap.get("ponches")))
                .homeRunsPermitidos(Integer.parseInt(pitcherMap.get("homeRunsPermitidos")))
                .era(Double.parseDouble(pitcherMap.get("era")))
                .calificacionGsc(Integer.parseInt(pitcherMap.get("califiacionnGsc")))
                .build();
    }

    private Pitcher obtenerPitcherData(List<PitcherResultados> pitcherResultados) {

        long partidosGanados = pitcherResultados.stream()
                .filter(resultado -> resultado.getResultado().equals("W"))
                .count();

        long partidosPerdidos = pitcherResultados.stream()
                .filter(resultado ->  resultado.getResultado().equals("L"))
                .count();

        long partidosLocalGanados = pitcherResultados.stream()
                .filter(resultado -> resultado.getSede().equals("local") && resultado.getResultado().equals("W"))
                .count();

        long partidosLocalPerdidos = pitcherResultados.stream()
                .filter(resultado -> resultado.getSede().equals("local") && resultado.getResultado().equals("L"))
                .count();

        long partidosVisitantesGanados = pitcherResultados.stream()
                .filter(resultado -> resultado.getSede().equals("visitante") && resultado.getResultado().equals("W"))
                .count();

        long partidosVisitantePerdidos = pitcherResultados.stream()
                .filter(resultado -> resultado.getSede().equals("visitante") && resultado.getResultado().equals("L"))
                .count();

        double totalIp = pitcherResultados.stream().mapToDouble(PitcherResultados::getIp).sum();

        int totalHitsPermitidos = pitcherResultados.stream().mapToInt(PitcherResultados::getHits).sum();

        int totalCarrerasPermitidas = pitcherResultados.stream()
                .mapToInt(PitcherResultados::getCarrerasPermitidas).sum();

        int totalBasePorBola = pitcherResultados.stream().mapToInt(PitcherResultados::getBasePorBola).sum();

        int totalPonches = pitcherResultados.stream().mapToInt(PitcherResultados::getPonches).sum();

        int totalHomeRunsPermitidos = pitcherResultados.stream()
                .mapToInt(PitcherResultados::getHomeRunsPermitidos).sum();

        double promedioEra = pitcherResultados.get(pitcherResultados.size()-1).getEra();

        double promedioCalifiacionnGsc = pitcherResultados.stream()
                .collect(Collectors.averagingDouble(PitcherResultados::getCalificacionGsc));

        double promedioHitsPorPartido = pitcherResultados.stream()
                .collect(Collectors.averagingDouble(PitcherResultados::getHits));

        double promedioCarrerasPorPartido = pitcherResultados.stream()
                .collect(Collectors.averagingDouble(PitcherResultados::getCarrerasPermitidas));

        double promedioBasesPorBolaPorPartido = pitcherResultados.stream()
                .collect(Collectors.averagingDouble(PitcherResultados::getBasePorBola));

        double promedioPonchesPorPartido = pitcherResultados.stream()
                .collect(Collectors.averagingDouble(PitcherResultados::getPonches));

        double promedioHomeRunsPorPartido = pitcherResultados.stream()
                .collect(Collectors.averagingDouble(PitcherResultados::getHomeRunsPermitidos));

        double promedioIpPorPartido = totalIp/(partidosGanados+partidosPerdidos);

        return Pitcher.builder()
                .totalPartidos((int) (partidosGanados+partidosPerdidos))
                .totalPartidosCasa((int) (partidosLocalGanados+partidosLocalPerdidos))
                .totalPartidosVisitante((int) (partidosVisitantesGanados+partidosVisitantePerdidos))
                .totalVictorias((int) (partidosLocalGanados+partidosVisitantesGanados))
                .totalDerrotas((int) (partidosLocalPerdidos+partidosVisitantePerdidos))
                .totalDerrotasCasa((int) partidosLocalPerdidos)
                .totalDerrotasVisitante((int) partidosVisitantePerdidos)
                .totalVictoriasCasa((int) partidosLocalGanados)
                .totalVictoriasVisitante((int) partidosVisitantesGanados)
                .totalIp(totalIp)
                .totalHitsPermitidos(totalHitsPermitidos)
                .totalCarrerasPermitidas(totalCarrerasPermitidas)
                .totalBasePorBola(totalBasePorBola)
                .totalPonches(totalPonches)
                .totalHomeRunsPermitidos(totalHomeRunsPermitidos)
                .promedioEra(promedioEra)
                .promedioCalificacionGsc((int) promedioCalifiacionnGsc)
                .promedioHitsPorPartido(promedioHitsPorPartido)
                .promedioCarrerasPorPartido(promedioCarrerasPorPartido)
                .promedioBasesPorBolaPorPartido(promedioBasesPorBolaPorPartido)
                .promedioPonchesPorPartido(promedioPonchesPorPartido)
                .promedioHomeRunsPorPartido(promedioHomeRunsPorPartido)
                .promedioIpPorPartido(promedioIpPorPartido)
                .build();
    }



}
