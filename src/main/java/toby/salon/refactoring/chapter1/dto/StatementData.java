package toby.salon.refactoring.chapter1.dto;

import java.util.List;

public record StatementData(
        Invoice invoice,
        Plays plays
) {

    public List<Performance> getPerformances() {
        return invoice.performances();
    }

    public Play playFor(Performance performance) {
        return plays.get(performance.playId());
    }
}
