package com.example.mlbpredictor.model.realtime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchDetails {

    private TeamData home_team_data;
    private TeamData away_team_data;
    private ScoreBoard scoreboard;

}
