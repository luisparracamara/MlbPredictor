package com.example.mlbpredictor.client;

import com.example.mlbpredictor.model.realtime.MatchDetails;
import com.example.mlbpredictor.model.today.TodayMatches;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "savant-client", url = "https://baseballsavant.mlb.com/", configuration = FeignConfig.class)
public interface MatchesFeignService {

    @GetMapping("gf")
    MatchDetails getMatchDetails(@RequestParam("game_pk") int id, @RequestHeader("user-agent") String agent);

    @GetMapping("gf")
    TodayMatches getMatchDetailsToday(@RequestParam("game_pk") int id, @RequestHeader("user-agent") String agent);

}
