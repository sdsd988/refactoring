package toby.salon.refactoring.chapter1;

import toby.salon.refactoring.chapter1.dto.*;

import java.text.NumberFormat;
import java.util.Locale;

public class Statement {

    private Invoice invoice;
    private Plays plays;
    private StatementData data;

    public Statement(StatementData data,Invoice invoice, Plays plays) {
        this.data = data;
        this.invoice = invoice;
        this.plays = plays;
    }

    public String statement() throws Exception {
        return renderPlainText(data);
    }

    private String renderPlainText(StatementData data) throws Exception {
        StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", data.invoice().customer()));

        for (Performance perf : data.invoice().performances()) {
            result.append(String.format("%s: %s (%d석)\n", data.playFor(perf).name(), usd(data.amountFor(perf)), perf.audience()));
        }

        result.append(String.format("총액: %s\n", usd(totalAmount())));
        result.append(String.format("적립 포인트: %d점\n", totalVolumeCredits()));

        return result.toString();
    }

    private int totalAmount() throws Exception {
        int result = 0;
        for (Performance perf : data.invoice().performances()) {
            result += data.amountFor(perf);
        }
        return result;
    }
    private int totalVolumeCredits() throws Exception {
        int volumeCredits = 0;
        for (Performance perf : data.invoice().performances()) {
            volumeCredits += volumeCreditsFor(perf);
        }
        return volumeCredits;
    }
    private int volumeCreditsFor(Performance aPerformance) throws Exception {
        int result = 0;
        result += Math.max(aPerformance.audience() - 30, 0);
        //희극 관객 5명마다 추가 포인트를 제공한다.
        if (data.playFor(aPerformance).type() == Type.COMEDY) result += Math.floor(aPerformance.audience() / 5);
        return result;
    }

    private String usd(int amount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount/100);
    }

}
