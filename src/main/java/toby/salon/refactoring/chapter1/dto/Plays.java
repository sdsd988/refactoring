package toby.salon.refactoring.chapter1.dto;

import java.util.HashMap;
import java.util.Map;

public record
Plays(Map<String, Play> playMap) {
    public Plays {
        playMap = Map.copyOf(playMap);
    }

    public Play get(String playId) {
        return playMap.get(playId);
    }
}
