package toby.salon.refactoring.chapter1.calculator;

import toby.salon.refactoring.chapter1.dto.Performance;
import toby.salon.refactoring.chapter1.dto.Play;
import toby.salon.refactoring.chapter1.dto.Type;

public class ComedyCalculator implements PerformanceCalculator {

    private final Performance performance;
    private final Play play;

    public ComedyCalculator(Performance performance, Play play) {
        this.performance = performance;
        this.play = play;
    }

    @Override
    public int amount() {
        int result = 30000;
        if (performance.audience() > 20) {
            result += 10000 + 500 * (performance.audience() - 20);
        }
        return result += 300 * performance.audience();
    }

    @Override
    public int volumeCredits() {
        int result = 0;
        result += Math.max(performance.audience() - 30, 0);
        result += Math.floor(performance.audience() / 5);
        return result;
    }
}
