package com.example.mlbpredictor;

import com.example.mlbpredictor.model.response.EquipoResponse;
import com.example.mlbpredictor.model.PartidoResultados;
import com.example.mlbpredictor.model.Series;
import com.example.mlbpredictor.model.Equipo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.util.*;

public class main {

    public static void main(String[] args) {

        //System.out.println(getHtml("https://www.baseball-reference.com/teams/STL/2022-schedule-scores.shtml"));

        //scrapping();

        //List<PartidoResultados> partidoResultados = obtenerResultadosEquipo();

        //calcularPartidosLocalVisitante(partidoResultados);


        String[] partido = {"35", "Tuesday,", "May", "17", "(1)", "boxscore", "STL", "@", "NYM", "L", "1", "3", "19-16", "2", "2.5", "Reed", "Mikolas", "Díaz", "2:54", "D", "1.18", "-"};

        String[] partido2 = {"5", "Thursday,", "Apr", "14", "boxscore", "STL", "@", "MIL", "L", "1", "5", "3-2", "2", "0.5", "Woodruff", "Wainwright", "3:04", "D", "42","794", "1.10", "-"};

        String[] partido3 = {"149", "Tuesday,", "Sep", "20", "boxscore", "STL", "@", "SDP", "L", "0", "5", "87-62", "1", "up", "8.5", "Clevinger", "Wainwright", "2:56", "N", "39","538", ".04", "--"};

        //getResultadosMap(partido3);

        List<PartidoResultados> partidoResultados = obtenerResultadosEquipo();

        Equipo equipo = calcularPartidosLocalVisitante(partidoResultados);

        EquipoResponse equipoResponse = EquipoResponse.builder()
                .nombre("pathvariable")
                .equipo(equipo)
                .build();

        //obtenerResultadosContraEquipos();

        Series series = obtenerDatosSeries(partidoResultados, equipo.getTotalVictorias(), equipo.getTotalDerrotas());

        System.out.println(series);

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

    public static void obtenerResultadosContraEquipos() {
        Document call = getHtml("https://www.baseball-reference.com/teams/STL/2022-schedule-scores.shtml");

        Elements els = call.getElementById("all_win_loss").select("td");

        Elements res = call.getElementById("all_win_loss").getElementsByClass("data_grid ");

        Elements prueba = call.select("div").get(26).getElementById("all_win_loss").getElementsByClass("hidden");

        Elements prueba2 = call.getElementsByAttribute("hidden");

//        res.forEach( res2 -> {
//            Element table = res2.getElementById("win_loss_3").firstElementChild();
//            System.out.println(table);
//        });

        System.out.println(prueba2);


    }

    public static void scrapping() {
        Document call = getHtml("https://www.baseball-reference.com/teams/STL/2022-schedule-scores.shtml");

        //System.out.println(articulos);

        Elements headers = call.select("th").select("tr");
        for (Element head: headers) {
            String datos = head.attr("data-stat");
            //System.out.println(datos);
        }

        //Elements result = call.select("tbody").select("tr").select("a");

        Elements result = call.select("tbody").select("tr");


        for (Element resultados: result) {

            String enlace = resultados.select("a").text();
            String href = resultados.select("a").attr("href");

            String resultado = resultados.text();

            //String resultado = resultados.attr("href");
            System.out.println(resultado);
            System.out.println(enlace);
            System.out.println(href);
            System.out.println("----------------------------------");

            //break;
        }


    }

    private static Series obtenerDatosSeries(List<PartidoResultados> partidoResultados, int totalVictorias, int totalDerrotas) {

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


    public static List<PartidoResultados> obtenerResultadosEquipo() {

        Document resultadosTeam = getHtml("https://www.baseball-reference.com/teams/STL/2022-schedule-scores.shtml");

        Elements resultados = resultadosTeam.select("tbody").select("tr");

        List<PartidoResultados> listaResultados = new ArrayList<>();
        for (String resultado : resultados.eachText()) {

            String[] partidos = resultado.split(" ");
            //System.out.println(Arrays.toString(partidos));

            if (!partidos[0].equals("Gm#")) {

                Map<String, String> resultadosMap = getResultadosMap(partidos);

                //System.out.println(resultadosMap);

                listaResultados.add(partidoResultadosMapper(resultadosMap));
            }
        }
        System.out.println(listaResultados);
        return listaResultados;
    }

    private static LocalDate formatearFecha(String[] fecha) {

        Map<String, Integer> meses = new HashMap<>();

        meses.put("Jan", 1);
        meses.put("Feb", 2);
        meses.put("Mar", 3);
        meses.put("Apr", 4);
        meses.put("May", 5);
        meses.put("Jun", 6);
        meses.put("Jul", 7);
        meses.put("Aug", 8);
        meses.put("Sep", 9);
        meses.put("Oct", 10);
        meses.put("Nov", 11);
        meses.put("Dec", 12);

        return LocalDate.of(2022, meses.get(fecha[0]), Integer.parseInt(fecha[1]));
    }

    public static Map<String, String> getResultadosMap(String[] partido) {

        Map<String, String> resultadosMap = new HashMap<>();

        //LocalDate fecha = formatearFecha(partido[2], partido[3]);

        //String fecha = (partido[1]+partido[2]+" "+partido[3]).replace(",", " ");

        String fecha = (partido[2]+" "+partido[3]).replace(",", " ");

        resultadosMap.put("numeroJuego", partido[0]);
        resultadosMap.put("fecha", fecha);

        if (partido[5].equals("boxscore") && partido[7].equals("@")) {

            resultadosMap.put("equipoPrincipal", partido[6]);
            resultadosMap.put("equipoOponente", partido[8]);
            resultadosMap.put("sede", "visitante");
            resultadosMap.put("resultado", obtenerResultadoFormato(partido[9]));
            resultadosMap.put("carrerasRealizadas", partido[10]);
            resultadosMap.put("carrerasPermitidas", partido[11]);
            return resultadosMap;
        }

        if (partido[4].equals("boxscore") && partido[6].equals("@")) {
            resultadosMap.put("equipoPrincipal", partido[5]);
            resultadosMap.put("equipoOponente", partido[7]);
            resultadosMap.put("sede", "visitante");
            resultadosMap.put("resultado", obtenerResultadoFormato(partido[8]));
            resultadosMap.put("carrerasRealizadas", partido[9]);
            resultadosMap.put("carrerasPermitidas", partido[10]);

            return resultadosMap;
        }

        if (partido[5].equals("boxscore")) {

            resultadosMap.put("equipoPrincipal", partido[6]);
            resultadosMap.put("equipoOponente", partido[7]);
            resultadosMap.put("sede", "local");
            resultadosMap.put("resultado", obtenerResultadoFormato(partido[8]));
            resultadosMap.put("carrerasRealizadas", partido[9]);
            resultadosMap.put("carrerasPermitidas", partido[10]);
            return resultadosMap;
        }

        resultadosMap.put("equipoPrincipal", partido[5]);
        resultadosMap.put("equipoOponente", partido[6]);
        resultadosMap.put("sede", "local");
        resultadosMap.put("resultado", obtenerResultadoFormato(partido[7]));
        resultadosMap.put("carrerasRealizadas", partido[8]);
        resultadosMap.put("carrerasPermitidas", partido[9]);

        return resultadosMap;

    }

    private static String obtenerResultadoFormato(String resultado) {
        if (resultado.length() > 1) {
            resultado = String.valueOf(resultado.charAt(0));
        }
        return resultado;
    }

    private static PartidoResultados partidoResultadosMapper(Map<String, String> partido) {
        return PartidoResultados.builder()
                .numeroJuego(Integer.parseInt(partido.get("numeroJuego")))
                .fecha(formatearFecha(partido.get("fecha").split(" ")) )
                .equipoPrincipal(partido.get("equipoPrincipal"))
                .equipoOponente(partido.get("equipoOponente"))
                .sede(partido.get("sede"))
                .resultado(partido.get("resultado"))
                .carrerasRealizadas(Integer.parseInt(partido.get("carrerasRealizadas")))
                .carrerasPermitidas(Integer.parseInt(partido.get("carrerasPermitidas")))
                .build();
    }


    private static Equipo calcularPartidosLocalVisitante(List<PartidoResultados> partidoResultados) {

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

        return Equipo.builder()
                .totalVictorias((int)partidosGanados)
                .totalDerrotas((int)partidosPerdidos)
                .totalDerrotasCasa((int)partidosLocalPerdidos)
                .totalDerrotasVisitante((int)partidosVisitantePerdidos)
                .totalVictoriasCasa((int)partidosLocalGanados)
                .totalVictoriasVisitante((int)partidosVisitantesGanados)
                .build();

    }




}
