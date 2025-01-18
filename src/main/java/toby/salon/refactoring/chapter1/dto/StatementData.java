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

    public int amountFor(Performance aPerformance) throws Exception {
        int result;

        switch (playFor(aPerformance).type()) {
            case TRAGEDY: //비극
                result = 40000;
                if (aPerformance.audience() > 30) {
                    result += 1000 * (aPerformance.audience() - 30);
                }
                break;
            case COMEDY: // 희극
                result = 30000;
                if (aPerformance.audience() > 20) {
                    result += 10000 + 500 * (aPerformance.audience() - 20);
                }
                result += 300 * aPerformance.audience();
                break;
            default:
                throw new Exception((String.format("알 수 없는 장르 : %s", playFor(aPerformance).type())));
        }
        return result;
    }
}
