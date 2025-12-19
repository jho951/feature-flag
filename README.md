# Feature Flag (v1)

[![Java](https://img.shields.io/badge/java-17%2B-blue)]()
[![Spring Boot](https://img.shields.io/badge/spring--boot-3.x-brightgreen)]()

**Feature Flag**는 애플리케이션을 재배포하지 않고도 기능을 실시간으로 ON/OFF(Kill-switch)하거나, 특정 사용자/그룹 타겟팅, A/B 테스트, 점진 롤아웃(0~100%)을 수행할 수 있게 해주는 Java 공통 모듈입니다.

---

## 🚀 주요 기능

- **배포 없는 기능 제어**: 설정(메모리/파일) 변경으로 즉시 ON/OFF
- **결정론적(Deterministic) 평가**: 동일 사용자는 항상 동일한 Variant에 배정
- **타겟팅**: userId / groups / attrs(Region, Plan 등)
- **점진 롤아웃**: 0~100% 롤아웃 비율
- **Framework Agnostic**: core는 순수 Java

---

## 📦 모듈 구성

| 모듈명 | 설명 |
| :--- | :--- |
| **`core`** | 평가 엔진, 도메인 모델, `FlagStore` 인터페이스 (순수 Java) |
| **`api`** | 앱 코드에서 core 타입 노출을 최소화하는 Facade(`FeatureFlagClient`) |
| **`store-file`** | JSON 파일 기반 `FlagStore` 구현(`JsonFileFlagStore`) |
| **`config`** | Spring Boot Starter. 자동 빈 등록(MEMORY/FILE) |

---

## 🛠 빠른 시작 (Spring Boot)

### 1) 의존성 추가 (Gradle)

```gradle
dependencies {
    implementation("io.github.jho951:feature-flag-config:0.1.0")
}
```

### 2) 설정 (application.yml)

```yml
featureflag:
  store: MEMORY      # MEMORY | FILE
  cache-ttl: 3s      # store=FILE일 때 파일 재조회 TTL
  file-path: /etc/app/flags.json  # store=FILE일 때 필수
```

### 3) 사용 예시

```java
@RestController
public class CheckoutController {
    private final FeatureFlagClient flags;

    public CheckoutController(FeatureFlagClient flags) {
        this.flags = flags;
    }

    @GetMapping("/checkout")
    public String checkout(@RequestHeader("X-User-Id") String userId) {
        var ctx = FlagContext.builder()
                .userId(userId)
                .group("beta")
                .attr("region", "KR")
                .build();

        boolean enabled = flags.isEnabled("checkout.newFlow", ctx);
        String variant = flags.variant("checkout.uiTest", ctx, "A");

        if (!enabled) return "OLD_CHECKOUT";
        return "B".equals(variant) ? "NEW_CHECKOUT_VARIANT_B" : "NEW_CHECKOUT_VARIANT_A";
    }
}
```

---

## 🛠 빠른 시작 (Pure Java)

### InMemory

```java
var store = new InMemoryFlagStore();
store.put(FlagDefinition.builder("checkout.newFlow")
        .enabled(true)
        .rolloutPercent(50)
        .build());

var svc = new FeatureFlagService(store);
var ctx = FlagContext.builder().userId("u-1").build();

boolean enabled = svc.isEnabled("checkout.newFlow", ctx);
```

### JSON File

```java
var store = new JsonFileFlagStore("/etc/app/flags.json", Duration.ofSeconds(3));
var svc = new FeatureFlagService(store);
```

---

## 💾 JSON 포맷

`store-file`은 **2가지 형식**을 지원합니다.

### 1) Map 형태 (추천)

```json
{
  "checkout.newFlow": {
    "enabled": true,
    "rolloutPercent": 20,
    "defaultVariant": "A",
    "targeting": {
      "allowUserIds": ["admin-01"],
      "allowGroups": ["beta-testers"],
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

### 2) List 형태

```json
[
  {
    "key": "checkout.newFlow",
    "enabled": true,
    "rolloutPercent": 20
  }
]
```

---

## ⚙️ 평가 규칙 (Reason)

플래그 평가는 아래 우선순위로 진행됩니다.

- `FLAG_NOT_FOUND`: 정의가 없으면 OFF
- `FLAG_DISABLED`: enabled=false면 OFF
- `TARGET_DENY`: deny(User/Group) 매칭 시 OFF
- `TARGET_ALLOW`: allow(User/Group) 매칭 시 ON (롤아웃 무시)
- `TARGET_MISS`: 속성 조건(requireAttrsIn) 미충족 시 OFF
- `ROLLOUT_OUT`: 롤아웃 버킷 탈락 시 OFF
- `ROLLOUT_IN`: 통과 시 ON (이후 weight로 Variant 결정)

---

## 📒 운영 팁 (v1)

### TTL(캐시) 트레이드오프
- `cache-ttl: 3s`는 일반적으로 괜찮지만, 초고트래픽 서비스라면 3초 동안에도 파일 I/O가 매우 많아질 수 있습니다.

### Flag 청소(Technical Debt)
- 배포 완료 후에도 `if (flags.isEnabled(...))`가 남아 있으면 코드가 지저분해집니다.
- 운영 프로세스로 **배포 완료 후 일정 기간 뒤 플래그/코드 삭제 태스크**를 꼭 포함하세요.

---

## 🗺️ v2 로드맵(계획)

- 고트래픽 대응 **로컬 캐시(Caffeine 등) + 관리자 변경 시 invalidate 전략**
- 플래그 만료/정리 프로세스(예: 배포 완료 후 N일 뒤 삭제)
- `evaluate()` 결과(variant, reason) **로깅/메트릭 연동(ELK/Prometheus 등)**
