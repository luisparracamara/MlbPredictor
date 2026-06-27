package com.example.mlbpredictor.model.today;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodayMatches {

    private TodayScoreBoard scoreboard;
}
