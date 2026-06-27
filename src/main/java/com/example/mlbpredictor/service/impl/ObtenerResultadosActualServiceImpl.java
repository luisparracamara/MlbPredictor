package com.example.mlbpredictor.service.impl;

import com.example.mlbpredictor.client.BaseballReference;
import com.example.mlbpredictor.client.MatchesFeignService;
import com.example.mlbpredictor.model.realtime.MatchDetails;
import com.example.mlbpredictor.model.realtime.RealTimeInfoTeams;
import com.example.mlbpredictor.model.request.HistoricalResultsRequest;
import com.example.mlbpredictor.model.request.PrediccionRequest;
import com.example.mlbpredictor.model.response.GetAllResults;
import com.example.mlbpredictor.model.response.GetResults;
import com.example.mlbpredictor.model.response.OfficialResult;
import com.example.mlbpredictor.model.response.PrediccionResponse;
import com.example.mlbpredictor.model.today.TodayMatches;
import com.example.mlbpredictor.model.today.TodayScoreBoard;
import com.example.mlbpredictor.service.ObtenerResultadosActualService;
import com.example.mlbpredictor.service.PredecirResultadoService;
import com.example.mlbpredictor.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.*;

@Service
@Slf4j
public class ObtenerResultadosActualServiceImpl implements ObtenerResultadosActualService {

    private final BaseballReference baseballReference;

    private final PredecirResultadoService predecirResultadoService;

    private final MatchesFeignService matchesFeignService;

    private final Utils utils;

    public ObtenerResultadosActualServiceImpl(BaseballReference baseballReference, PredecirResultadoService predecirResultadoService, MatchesFeignService matchesFeignService, Utils utils) {
        this.baseballReference = baseballReference;
        this.predecirResultadoService = predecirResultadoService;
        this.matchesFeignService = matchesFeignService;
        this.utils = utils;
    }


    @Override
    public List<PrediccionResponse> getTodayMatches() {
        Document partidos = baseballReference.getHtml("https://www.baseball-reference.com/previews/");

        Elements listPartidos = partidos.select("div")
                .attr("class", "game_summary nohover").select("table").select("td").select("a");

        List<String> equipos = new ArrayList<>();
        List<String> pitchers = new ArrayList<>();

        System.out.println(listPartidos);

        listPartidos.eachAttr("href").forEach(partido -> {

            if (partido.startsWith("/teams")) {
                equipos.add(partido.substring(7, 10));
            }

            if (partido.startsWith("https://www.baseball-reference.com/players/")) {
                String[] pitchersArray = partido.split("/");

                String pitcherId = pitchersArray[pitchersArray.length - 1];
                String[] pitcherArray = pitcherId.split("\\.");
                pitchers.add(pitcherArray[0]);
            }
        });


        List<PrediccionResponse> response = new ArrayList<>();
        for (int i = 0; i < equipos.size() - 1; i += 2) {
            try {
                String visitTeam = equipos.get(i);
                String visitPitcher = pitchers.get(i);
                String homeTeam = equipos.get(i + 1);
                String homePitcher = pitchers.get(i + 1);

                PrediccionRequest prediccionRequest = PrediccionRequest.builder()
                        .equipoLocal(homeTeam)
                        .equipoVisitante(visitTeam)
                        .pitcherLocal(homePitcher)
                        .pitcherVisitante(visitPitcher)
                        //.date(LocalDate.now())
                        .build();

                Thread.sleep(8000);
                response.add(predecirResultadoService.predecirResultado(prediccionRequest));

            } catch (Exception e) {
                log.error("ERROR EN CALCULO DE PARTIDO DE HOY: " + e);
            }
        }

        return response;
    }

    @Override
    public GetResults getHistoricalResults(HistoricalResultsRequest request) {
        LocalDate date = utils.getDateToSearch(request.getLimitDate());
        String zeroFormatMonth = String.format("%02d", date.getMonthValue());
        String zeroFormatDay = String.format("%02d", date.getDayOfMonth());
        List<RealTimeInfoTeams> realTimeInfoTeams = getInfoFromSavant(zeroFormatDay, zeroFormatMonth, date.getYear());
        List<GetAllResults> results = new ArrayList<>();

        //análisis de todos los días antes de la fecha buscada
        date = date.minusDays(1);

        for (int i = 0; i < realTimeInfoTeams.size(); i++)  {
            try {

                RealTimeInfoTeams realTimeInfoTeam = realTimeInfoTeams.get(i);
                //mapper
                PrediccionRequest prediccionRequest = PrediccionRequest.builder()
                        .pitcherLocal(realTimeInfoTeam.getPitcherHome())
                        .pitcherVisitante(realTimeInfoTeam.getPitcherAway())
                        .equipoLocal(realTimeInfoTeam.getTeamHome())
                        .equipoVisitante(realTimeInfoTeam.getTeamAway())
                        .date(date)
                        .build();

                GetAllResults getAllResults = new GetAllResults();

                // Espera 3 segundos antes de la próxima iteración
                Thread.sleep(8000);

                PrediccionResponse prediccionResponse = predecirResultadoService.predecirResultado(prediccionRequest);

                OfficialResult officialResult = OfficialResult.builder()
                        .winnerTeam(realTimeInfoTeams.get(i).getWinnerTeam())
                        .loserTeam(realTimeInfoTeams.get(i).getLoserTeam())
                        .fullResult(realTimeInfoTeams.get(i).getWinnerTeam()+" "+realTimeInfoTeams.get(i).getWinnerRuns()
                                +"-"+realTimeInfoTeams.get(i).getLoserTeam()+" "+realTimeInfoTeams.get(i).getLoserRuns())
                        .build();

                getAllResults.setOfficialResult(officialResult);
                getAllResults.setResultForecast(prediccionResponse);
                results.add(getAllResults);
            } catch (Exception e) {
                RealTimeInfoTeams game =  realTimeInfoTeams.get(i);
                log.error("[ERROR AL MOMENTO DE HACER EL PROCESO DE CALCULO DE RESULTADOS] {} GAME {}", e.getMessage(),
                        game.getTeamHome() + " VS "+game.getTeamAway());
            }
        }

        return GetResults.builder().results(results).build();
    }

    public List<RealTimeInfoTeams> getInfoFromSavant(String day, String month, int year) {
        //https://baseballsavant.mlb.com/gf?game_pk=745488
        //https://baseballsavant.mlb.com/scoreboard?date=2024-06-18

        //Document matchByDate = baseballReference.getHtml("https://www.mlb.com/scores/2024-06-18");
        Document matchByDate = baseballReference.getHtml("https://www.mlb.com/scores/"+year+"-"+month+"-"+day);
        Elements matchesLink = matchByDate.select("a.linkstyle__AnchorElement-sc-5g3tf0-0.fSWEIu.getProductButtons__ButtonLink-sc-bgnczd-1.elIcfn.trk-button_watch");

        List<RealTimeInfoTeams> partidosFinalResult = new ArrayList<>();

        matchesLink.eachAttr("href").forEach(partido -> {
            String[] ids = partido.split("/");
            int matchId = Integer.parseInt(ids[4].substring(1));
            MatchDetails matchDetails = matchesFeignService.getMatchDetails(matchId, "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Mobile Safari/537.36");

            RealTimeInfoTeams timeInfoTeams = RealTimeInfoTeams.builder()
                    .teamHome(matchDetails.getHome_team_data().getAbbreviation())
                    .teamAway(matchDetails.getAway_team_data().getAbbreviation())
                    .pitcherHome(createPitcherId(matchDetails.getScoreboard().getProbablePitchers().getHome().getFullName()))
                    .pitcherAway(createPitcherId(matchDetails.getScoreboard().getProbablePitchers().getAway().getFullName()))
                    .id(matchId)
                    .build();

            if (matchDetails.getScoreboard().getLinescore().getTeams().getHome().getRuns() >
                    matchDetails.getScoreboard().getLinescore().getTeams().getAway().getRuns()) {
                timeInfoTeams.setWinnerTeam(matchDetails.getHome_team_data().getAbbreviation());
                timeInfoTeams.setLoserTeam(matchDetails.getAway_team_data().getAbbreviation());
                timeInfoTeams.setWinnerRuns(matchDetails.getScoreboard().getLinescore().getTeams().getHome().getRuns());
                timeInfoTeams.setLoserRuns(matchDetails.getScoreboard().getLinescore().getTeams().getAway().getRuns());
            } else {
                timeInfoTeams.setLoserTeam(matchDetails.getHome_team_data().getAbbreviation());
                timeInfoTeams.setWinnerTeam(matchDetails.getAway_team_data().getAbbreviation());
                timeInfoTeams.setWinnerRuns(matchDetails.getScoreboard().getLinescore().getTeams().getAway().getRuns());
                timeInfoTeams.setLoserRuns(matchDetails.getScoreboard().getLinescore().getTeams().getHome().getRuns());
            }

            partidosFinalResult.add(timeInfoTeams);

        });

        return partidosFinalResult;
    }

    private String createPitcherId(String fullName) {
        String normalizedText = Normalizer.normalize(fullName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String[] names = normalizedText.split(" ");

        String lastName = "";
        if (names[1].length() >= 5) {
            lastName = names[1].substring(0,5).toLowerCase(Locale.ROOT);
        } else {
            lastName = names[1].toLowerCase(Locale.ROOT);
        }
        String name = names[0].substring(0,2).toLowerCase(Locale.ROOT);

        return lastName+name+"01";
    }

    public List<PrediccionResponse> getTodayPrediction() {
        List<PrediccionResponse> response = new ArrayList<>();
        LocalDate date = LocalDate.now();
        String zeroFormatMonth = String.format("%02d", date.getMonthValue());
        String zeroFormatDay = String.format("%02d", date.getDayOfMonth());
        Document matchByDate = baseballReference.getHtml("https://www.mlb.com/scores/"+ date.getYear()+"-"+ zeroFormatMonth+"-"+zeroFormatDay);
        Elements matchesLink = matchByDate.attr("class","linkstyle__AnchorElement-sc-5g3tf0-0 fSWEIu getProductButtons__ButtonLink-sc-bgnczd-1 elIcfn trk-preview ")
                .getElementsByClass("linkstyle__AnchorElement-sc-5g3tf0-0 fSWEIu getProductButtons__ButtonLink-sc-bgnczd-1 elIcfn trk-preview ");

        List<PrediccionRequest> prediccionRequests = new ArrayList<>();
        matchesLink.eachAttr("href").forEach(partido -> {
            String[] ids = partido.split("/");
            int matchId = Integer.parseInt(ids[2]);
            TodayMatches todayMatches = matchesFeignService.getMatchDetailsToday(matchId, "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Mobile Safari/537.36");
            Optional<TodayMatches> optionalTodayMatches = Optional.ofNullable(todayMatches);
            if (optionalTodayMatches.isPresent()) {
                TodayMatches match = optionalTodayMatches.get();
                if (match.getScoreboard().getProbablePitchers().getHome() != null && match.getScoreboard().getProbablePitchers().getAway() != null) {
                    PrediccionRequest prediccionRequest = PrediccionRequest.builder()
                            .pitcherLocal(createPitcherId(match.getScoreboard().getProbablePitchers().getHome().getFullName()))
                            .pitcherVisitante(createPitcherId(match.getScoreboard().getProbablePitchers().getAway().getFullName()))
                            .equipoLocal(match.getScoreboard().getTeams().getHome().getAbbreviation())
                            .equipoVisitante(match.getScoreboard().getTeams().getAway().getAbbreviation())
                            .build();
                    prediccionRequests.add(prediccionRequest);
                }
            }
        });


        for (int i = 0; i < prediccionRequests.size(); i++) {
            try {
                Thread.sleep(8000);
                PrediccionRequest request = prediccionRequests.get(i);
                PrediccionResponse prediccionResponse = predecirResultadoService.predecirResultado(request);
                response.add(prediccionResponse);
            } catch (Exception e) {
                PrediccionRequest request = prediccionRequests.get(i);
                log.error("[ERROR AL MOMENTO DE HACER EL PROCESO DE CALCULO DE RESULTADOS] {} GAME {}", e.getMessage(),
                        request.getEquipoLocal() + " VS "+ request.getEquipoVisitante());
            }
        }





        return response;
    }

}


