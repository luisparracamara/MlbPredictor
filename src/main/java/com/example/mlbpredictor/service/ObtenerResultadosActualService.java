package com.example.mlbpredictor.service;

import com.example.mlbpredictor.model.request.HistoricalResultsRequest;
import com.example.mlbpredictor.model.request.PrediccionRequest;
import com.example.mlbpredictor.model.response.GetResults;
import com.example.mlbpredictor.model.response.PrediccionResponse;

import java.util.List;

public interface ObtenerResultadosActualService {

    List<PrediccionResponse> getTodayMatches();

    GetResults getHistoricalResults(HistoricalResultsRequest request);

    List<PrediccionResponse> getTodayPrediction();

}
