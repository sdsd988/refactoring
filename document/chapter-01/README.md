# 1. 리팩터링 : 첫번째 예시

---

## 도입

책에서의 리팩토링 설명 방식

예시를 통해 직관적 이해 후 원칙을 통해 구체적으로 파악한다.

책의 예시는 설명을 위해 복잡하지 않게 작성했다.

실제 코드는 복잡할 것으로 가정한다면 리팩토링의 필요성, 중요성은 높다.

## 1.1 자, 시작해보자

---

#### 예시

상황

1. 다양한 연극을 외주로 받아서 공연하는 극단
2. 공연 요청이 들어오면 연극의 장르와 관객 규모를 기초로 비용을 책정
3. 현재 극단은 두 가지 장르(비극, 희극)만 공연
4. 공연료 이외 포인트를 지급하여 다음번 의뢰 시 공연료 할인 가능

* plays.json 

 ``` json
{
  "hamlet": {
    "name": "hamlet",
    "type": "tragedy"
  },
  "as-like": {
    "name": "As You Like It",
    "type": "comedy"
  },
  "othello": {
    "name": "Othello",
    "type": "tragedy"
  }
}
``` 

* invoice.json
```json
[
  {
    "customer":"BigCo",
    "performances": [
      {
        "playID": "hamlet",
        "audience": 55
      },
      {
        "playID": "as-like",
        "audience": 35
      },
      {
        "playID": "othello",
        "audience": 40
      }
    ]
  }
]
```

* 공연료 청구서 출력 코드
```java
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
```
## 1.2 예시 프로그램을 본 소감

예시는 간단해서 이해 가능하지만, 규모가 크다면 이해하기 어려운 요소들이 있다.

코드가 지저분하면, 기계는 이해하고 동작하지만 인간은 그렇지 않다.

리팩토링 통해 버그 발생이 낮고, 수정하기 쉬운 코드로 전환가능 하다.

```
 프로그램이 새로운 기능을 추가하기에 편한 구조가 아니라면, 먼저 기능을 추가하기 쉬운 형태로 리팩터링하고 나서 원하는 기능을 추가한다.
```

새로운 요구 사항의 추가

1. HTML 출력
2. 연극 장르의 추가

변경이 발생하면 코드를 수정해야 하고, 이 떄 리팩터링의 필요성이 대두된다.

## 1.3 리팩터링의 첫 단계

### 리팩터링의 첫 단계, 테스트 코드

- 테스트 코드 수행을 통해 리팩터링 후 코드 안전성을 확보한다.

## 1.4 statement() 함수 쪼개기

switch 문 리팩토링

- 코드를 분석해 얻은 정보 : switch 문에서 한 번의 공연에 대한 요금을 계산한다.
- 함수 추출하기 : amountFor(aPerformance)
  - 주의해야 할 점은 `유효 범위를 벗어나는 변수, 곧바로 사용할 수 없는 변수`를 파악한다.
  - 예시에서는 perf, play(값이 변경되지 않음->매개변수), thisAmount(값이 변경 -> 반환값) 
  - 반환값이 여러개라면, 어떻게 작성?

```java
 private int amountFor(Performance perf, Play play) throws Exception {
        int thisAmount;
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
        return thisAmount;
    }
```

```java
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

            thisAmount = amountFor(perf, play);

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

    private int amountFor(Performance perf, Play play) throws Exception {
        int thisAmount;
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
        return thisAmount;
    }

}
```
- 매개변수, 리턴값의 `이름`을 이해하기 용이하게 리팩토링
```java
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

            thisAmount = amountFor(perf, play);

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

    private int amountFor(Performance aPerformance, Play play) throws Exception {
        int result;
        switch (play.type()) {
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
                throw new Exception((String.format("알 수 없는 장르 : %s", play.type())));
        }
        return result;
    }

}
```
play 변수 제거하기 (매개 변수 줄이기)

- 제거(리팩토링) 이유 : play 값은 반복문마다 변하는 값(aPerformance)부터 생성되기에, 매개변수로 전달 받을 필요가 없다.
- 즉 statement 함수 내부의 지역 변수(play)를 질의 함수로 전환한다.
- 