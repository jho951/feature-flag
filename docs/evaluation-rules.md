# 평가 규칙

`FeatureFlagService#evaluate`는 아래 순서로 플래그를 평가합니다.

## 순서

1. `FLAG_NOT_FOUND`: 스토어에 플래그 정의가 없으면 OFF
2. `FLAG_DISABLED`: `enabled=false`면 OFF
3. `TARGET_DENY`: `denyUserIds` 또는 `denyGroups` 매칭 시 OFF
4. `TARGET_ALLOW`: `allowUserIds` 또는 `allowGroups` 매칭 시 ON
5. `TARGET_MISS`: 타겟팅 조건(`requireAttrsIn`) 미충족 시 OFF
6. `ROLLOUT_OUT`: 롤아웃 버킷 탈락 시 OFF
7. `ROLLOUT_IN`: 롤아웃 통과 시 ON

## 롤아웃 계산

- 입력 기준값은 `userId` 우선, 없으면 `attrs["anonId"]`를 사용합니다.
- 기준값이 없으면 안전하게 OFF 처리합니다.
- 해시는 SHA-256 기반으로 계산되어 동일 입력에 대해 결정론적입니다.

## Variant 계산

- `variants`가 비어 있으면 `defaultVariant`를 사용합니다.
- `variants`가 있으면 각 `weight` 합계를 기준으로 버킷을 나눕니다.
- `weight` 총합이 0 이하이면 `defaultVariant`를 사용합니다.
