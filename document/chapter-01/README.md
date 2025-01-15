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

```