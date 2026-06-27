package com.example.mlbpredictor.model.realtime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProbablePitchers {

    private PitcherData home;
    private PitcherData away;
}
