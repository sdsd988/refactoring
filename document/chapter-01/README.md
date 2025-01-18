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
- 새롭게 얻은 정보는 빠르게 휘발되므로, 그 전에 `즉시 코드에 반영`하는 것이 좋다!
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

제거(리팩토링) 이유 : play 값은 반복문마다 변하는 값(aPerformance)부터 생성되기에, 매개변수로 전달 받을 필요가 없다.

즉 statement 함수 내부의 지역 변수(play)를 질의 함수(playFor)로 전환한다.

이를 통해 지역변수, 함수의 매개변수를 줄일 수 있다.

관리의 포인트를 줄여 수정 시 영향도를 최소화한다.

```java


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
        result.append(String.format("%s: %s (%d석)\n", playFor(perf).name(), usd(thisAmount), perf.audience()));
        totalAmount += thisAmount;
    }
    result.append(String.format("총액: %s\n", usd(totalAmount)));
    result.append(String.format("적립 포인트: %d점\n", volumeCredits));

    return result.toString();
}

private Play playFor(Performance aPerformance) throws Exception {
  return plays.get(aPerformance.playId());
}
```

적립 포인트 계산 코드 추출하기 

```java
   
   
   
    private int volumeCreditsFor(Performance aPerformance) throws Exception {
        int result = 0;
        result += Math.max(aPerformance.audience() - 30, 0);
        if (playFor(aPerformance).type() == Type.COMEDY) result += Math.floor(aPerformance.audience() / 5);
        return result;
    }


    public String statement(Invoice invoice) throws Exception {

      int totalAmount = 0;
      int volumeCredits = 0;

      StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", invoice.customer()));


      for (Performance perf : invoice.performances()) {
        int thisAmount = 0;

        thisAmount = amountFor(perf);

        //포인트를 적립한다.
        volumeCredits += volumeCreditsFor(perf); // 추출한 한수값을 이용해 값을 누적

        //청구 내역 출력
        result.append(String.format("%s: %s (%d석)\n", playFor(perf).name(), usd(thisAmount), perf.audience()));
        totalAmount += thisAmount;
      }
      result.append(String.format("총액: %s\n", usd(totalAmount)));
      result.append(String.format("적립 포인트: %d점\n", volumeCredits));

      return result.toString();
    }
```


format 변수 제거하기

```java
   private String usd(int amount) {
     return   NumberFormat.getCurrencyInstance(Locale.US).format(amount/100);
    }

```

기능을 추출하고(formating), 함수 이름을 구체화(format -> usd)를 통헤 코드 가독성을 높였다.

volumeCredits 변수 제거하기

1. 반복문을 분리한다.
2. 분리한 반복문의 변수 선언을 최상위가 아니라 `함수 근처`로 옮긴다. -> 변수가 어떻게 선언되고, 변화하는 지 이해할 수 있고 리팩토링으로 이어진다.
```java
public String statement(Invoice invoice) throws Exception {

    int totalAmount = 0;
    StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", invoice.customer()));

    for (Performance perf : invoice.performances()) {
        //포인트를 적립한다.
        result.append(String.format("%s: %s (%d석)\n", playFor(perf).name(), usd(amountFor(perf)), perf.audience()));
        totalAmount += amountFor(perf);
    }

    result.append(String.format("총액: %s\n", usd(totalAmount)));
    result.append(String.format("적립 포인트: %d점\n", totalVolumeCredits()));

    return result.toString();
}

private int totalVolumeCredits() throws Exception {
    int volumeCredits = 0;
    for (Performance perf : invoice.performances()) {
        volumeCredits += volumeCreditsFor(perf);
    }
    return volumeCredits;
}
```

리팩토링(반복문 쪼개기)으로 인해 코드 성능이 저하할까? -> 언어는 발전하기에 반복문 증가의 영향은 미미하다.

그럼에도 불구하고, 실제 코드 성능이 저하한다면? -> 그래도 리팩토링으로 얻는 이점이 더 많다! 

성능과 리팩토링 사이의 고민이라면 리팩토링 후 성능 개선하자!

1. 반복문 쪼개기 : 변수 값을 누적시키는 부분을 분리한다.
2. 문장 슬라이드하기 : 변수 초기화 문장을 변수 값 누적 코드 바로 앞으로 옮긴다.
3. 함수 추출하기 : 적립 포인트 계산 부분을 벼도 함수로 추출
4. 변수 인라인하기 : volumeCredits 변수 제거

세분화 하는 것의 장점은, 코드 오류의 지점을 빠르게 파악하고 개선할 수 있기 때문이다.

totalAmount 같은 방식으로 리팩토링
```java
   private int totalAmount() throws Exception {
        int result = 0;
        for (Performance perf : invoice.performances()) {
            result += amountFor(perf);
        }
        return result;
    }

public String statement(Invoice invoice) throws Exception {

  StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", invoice.customer()));

  for (Performance perf : invoice.performances()) {
    //포인트를 적립한다.
    result.append(String.format("%s: %s (%d석)\n", playFor(perf).name(), usd(amountFor(perf)), perf.audience()));
  }

  result.append(String.format("총액: %s\n", usd(totalAmount())));
  result.append(String.format("적립 포인트: %d점\n", totalVolumeCredits()));

  return result.toString();
}
```

## 1.5 중간 점검 : 난무하는 중첩함수

리팩터링 결과 코드는 간결해졌다.

최상위  statement() 함수는 7줄로 간결해지고, 계산 로직을 보조 함수로 빼내며 계산 과정 및 전체 흐름이 이해하기 쉬워졌다.

## 1.6 계산 단계와 포맷팅 단계 분리하기

지금까지는 프로그램의 논리적인 요소를 파악하기 쉽도록 코드의 구조를 보강하는 일에 집중했다.

기능을 추가하기 전에 구조를 개선하는 일이 선행되어야 한다! 왜? 그래야 기능 추가가 쉬워지니까!

이제는 기능을 변경하자! statement() 의 HTML 버전을 만드는 작업

현재 String 버전으로 분리된 계산 코드를 활용하여 HTML 버전을 만들 수 없을까?

다양한 해결책 중 `단계 쪼개기` 적용

첫 단계에서는 statement()에 필요한 데이터를 처리

두번째 단계에서는 앞서 처리한 결과를 string, html 표현

요약하면, 첫 단계는 구조 생성 두번째 단계는 결과 반환이다.

먼저, 함수 추출하기로 뽑아낸다.

```java
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
```

리팩토링의 단계

1. 중간 데이터 단계로 invoice 정보를 옮긴다.
2. 중간 데이터 단계로 performace 정보를 옮긴다.
3. 중간 데이터 단계로 play 정보를 옮긴다.
4. playFor, amountFor,totalAmount, totalVolumeCredits 함수를 옮긴다.
5. 최종적으로, statement 함수는 중간 데이터만 활용하여 수행된다.
```java
 public String statement() throws Exception {
        return renderPlainText(data);
    }
```

1.7 중간 점검 : 두 파일(과 두 단계)로 분리됨

잠시 쉬면서 코드의 상태를 점검해보자, 현재 코드는 두 개의 파일로 구성된다.

Statement.class
```java
package toby.salon.refactoring.chapter1;

import toby.salon.refactoring.chapter1.dto.*;

import java.text.NumberFormat;
import java.util.Locale;

public class Statement {

    private StatementData data;

    public Statement(Invoice invoice, Plays plays) {
        this.data = new StatementData(invoice,plays);
    }

    public String statement() throws Exception {
        return renderPlainText(data);
    }

    private String renderPlainText(StatementData data) throws Exception {
        StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", data.invoice().customer()));

        for (Performance perf : data.invoice().performances()) {
            result.append(String.format("%s: %s (%d석)\n", data.playFor(perf).name(), usd(data.amountFor(perf)), perf.audience()));
        }

        result.append(String.format("총액: %s\n", usd(data.totalAmount())));
        result.append(String.format("적립 포인트: %d점\n", data.totalVolumeCredits()));

        return result.toString();
    }

    private String htmlStatement(Invoice invoice, Plays plays) throws Exception {
        return renderHtml(data);
    }
    private String renderHtml(StatementData data) throws Exception {
        StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", data.invoice().customer()));
        for (Performance performance : data.invoice().performances()) {
            result.append(String.format("%s: $%d %d석\n",data.playFor(performance).name(), data.amountFor(performance) / 100, performance.audience()));
        }

        result.append(String.format("총액: $%d\n", data.totalAmount()));
        result.append(String.format("적립 포인트: %d점", data.totalVolumeCredits()));
        return result.toString();
    }


    private String usd(int amount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount/100);
    }

}
```

StatementData.class
```java
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

```

리팩토링 후 코드의 양이 늘었지만, 코드의 로직이 분명해졌다.

이를 통해 기존 코드의 파악이 빨라지고, 수정이 편리해진다.

수정이 편리해진다는 뜻은 코드의 예로, 코드의 복사 없이 htmlStatement() 코드 추가가 가능하다.

```String
캠핑자들에게는 "도착했을 때보다 깔끔하게 정돈하고 떠난다."는 규칙이 있다. 프로그래밍도 마찬가지다.
항시 코드페이스를 작업 시작 전보다 건강하게 만들어놓고 떠나야 한다.
```
## 1.8 다형성을 활용해 계산 코드 재구성하기

장르에따라, 계산 로직을 분리하는 리팩토링을 진행(`조건부 로직을 다형성으로 바꾸기`)

공연료 계산기 만들기

```Java
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
}
```

함수들을 계산기로 옮기기

```java
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

```

공연료 계산기를 다형성 버전으로 만들기

클래스에 로직을 담았으니 이제 다형성을 지원하게 만들어보자.

PerformanceCalculator.class
```java
package toby.salon.refactoring.chapter1.calculator;

public interface PerformanceCalculator {

    int amount();
    int volumeCredits();
}
```

TragedyCalculator.class
```java
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

```

ComedyCalculator.class
```java
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
```
## 1.9 상태 점검: 다형성을 활용하여 데이터 생성하기

### 다형성을 추가한 결과로 무엇이 달라졌을까

코드의 양이 늘어났다.

연극 장르별 계산 코드를 묶었다. -> 수정 지점이 명확해졌다.

조건부 로직 함수 amountFor(),volumeCredits()를 생성함수 하나로 옮겼다.

record 타입을 활용해서 Javascript와 유사하게 리팩토링

## 1.10 마치며

이번 장에서는 함수 추출하기, 변수 인라인하기 , 함수 옮기기, 조건부로직을 다형성으로 바꾸기등을 통해 리팩터링을 진행

리팩터링의 대부분은 코드가 하는일을 파악하는 일

```string
좋은 코드를 가늠하는 확실한 방법은 '얼마나 수정하기 쉬운가'다.
```

이 책은 코드를 개선하는 방법을 다룬다. 

다양한 기준이 있을 수 있지만, 대부분 사람이 공감할 수 있는 기준은 `수정하기 쉬운 정도`이다.

이번 장의 예시를 통해 배울 수 있는 가장 중요한 것은 `리팩터링하는 리듬이다.`

리팩터링을 효과적으로 하는 핵심은,

단계를 잘게 나눠야 더 빠르게 처리할 수 있고

코드는 절대 깨지지 않으며

이러한 작은 단계들이 모여서 상당히 큰 변화를 이룰 수 있다는 사실을 깨닫는 것이다.




