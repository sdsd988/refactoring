package toby.salon.refactoring.chapter1.dto;


import toby.salon.refactoring.chapter1.calculator.ComedyCalculator;
import toby.salon.refactoring.chapter1.calculator.PerformanceCalculator;
import toby.salon.refactoring.chapter1.calculator.TragedyCalculator;

public record StatementData(
        Invoice invoice,
        Plays plays
) {

    public Play playFor(Performance performance) {
        return plays.get(performance.playId());
    }

    private PerformanceCalculator getPerformanceCalculator(Performance aPerformance,Play aPlay) throws Exception {
        switch (aPlay.type()) {
            case TRAGEDY -> {
                return new TragedyCalculator(aPerformance, aPlay);
            }
            case COMEDY -> {
                return new ComedyCalculator(aPerformance, aPlay);
            }
            default -> {
                throw new Exception((String.format("알 수 없는 장르 : %s", aPlay.type())));
            }
        }
    }

    public int amountFor(Performance aPerformance) throws Exception {
        return getPerformanceCalculator(aPerformance, playFor(aPerformance)).amount();
    }

    public int volumeCreditsFor(Performance aPerformance) throws Exception {
      return  getPerformanceCalculator(aPerformance, playFor(aPerformance)).volumeCredits();
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
