package toby.salon.refactoring.chapter1;

import toby.salon.refactoring.chapter1.dto.*;

public class Statement {

    private Invoice invoice;
    private Plays plays;

    public Statement(Invoice invoice, Plays plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    public String statement(Invoice invoice) throws Exception {

        int totalAmount = 0;
        int volumeCredits = 0;

        StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", invoice.customer()));

        for (Performance perf : invoice.performances()) {
            int thisAmount = 0;

            thisAmount = amountFor(perf);

            //포인트를 적립한다.
            volumeCredits += volumeCreditsFor(perf);

            //청구 내역 출력
            result.append(String.format("%s: $%d (%d석)\n", playFor(perf).name(), thisAmount / 100, perf.audience()));
            totalAmount += thisAmount;
        }
        result.append(String.format("총액: $%d\n", totalAmount / 100));
        result.append(String.format("적립 포인트: %d점\n", volumeCredits));

        return result.toString();
    }

    private int volumeCreditsFor(Performance aPerformance) throws Exception {
        int result = 0;
        result += Math.max(aPerformance.audience() - 30, 0);
        //희극 관객 5명마다 추가 포인트를 제공한다.
        if (playFor(aPerformance).type() == Type.COMEDY) result += Math.floor(aPerformance.audience() / 5);
        return result;
    }

    private int amountFor(Performance aPerformance) throws Exception {
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

    private Play playFor(Performance aPerformance) throws Exception {
        return plays.get(aPerformance.playId());
    }

}
