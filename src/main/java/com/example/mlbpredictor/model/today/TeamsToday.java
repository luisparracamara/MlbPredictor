package com.example.mlbpredictor.model.today;

import com.example.mlbpredictor.model.realtime.TeamData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamsToday {

    private TeamData home;
    private TeamData away;

}
