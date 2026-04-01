# Contributing

## Development Setup

Requirements:

- JDK 17
- Gradle Wrapper

Run:

```bash
./gradlew clean build
```

## Guidelines

- Keep public APIs small and explicit.
- Preserve module boundaries between `core`, `api`, `store-file`, and `config`.
- Add tests for behavior changes when modifying evaluation rules or storage implementations.
- Update docs when introducing new user-facing configuration or artifact changes.

## Pull Requests

- Describe the user-visible change.
- Include test coverage or explain why tests were not added.
- Note compatibility impact when changing published APIs or Maven coordinates.
