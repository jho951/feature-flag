# 개요 및 모듈 구조

## 프로젝트 목적

`feature-flag`는 기능 배포와 활성화를 분리하기 위한 라이브러리입니다.

- 배포 없이 기능 ON/OFF
- 사용자/그룹/속성 기반 타겟팅
- 결정론적 롤아웃 및 A/B 분기

## 모듈 구성

| 모듈 | 설명 |
| :--- | :--- |
| `core` | 순수 Java 평가 엔진, 도메인 모델, `FlagStore` 인터페이스 |
| `api` | 애플리케이션에서 사용할 Facade 인터페이스 (`FeatureFlagClient`) |
| `store-file` | JSON 파일을 읽는 `FlagStore` 구현체 (`JsonFileFlagStore`) |
| `config` | Spring Boot 자동 설정(`AutoConfiguration`) |

## 핵심 타입

- `FlagContext`: 평가 입력 컨텍스트 (`userId`, `groups`, `attrs`)
- `FlagDefinition`: 플래그 정의 (`enabled`, `rolloutPercent`, `targeting`, `variants`)
- `Targeting`: allow/deny 및 속성 조건
- `FeatureFlagService`: 최종 평가 엔진
- `FlagDecision`: 평가 결과 (`enabled`, `variant`, `reason`, `meta`)

## 동작 흐름

1. `FlagStore`에서 키로 `FlagDefinition` 조회
2. 플래그 상태(enabled) 확인
3. deny/allow 타겟팅 우선 적용
4. eligibility(속성/허용목록) 확인
5. 롤아웃 버킷 계산
6. variant 가중치 계산
