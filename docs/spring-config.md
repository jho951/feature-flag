# Spring Boot 설정

`config` 모듈은 `FeatureFlagAutoConfiguration`를 통해 `FlagStore`, `FeatureFlagService`, `FeatureFlagClient`와 웹 게이트 관련 컴포넌트를 자동 등록합니다.

## 프로퍼티

접두사: `pluginpolicyengine`

| 키 | 타입 | 기본값 | 설명 |
| :--- | :--- | :--- | :--- |
| `store` | `MEMORY \| FILE` | `MEMORY` | 사용할 스토어 타입 |
| `file-path` | `String` | 없음 | `store=FILE`일 때 필수 |
| `cache-ttl` | `Duration` | `3s` | 파일 재조회 TTL |

## 빈 등록 규칙

- `store=MEMORY`(기본): `InMemoryFlagStore` 등록
- `store=FILE`: `JsonFileFlagStore(filePath, cacheTtl)` 등록
- 공통: `FeatureFlagService`, `FeatureFlagClient` 등록
- 공통: `FeatureAccessPolicyEngine`, `FeatureFlagContextResolver`, `FeatureDeniedHandler` 등록
- Servlet 웹 환경: `FeatureGateInterceptor`, `FeatureGateWebMvcConfigurer` 등록

## 웹 게이트 확장 포인트

- `@FeatureGate("features.public-user-api")`: HTTP 진입점 공개 여부를 판단하는 표준 어노테이션
- `FeatureFlagContextResolver`: `HttpServletRequest`를 `FlagContext`로 변환
- `FeatureDeniedHandler`: 접근 거부 시 응답 정책 처리
- `FeatureAccessPolicyEngine`: 공개 여부를 최종 평가하는 정책 엔진

기본 `FeatureDeniedHandler`는 `404 Not Found`를 반환합니다. 조직별 정책이 다르면 동일 타입 빈을 직접 등록해 교체하면 됩니다.

## 주의사항

- `store=FILE`인데 `file-path`가 비어 있으면 예외가 발생합니다.
- `cache-ttl=0`이면 파일을 매번 다시 읽습니다.
