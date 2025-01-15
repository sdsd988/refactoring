package toby.salon.refactoring.chapter1;

import toby.salon.refactoring.chapter1.dto.*;

public class Statement {

    private Invoice invoice;
    private Plays plays;

    public Statement(Invoice invoice, Plays plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    public String statement(Invoice invoice, Plays plays) throws Exception {

        int totalAmount = 0;
        int volumeCredits = 0;

        StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", invoice.customer()));

        for (Performance perf : invoice.performances()) {
            Play play = plays.get(perf.playId());
            int thisAmount = 0;

            switch (play.type()) {
                case TRAGEDY: //비극
                    thisAmount = 40000;
                    if (perf.audience() > 30) {
                        thisAmount += 1000 * (perf.audience() - 30);
                    }
                    break;
                case COMEDY: // 희극
                    thisAmount = 30000;
                    if (perf.audience() > 20) {
                        thisAmount += 10000 + 500 * (perf.audience() - 20);
                    }
                    thisAmount += 300 * perf.audience();
                    break;
                default:
                    throw new Exception((String.format("알 수 없는 장르 : %s", play.type())));
            }

            //포인트를 적립한다.
            volumeCredits += Math.max(perf.audience() - 30, 0);

            //희극 관객 5명마다 추가 포인트를 제공한다.
            if (play.type() == Type.COMEDY) volumeCredits += Math.floor(perf.audience() / 5);

            //청구 내역 출력
            result.append(String.format("%s: $%d (%d석)\n", play.name(), thisAmount / 100, perf.audience()));
            totalAmount += thisAmount;
        }
        result.append(String.format("총액: $%d\n", totalAmount / 100));
        result.append(String.format("적립 포인트: %d점\n", volumeCredits));

        return result.toString();
    }

}
