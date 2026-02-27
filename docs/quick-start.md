# 빠른 시작

## Spring Boot

### 1) 의존성 추가

```gradle
dependencies {
    implementation("io.github.jho951:feature-flag-config:1.0.0")
}
```

### 2) `application.yml` 설정

```yaml
featureflag:
  store: MEMORY      # MEMORY | FILE
  cache-ttl: 3s      # FILE 스토어 캐시 TTL
  file-path: /etc/app/flags.json
```

### 3) 코드 사용

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
        return "B".equals(variant) ? "NEW_CHECKOUT_B" : "NEW_CHECKOUT_A";
    }
}
```

## Pure Java

### InMemory 예시

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

### File Store 예시

```java
var store = new JsonFileFlagStore("/etc/app/flags.json", Duration.ofSeconds(3));
var svc = new FeatureFlagService(store);
```
