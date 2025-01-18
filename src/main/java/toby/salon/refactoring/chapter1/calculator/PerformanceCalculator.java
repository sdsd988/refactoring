package toby.salon.refactoring.chapter1.calculator;

import toby.salon.refactoring.chapter1.dto.Performance;
import toby.salon.refactoring.chapter1.dto.Play;
import toby.salon.refactoring.chapter1.dto.Type;

import static toby.salon.refactoring.chapter1.dto.Type.COMEDY;
import static toby.salon.refactoring.chapter1.dto.Type.TRAGEDY;

public class PerformanceCalculator {

    private final Performance performance;
    private final Play play;

    public PerformanceCalculator(Performance performance, Play play) {
        this.performance = performance;
        this.play = play;
    }

    public int amount() throws Exception {
        int result;
        switch (play.type()) {
            case TRAGEDY: //비극
                result = 40000;
                if (performance.audience() > 30) {
                    result += 1000 * (performance.audience() - 30);
                }
                break;
            case COMEDY: // 희극
                result = 30000;
                if (performance.audience() > 20) {
                    result += 10000 + 500 * (performance.audience() - 20);
                }
                result += 300 * performance.audience();
                break;
            default:
                throw new Exception((String.format("알 수 없는 장르 : %s", play.type())));
        }
        return result;
    }

    public int volumeCredits() {
        int result = 0;
        result += Math.max(performance.audience() - 30, 0);
        //희극 관객 5명마다 추가 포인트를 제공한다.
        if (play.type() == Type.COMEDY) result += Math.floor(performance.audience() / 5);
        return result;
    }
}
