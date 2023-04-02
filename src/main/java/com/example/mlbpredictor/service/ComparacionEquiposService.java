package com.example.mlbpredictor.service;

import com.example.mlbpredictor.model.response.EquipoResponse;
import com.example.mlbpredictor.model.request.ComparacionEquiposRequest;

import java.util.List;

public interface ComparacionEquiposService {

    List<EquipoResponse> compararEquipos(ComparacionEquiposRequest equiposRequest);
}
