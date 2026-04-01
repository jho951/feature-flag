# 아키텍처 설계

## 설계 전략

이 프로젝트는 `설정 프로퍼티 기반 분기`와 `기능 공개 정책 평가`를 분리하는 방향으로 설계하며, 외부 저장소와 실행 정책을 플러그인처럼 교체할 수 있게 합니다.

- `pluginpolicyengine.*`: 라이브러리 부트스트랩과 인프라 설정
- `features.*`: 실제 공개 여부를 판단하는 기능 키

핵심 전략은 다음과 같습니다.

1. `FeatureFlagClient`는 `evaluate`를 중심 API로 둡니다.
2. `isEnabled`, `variant`는 `evaluate` 위의 편의 API로 유지합니다.
3. HTTP 진입점에서는 `@FeatureGate`로 공개 여부 판단을 표준화합니다.
4. 요청 컨텍스트 구성은 `FeatureFlagContextResolver`로 외부 확장 가능하게 둡니다.
5. 접근 거부 응답은 `FeatureDeniedHandler`로 분리해 조직별 정책을 끼워 넣을 수 있게 합니다.

## 책임 전략

라이브러리는 공통 메커니즘만 책임지고, 도메인 정책은 애플리케이션에 남깁니다.

### 라이브러리 책임

- 플래그 정의 조회 및 평가
- 평가 결과 표준화 (`FlagDecision`)
- 스프링 자동구성
- HTTP 진입점 게이팅 (`@FeatureGate`, `FeatureGateInterceptor`)
- 컨텍스트 해석 확장 포인트 제공
- 거부 응답 확장 포인트 제공

### 애플리케이션 책임

- 어떤 기능 키를 어떤 엔드포인트에 연결할지 결정
- 인증/인가 체계에서 어떤 값을 `FlagContext`에 넣을지 결정
- 접근 거부 시 `404`, `403`, JSON 에러 바디 중 어떤 정책을 쓸지 결정
- 기능 비활성 상태에서의 비즈니스 fallback 결정

## 계층 구조

### `core`

- 순수 Java 평가 엔진
- `FlagDefinition`, `FlagContext`, `FlagDecision`, `FeatureFlagService`

### `api`

- 애플리케이션이 의존하는 얇은 facade
- `FeatureFlagClient`

### `config`

- Spring Boot 자동구성
- 정책 엔진과 HTTP 게이트 연결
- 기본 구현과 확장 포인트 제공

## 요청 처리 흐름

1. `FeatureGateInterceptor`가 `@FeatureGate` 선언을 찾습니다.
2. `FeatureFlagContextResolver`가 `HttpServletRequest`를 `FlagContext`로 변환합니다.
3. `FeatureAccessPolicyEngine`이 기능 키를 평가합니다.
4. 허용이면 요청을 계속 진행합니다.
5. 거부이면 `FeatureDeniedHandler`가 응답 정책을 수행합니다.

## 왜 빈 등록보다 요청 게이트인가

`@ConditionalOnProperty`나 조건부 빈 등록은 애플리케이션 시작 시점에만 결정됩니다. 반면 이 프로젝트의 목적은 배포 후 운영 중에 정책 결정을 바꾸는 것입니다. 따라서 공개 여부는 빈 생성 시점이 아니라 요청 처리 시점에 판단하는 구조가 더 적합합니다.
