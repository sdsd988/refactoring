package toby.salon.refactoring.chapter1.calculator;

import toby.salon.refactoring.chapter1.dto.Performance;
import toby.salon.refactoring.chapter1.dto.Play;

public class TragedyCalculator implements PerformanceCalculator {

    private final Performance performance;
    private final Play play;

    public TragedyCalculator(Performance performance, Play play) {
        this.performance = performance;
        this.play = play;
    }

    @Override
    public int amount() {
        int result = 40000;
        if (performance.audience() > 30) {
            result += 1000 * (performance.audience() - 30);
        }
        return result;
    }

    @Override
    public int volumeCredits() {
        int result = 0;
        return result += Math.max(performance.audience() - 30, 0);
    }
}
