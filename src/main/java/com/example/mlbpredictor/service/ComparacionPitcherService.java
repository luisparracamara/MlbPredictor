package com.example.mlbpredictor.service;

import com.example.mlbpredictor.model.request.ComparacionPitcherRequest;
import com.example.mlbpredictor.model.response.PitcherResponse;

import java.util.List;

public interface ComparacionPitcherService {


    List<PitcherResponse> compararPitchers(ComparacionPitcherRequest comparacionPitcherRequest);

}
