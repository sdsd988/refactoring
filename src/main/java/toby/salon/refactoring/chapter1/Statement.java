package toby.salon.refactoring.chapter1;

import toby.salon.refactoring.chapter1.dto.*;

import java.text.NumberFormat;
import java.util.Locale;

public class Statement {

    private Invoice invoice;
    private Plays plays;

    public Statement(Invoice invoice, Plays plays) {
        this.invoice = invoice;
        this.plays = plays;
    }


    public String statement(Invoice invoice) throws Exception {
        return renderPlainText(invoice);
    }

    private String renderPlainText(Invoice invoice) throws Exception {
        StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", invoice.customer()));

        for (Performance perf : invoice.performances()) {
            result.append(String.format("%s: %s (%d석)\n", playFor(perf).name(), usd(amountFor(perf)), perf.audience()));
        }

        result.append(String.format("총액: %s\n", usd(totalAmount())));
        result.append(String.format("적립 포인트: %d점\n", totalVolumeCredits()));

        return result.toString();
    }

    private int totalAmount() throws Exception {
        int result = 0;
        for (Performance perf : invoice.performances()) {
            result += amountFor(perf);
        }
        return result;
    }
    private int totalVolumeCredits() throws Exception {
        int volumeCredits = 0;
        for (Performance perf : invoice.performances()) {
            volumeCredits += volumeCreditsFor(perf);
        }
        return volumeCredits;
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
    private String usd(int amount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount/100);
    }

}
