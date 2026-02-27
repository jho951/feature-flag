# Spring Boot 설정

`config` 모듈은 `FeatureFlagAutoConfiguration`를 통해 `FlagStore`, `FeatureFlagService`, `FeatureFlagClient`를 자동 등록합니다.

## 프로퍼티

접두사: `featureflag`

| 키 | 타입 | 기본값 | 설명 |
| :--- | :--- | :--- | :--- |
| `store` | `MEMORY \| FILE` | `MEMORY` | 사용할 스토어 타입 |
| `file-path` | `String` | 없음 | `store=FILE`일 때 필수 |
| `cache-ttl` | `Duration` | `3s` | 파일 재조회 TTL |

## 빈 등록 규칙

- `store=MEMORY`(기본): `InMemoryFlagStore` 등록
- `store=FILE`: `JsonFileFlagStore(filePath, cacheTtl)` 등록
- 공통: `FeatureFlagService`, `FeatureFlagClient` 등록

## 주의사항

- `store=FILE`인데 `file-path`가 비어 있으면 예외가 발생합니다.
- `cache-ttl=0`이면 파일을 매번 다시 읽습니다.
