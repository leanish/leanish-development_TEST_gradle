# Gradle declarations and security fixture

Synthetic fixture covering cases 2, 3, 4, 5, 6, 8, 9, 10, 11, 12 and 17: literals, a version
catalog, a buildSrc convention, Kotlin DSL, independent security configurations,
a wrapper, pinned Actions, ordinary/strict declared-lower dependencies, and majors
(bump-it and secure-it) whose upgrade needs source adaptation, at apply time or
during review.

Current main still holds the seeds of cases 2, 3, 5, 6, 8, 9, 10, 11, 12 and 17, plus
the security floors that secure-it's #12 added. The security seeds of case 4, C
(OkHttp parent search) and the grgit plugin move were consumed when #12 merged on
2026-10-11. Seed history and re-seeding instructions are kept outside the fixture repositories.

```sh
./gradlew --no-daemon check
```

Use a Gradle daemon on JDK 21 with JDK 25 installed for the main toolchain.
buildSrc uses the daemon JVM. Security inputs are isolated in directSecurity,
transitiveSecurity, okhttpSecurity and floorSecurity; case 10 uses declaredLower.
The two OkHttp baselines were separate seed commits (3078549 and ab614a7).
Tests exercise benign APIs;
no vulnerability is exploited and HttpClient does not perform network I/O.

Case expectations, acceptance steps, probe helpers and validation evidence are kept
outside the fixture repositories for human and release validation. These repositories are test inputs;
do not publish packages. Claude manages GitHub adoption and the run configuration.
This fixture isn't part of regular tool passes: it runs only in release-candidate
passes that opt fixtures in (`SUPPLY_CHAIN_FIXTURES=1`).
The gate's pin is the `uses:` line in `.github/workflows/supply-chain.yml` (its
history is in git); `fixture-repin` moves it after each stable release. The gate uses
the same commit for inventory and comparison. Tools are deployed separately; record
their release/commit for each trial.
