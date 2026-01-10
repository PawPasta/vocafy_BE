# Validation relaxations (test-only)

This file records validations intentionally relaxed for long-lived tokens in test environments.

## Google ID token decoder

File: `src/main/kotlin/com/exe/vocafy_BE/config/SecurityConfig.kt`

Removed validations:
- `exp` (expiration time) check
- `nbf` (not before) check
- `iat` (issued at) check
- Google ID token `aud`/`azp` client ID check

Still enforced:
- Signature verification via Google JWK set
- Issuer check (when `security.oauth2.google.issuer` is set)

Reason:
- Test environment uses a single long-lived token, so time-based claims would fail after expiration.
