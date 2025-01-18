package toby.salon.refactoring.chapter1.dto;


public record StatementData(
        Invoice invoice,
        Plays plays
) {

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

    public int volumeCreditsFor(Performance aPerformance) throws Exception {
        int result = 0;
        result += Math.max(aPerformance.audience() - 30, 0);
        //희극 관객 5명마다 추가 포인트를 제공한다.
        if (playFor(aPerformance).type() == Type.COMEDY) result += Math.floor(aPerformance.audience() / 5);
        return result;
    }

    public int totalVolumeCredits() throws Exception {
        int volumeCredits = 0;
        for (Performance perf : invoice().performances()) {
            volumeCredits += volumeCreditsFor(perf);
        }
        return volumeCredits;
    }

    public int totalAmount() throws Exception {
        int result = 0;
        for (Performance perf : invoice().performances()) {
            result += amountFor(perf);
        }
        return result;
    }
}
