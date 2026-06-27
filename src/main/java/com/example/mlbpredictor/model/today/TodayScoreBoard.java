package com.example.mlbpredictor.model.today;

import com.example.mlbpredictor.model.realtime.ProbablePitchers;
import com.example.mlbpredictor.model.realtime.TeamData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodayScoreBoard {

    private ProbablePitchers probablePitchers;
    private TeamsToday teams;

}
