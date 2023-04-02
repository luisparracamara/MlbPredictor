package com.example.mlbpredictor.service;

import com.example.mlbpredictor.model.request.PrediccionRequest;
import com.example.mlbpredictor.model.response.PrediccionResponse;

public interface PredecirResultadoService {

    PrediccionResponse predecirResultado(PrediccionRequest prediccionRequest);

}
