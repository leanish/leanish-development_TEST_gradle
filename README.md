# Gradle declarations and security fixture

Covers cases **2, 3, 4, 5, 6, 8, 9, 10**: literal and catalog dependencies, a precompiled
buildSrc convention, plugin-added Kotlin dependencies and `kotlin("stdlib")`,
direct/transitive vulnerabilities, a wrapper bump, and aged Actions pins.

```sh
./gradlew --no-daemon check
```

Install JDK 25 for the main build toolchain. Gradle may run on JDK 21 or 25;
buildSrc uses the daemon JVM rather than imposing a Java 25 toolchain. Tests
exercise Commons Codec and the Commons IO API supplied by buildSrc. Security dependencies live in two independent resolvable configurations:
`directSecurity` resolves Commons Text 1.9, and `transitiveSecurity` resolves
Commons Configuration 2.11.0 with Commons Text 1.12.0 and Commons Lang 3.14.0.
Separate graphs prevent the latter safe Text from upgrading away the former
vulnerable direct Text. `check` resolves both graphs, without exploiting a CVE.

secure-it repairs the direct Text declaration and adds explicit Commons Lang
security-floor dependencies in every affected fixture configuration, with
`because(...)` and `.github/dependency-floors.json`. A plain Gradle constraint is
not a valid recorded floor in this tool version. There is initially no floor.
The secure-it plan does not upgrade a clean direct parent to fix its transitive;
it needs a floor while the parent stays pinned. A later bump-it parent upgrade
may make that floor redundant, allowing a separate secure-it floor-removal PR
after an unlocked proof. Do not run the parent bump first and then expect the
same vulnerable baseline.

The convention dependency is declared in a `.gradle.kts` file, which routine
verification permits editing. A declaration in an arbitrary `.kt` file would be
source-indexed but its routine edit is a documented unsupported case; this fixture
uses the supported precompiled-script form. Explicitly versioning the buildSrc
Kotlin DSL plugin provides a planned plugin move in that build, needed to verify
its unnamed injected dependencies when they move. Cross-version Gradle/Kotlin
DSL compatibility must be confirmed before adoption.

Case 10 is isolated in `declaredLower`: A=HttpClient 4.5.13 requests
B=HttpCore 4.4.13, while the explicit B declaration is 4.4.10. Normal resolution
should select 4.4.13; bump-it should still plan from the declared 4.4.10.
`check` resolves this third graph. Its declarations are in the applied
`declared-lower.gradle.kts`, a supported dependency-edit file.

```sh
./gradlew --no-daemon dependencyInsight --configuration declaredLower --dependency httpcore
./gradlew --no-daemon -Pfixture.strictLower=true dependencyInsight --configuration declaredLower --dependency httpcore
./gradlew --no-daemon -Pfixture.strictLower=true resolveDeclaredLower
```

The opt-in strict variant replaces B's ordinary declaration with strictly 4.4.10
in the SAME configuration; it should force 4.4.10 because A's plain transitive
request yields to the direct strict version. It does not create a permanently
broken resolvable configuration in the weekly inventory. Conflicting hard
constraints can fail resolution, but none is deliberately added here. Confirm
this behavior on 9.3.1; see the new VERIFY items in EXPECTED.md. No network I/O
through HttpClient is executed by these checks.

These repositories are synthetic test inputs, not packages to publish. The baseline
contains deliberately old dependencies. The security fixtures should make `scan`
red while their build/tests stay green. On PRs, `compare` permits inherited findings
and fails on newly introduced findings; a green PR does not prove `main` is clean.

Claude owns GitHub creation/adoption and the weekly tool configuration. Use private
repos under `leanish`, default branch `main`, with no Dependabot update PRs. Require
`supply-chain / supply-chain`, `supply-chain / cooldown`, and the build/test check
when the account plan supports required checks on private repositories. Keep the
daily gate scan enabled; the macOS weekly tools are separate from this workflow.

The gate is pinned to the supply-chain v0.2.2 release commit
`a8c3f1010bc321ab3c45603eb5600ff9dda7ff3a` (`v0.2.2`), like the other adopters.

Before the first live run, resolve every `VERIFY:` in EXPECTED.md, capture a gate
inventory/report, then preview candidates as the adoption guide describes. Run
secure-it first on the vulnerable baseline, capture its plan/diff/report without
merging, then bump-it against that same baseline. Each tool plans from `main`, not
from the other's PR. Record exact versions chosen, findings fixed/inherited,
notes, failures, cooldown and CI results; CLI exit 0 alone is not acceptance.
Merge/rebase only after collecting that evidence, then run review and scan again.

Repeated weeks on an unchanged baseline should produce `already-open` and review
existing PRs. After merging fixes, those moves disappear; a new PR every week is
not expected. Retain the initial baseline revision externally for repeat trials.
If re-seeding, use a deliberate ordinary commit or a fresh test repo; the gate
should reject reintroducing vulnerabilities. Do not force-push or weaken policy.

No release is fabricated to test age. The expected baseline fixes are historically
aged; newest routine/major targets depend on registry time and the current advisory
snapshot. bump-it must select only eligible aged targets. If a future security fix
is younger than 7 days, expect a draft security-cooldown PR, `supply-chain` passing
only after its rule proof, and `cooldown` red until it ages. Missing registry data
is an incomplete run, never a clean verdict. Exact historical target examples in
EXPECTED.md are not frozen assertions about the live registry.
