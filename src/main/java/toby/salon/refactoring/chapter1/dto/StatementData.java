package toby.salon.refactoring.chapter1.dto;


import toby.salon.refactoring.chapter1.calculator.PerformanceCalculator;

public record StatementData(
        Invoice invoice,
        Plays plays
) {

    public Play playFor(Performance performance) {
        return plays.get(performance.playId());
    }

    public int amountFor(Performance aPerformance) throws Exception {
        return new PerformanceCalculator(aPerformance, playFor(aPerformance)).amount();
    }

    public int volumeCreditsFor(Performance aPerformance) throws Exception {
        int result = 0;
        result += Math.max(aPerformance.audience() - 30, 0);
        //희극 관객 5명마다 추가 포인트를 제공한다.
        if (playFor(aPerformance).type() == Type.COMEDY) result += Math.floor(aPerformance.audience() / 5);
        return result;
    }

    public int totalVolumeCredits() throws Exception {
        return invoice.performances().stream().mapToInt(perf -> {
            try {
                return volumeCreditsFor(perf);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).sum();
    }

    public int totalAmount() throws Exception {
        return invoice().performances()
                .stream()
                .mapToInt(perf -> {
                    try {
                        return amountFor(perf);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .sum();
    }
}
