# JSON 스토어 포맷

`store-file` 모듈의 `JsonFlagSerde`는 두 가지 JSON 포맷을 지원합니다.

## 1) Map 형태 (권장)

```json
{
  "checkout.newFlow": {
    "enabled": true,
    "rolloutPercent": 20,
    "defaultVariant": "A",
    "targeting": {
      "allowUserIds": ["admin-01"],
      "denyUserIds": ["blocked-01"],
      "allowGroups": ["beta-testers"],
      "denyGroups": ["suspended"],
      "requireAttrsIn": {
        "region": ["KR", "JP"],
        "plan": ["PRO"]
      }
    },
    "variants": [
      { "name": "A", "weight": 50 },
      { "name": "B", "weight": 50 }
    ]
  }
}
```

## 2) List 형태

```json
[
  {
    "key": "checkout.newFlow",
    "enabled": true,
    "rolloutPercent": 20,
    "defaultVariant": "A"
  }
]
```

## 필드 요약

- `key`: 플래그 키 (List 형태에서 필수, Map 형태에서는 생략 가능)
- `enabled`: 플래그 활성 여부 (기본값 `true`)
- `rolloutPercent`: 0~100 범위로 자동 보정
- `defaultVariant`: variant가 없거나 계산 실패 시 기본값 (`on`)
- `targeting`: allow/deny/속성 조건
- `variants`: `{name, weight}` 배열

## 파싱 실패 시 동작

- 파일이 없거나 JSON 파싱에 실패하면 빈 맵으로 처리됩니다.
- 운영 환경에서는 파싱 실패 로깅 및 이전 캐시 유지 전략을 별도로 적용하는 것을 권장합니다.
