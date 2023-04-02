package com.example.mlbpredictor;

import com.example.mlbpredictor.model.Pitcher;
import com.example.mlbpredictor.model.PitcherResultados;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class pitcherFunc {

    public static void main(String[] args) {


        //pitcherData();

        String[] results = {"10", "17", "60", "Jun", "13", "SFG", "KCR", "W,6-2", "7-7", "H(1)", "18", "1.0", "0", "0", "0", "0", "0", "0", "0", "5.19", "4.73", "3", "9", "6", "1", "0", "2", "1", "1", "0", "0", "0", "0", "0", "0", "0", "3", "0", "0", "0", "0", "0", "0", "1.26", "0.095", "1.37", "0.06%", "0.49", "2.25", "3.00", "7t", "---", "0", "out", "a1", "7t", "3", "out", "a1"};

        //String[] prueba = {"5", "307", "13", "Apr", 20, LAA, @, HOU, W,6-0, 7-8, 4, 2.0, 0, 0, 0, 0, 2, 0, 0, 2.57, 4.97, 6, 22, 15, 3, 4, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, .11, 0.017, .13, 0.01%, 0.96, 8.50, 12.00, 7b, ---, 0, out, a6, 8b, 3, out, a6};

        System.out.println(createPitcherResultados("Mauricio Llovera",results));

    }

    private static void pitcherData() {
        Document call = getHtml("https://www.baseball-reference.com/players/gl.fcgi?id=valdefr01&t=p&year=2022");
        Elements headers = call.select("table").select("tbody").select("tr");

        String nombrePitcher = call.select("h1").select("span").text();

        List<PitcherResultados> resultados = new ArrayList<>();

        headers.eachText().forEach( game -> {

            String[] pitcherResultados = game.split(" ");

            if (!pitcherResultados[0].equals("May") && !pitcherResultados[0].equals("Jun") && !pitcherResultados[0].equals("Jul") &&
                    !pitcherResultados[0].equals("Aug") && !pitcherResultados[0].equals("Sep/Oct")) {

                Map<String, String> pitcherMap = createPitcherResultados(nombrePitcher, pitcherResultados);
                resultados.add(partidosResultadosMapper(pitcherMap));

            }

        });

        Pitcher pitcher = getPitcherData(resultados);
        System.out.println(pitcher);

        //System.out.println(resultados);

//        for (Element resultado : headers) {
//
//            System.out.println(resultado.text());
//
//            //System.out.println(resultado.select("td").select("a").attr("href"));
//
//        }
    }

    private static Map<String, String> createPitcherResultados(String nombrePitcher, String[] pitcherResultados) {

        Map<String, String> resultados = new HashMap<>();

        resultados.put("numeroJuego", pitcherResultados[0]);
        resultados.put("nombre", nombrePitcher);
        resultados.put("fecha", pitcherResultados[3]+" "+pitcherResultados[4]);
        resultados.put("equipoPrincipal", pitcherResultados[5]);

        if (pitcherResultados[6].equals("@")) {

            resultados.put("equipoRival", pitcherResultados[7]);
            resultados.put("sede", "visitante");
            resultados.put("resultado", String.valueOf(pitcherResultados[8].charAt(0)));
            resultados.put("marcador", pitcherResultados[8].substring(2));

            if (pitcherResultados[10].startsWith("L") || pitcherResultados[10].startsWith("W")) {
                resultados.put("ip", pitcherResultados[12]);
                resultados.put("hits", pitcherResultados[13]);
                resultados.put("carrerasPermitidas", pitcherResultados[14]);
                resultados.put("basePorBola", pitcherResultados[16]);
                resultados.put("ponches", pitcherResultados[17]);
                resultados.put("homeRunsPermitidos", pitcherResultados[18]);
                resultados.put("era", pitcherResultados[20]);
                resultados.put("califiacionnGsc", pitcherResultados[32]);
            }else{
                resultados.put("ip", pitcherResultados[11]);
                resultados.put("hits", pitcherResultados[12]);
                resultados.put("carrerasPermitidas", pitcherResultados[13]);
                resultados.put("basePorBola", pitcherResultados[15]);
                resultados.put("ponches", pitcherResultados[16]);
                resultados.put("homeRunsPermitidos", pitcherResultados[17]);
                resultados.put("era", pitcherResultados[19]);
                resultados.put("califiacionnGsc", pitcherResultados[31]);
            }

        }else{

            resultados.put("equipoRival", pitcherResultados[6]);
            resultados.put("sede", "local");
            resultados.put("resultado", String.valueOf(pitcherResultados[7].charAt(0)));
            resultados.put("marcador", pitcherResultados[7].substring(2));

            System.out.println(pitcherResultados[9]);

            if (pitcherResultados[9].startsWith("L") || pitcherResultados[9].startsWith("W")) {
                resultados.put("ip", pitcherResultados[11]);
                resultados.put("hits", pitcherResultados[12]);
                resultados.put("carrerasPermitidas", pitcherResultados[13]);
                resultados.put("basePorBola", pitcherResultados[15]);
                resultados.put("ponches", pitcherResultados[16]);
                resultados.put("homeRunsPermitidos", pitcherResultados[17]);
                resultados.put("era", pitcherResultados[19]);
                resultados.put("califiacionnGsc", pitcherResultados[31]);
            }else{
//                System.out.println("aki");
//                System.out.println(resultados);

                if (!StringUtils.isNumeric(pitcherResultados[9])){
                    System.out.println("aki");
                    resultados.put("ip", pitcherResultados[11]);
                    resultados.put("hits", pitcherResultados[12]);
                    resultados.put("carrerasPermitidas", pitcherResultados[13]);
                    resultados.put("basePorBola", pitcherResultados[15]);
                    resultados.put("ponches", pitcherResultados[16]);
                    resultados.put("homeRunsPermitidos", pitcherResultados[17]);
                    resultados.put("era", pitcherResultados[19]);
                    resultados.put("califiacionnGsc", pitcherResultados[31]);
                }else{
                    resultados.put("ip", pitcherResultados[10]);
                    resultados.put("hits", pitcherResultados[11]);
                    resultados.put("carrerasPermitidas", pitcherResultados[12]);
                    resultados.put("basePorBola", pitcherResultados[14]);
                    resultados.put("ponches", pitcherResultados[15]);
                    resultados.put("homeRunsPermitidos", pitcherResultados[16]);
                    resultados.put("era", pitcherResultados[18]);
                    resultados.put("califiacionnGsc", pitcherResultados[30]);
                }

            }

        }

        return resultados;
    }

    private static PitcherResultados partidosResultadosMapper(Map<String, String> pitcherMap) {

        System.out.println(pitcherMap);

        PitcherResultados pitcherResultados = PitcherResultados.builder()
                .numeroJuego(Integer.parseInt(pitcherMap.get("numeroJuego")))
                .nombre(pitcherMap.get("nombre"))
                .fecha(LocalDate.now())
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

        return pitcherResultados;
    }

    private static Pitcher getPitcherData(List<PitcherResultados> pitcherResultados) {

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

//        pitcherResultados.forEach( pitcherResultados1 -> {
//            System.out.println(pitcherResultados1.getIp());
//        });

        double totalIp = pitcherResultados.stream()
                .collect(Collectors.summingDouble( (pitcher) -> pitcher.getIp() ));

        int totalHitsPermitidos = pitcherResultados.stream()
                .collect(Collectors.summingInt( (pitcher) -> pitcher.getHits() ));

        int totalCarrerasPermitidas = pitcherResultados.stream()
                .collect(Collectors.summingInt( (pitcher) -> pitcher.getCarrerasPermitidas() ));

        int totalBasePorBola = pitcherResultados.stream()
                .collect(Collectors.summingInt( (pitcher) -> pitcher.getBasePorBola() ));

        int totalPonches = pitcherResultados.stream()
                .collect(Collectors.summingInt( (pitcher) -> pitcher.getPonches() ));

        int totalHomeRunsPermitidos = pitcherResultados.stream()
                .collect(Collectors.summingInt( (pitcher) -> pitcher.getHomeRunsPermitidos() ));

        double promedioEra = pitcherResultados.get(pitcherResultados.size()-1).getEra();

        double promedioCalifiacionnGsc = pitcherResultados.stream()
                .collect(Collectors.averagingDouble( (pitcher) -> pitcher.getCalificacionGsc() ));

        double promedioHitsPorPartido = pitcherResultados.stream()
                .collect(Collectors.averagingDouble( (pitcher) -> pitcher.getHits() ));

        double promedioCarrerasPorPartido = pitcherResultados.stream()
                .collect(Collectors.averagingDouble( (pitcher) -> pitcher.getCarrerasPermitidas() ));

        double promedioBasesPorBolaPorPartido = pitcherResultados.stream()
                .collect(Collectors.averagingDouble( (pitcher) -> pitcher.getBasePorBola() ));

        double promedioPonchesPorPartido = pitcherResultados.stream()
                .collect(Collectors.averagingDouble( (pitcher) -> pitcher.getPonches() ));

        double promedioHomeRunsPorPartido = pitcherResultados.stream()
                .collect(Collectors.averagingDouble( (pitcher) -> pitcher.getHomeRunsPermitidos() ));

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
                .build();
    }


    public static Document getHtml(String url) {
        Document html = null;

        try {
            html = Jsoup.connect(url).get();

        }catch (Exception e) {
            System.out.println("error al obtener codigo html");
        }

        return html;
    }
}
