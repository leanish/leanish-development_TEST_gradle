# Gradle declarations and security fixture

Synthetic fixture covering cases 2, 3, 4, 5, 6, 8, 9 and 10: literals, a version
catalog, a buildSrc convention, Kotlin DSL, independent security configurations,
a wrapper, pinned Actions and ordinary/strict declared-lower dependencies.

```sh
./gradlew --no-daemon check
```

Use a Gradle daemon on JDK 21 with JDK 25 installed for the main toolchain.
buildSrc uses the daemon JVM. Security inputs are isolated in directSecurity,
transitiveSecurity, okhttpSecurity and floorSecurity; case 10 uses declaredLower.
The two OkHttp baselines are separate seed commits, as documented in EXPECTED.md. Tests exercise benign APIs;
no vulnerability is exploited and HttpClient does not perform network I/O.

For the independent case-10 probes, run the commands in
[EXPECTED.md](EXPECTED.md#case-10-reproducible-probes). They prepare local copies
and collect real inventories, plans, checks and verifier/gate reports.

[EXPECTED.md](EXPECTED.md) is the single source of case expectations, runnable
acceptance steps, timing caveats and VERIFY items. [VALIDATION.md](VALIDATION.md)
records checks already performed. These private repositories are test inputs;
do not publish packages. Claude manages GitHub adoption and weekly configuration.
Gate adoption uses the same supply-chain commit for inventory and comparison;
see EXPECTED.md for the prerelease pin and released v0.3.0 acceptance.
