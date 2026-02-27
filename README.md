# Feature Flag

[![Java](https://img.shields.io/badge/java-17%2B-blue)]()
[![Spring Boot](https://img.shields.io/badge/spring--boot-3.x-brightgreen)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)

애플리케이션 재배포 없이 기능 ON/OFF, 타겟팅, 점진 롤아웃, A/B 테스트를 수행하는 Java 기반 Feature Flag 모듈입니다.

## Docs

- [문서 인덱스](./docs/README.md)
- [개요 및 모듈 구조](./docs/overview.md)
- [빠른 시작](./docs/quick-start.md)
- [Spring Boot 설정](./docs/spring-config.md)
- [평가 규칙](./docs/evaluation-rules.md)
- [JSON 스토어 포맷](./docs/json-format.md)

## Modules

- `core`: 평가 엔진, 도메인 모델, `FlagStore` 인터페이스
- `api`: 앱에서 사용하는 최소 API (`FeatureFlagClient`)
- `store-file`: JSON 파일 기반 `FlagStore` 구현
- `config`: Spring Boot 자동 설정(starter)

## License

이 프로젝트는 [MIT License](./LICENSE)를 따릅니다.
