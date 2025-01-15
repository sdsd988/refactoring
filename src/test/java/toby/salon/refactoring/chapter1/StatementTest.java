package toby.salon.refactoring.chapter1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toby.salon.refactoring.chapter1.dto.*;

import java.util.ArrayList;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StatementTest {

    @DisplayName("청구 내역 테스트")
    @Test
    void test() throws Exception {
        //given

        List<Performance> performances = new ArrayList<>();
        performances.add(new Performance("hamlet",55));
        performances.add(new Performance("as-like",35));
        performances.add(new Performance("othello",40));

        Invoice invoice = new Invoice("BigCo", performances);

        Map<String, Play> playMap = new HashMap<>();
        playMap.put("hamlet", new Play("hamlet", Type.TRAGEDY));
        playMap.put("as-like", new Play("As You Like It", Type.COMEDY));
        playMap.put("othello", new Play("Othello", Type.TRAGEDY));
        Plays plays = new Plays(playMap);

        Statement statement = new Statement(invoice,plays);

        String answer = "청구내역 (고객명: BigCo)\n" +
                "hamlet: $650 (55석)\n" +
                "As You Like It: $580 (35석)\n" +
                "Othello: $500 (40석)\n" +
                "총액: $1730\n" +
                "적립 포인트: 47점\n";

        //when
        String result = statement.statement(invoice);
        //then
        assertEquals(answer, result);

    }

}