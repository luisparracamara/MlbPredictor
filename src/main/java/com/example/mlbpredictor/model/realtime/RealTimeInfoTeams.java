package com.example.mlbpredictor.model.realtime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealTimeInfoTeams {

    private String teamAway;
    private String teamHome;
    private String urlMatch;
    private String pitcherAway;
    private String pitcherHome;
    private int id;
    private String winnerTeam;
    private int winnerRuns;
    private String loserTeam;
    private int loserRuns;

    // Override toString() to produce JSON-like output
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
