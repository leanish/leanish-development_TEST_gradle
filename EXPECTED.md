# Expected outcomes — Gradle declarations and security

Baseline authored 2026-10-09. Case 4 graphs must remain separate: normal Gradle
conflict resolution would otherwise hide vulnerable Text 1.9 behind Text 1.12.0.

| Case | File/line | bump-it | secure-it | Gate |
|---|---|---|---|---|
| 2: literal routine | `build.gradle.kts:26` | Highest aged safe 1.x in routine; no code change expected for encodeHexString. | No named baseline advisory expected; live snapshot decides. | Changed artifact aged/safe => comparison PASS; unrelated fixture findings inherited. |
| 2: catalog routine | `gradle/libs.versions.toml:3` and `gradle/libs.versions.toml:7` | JetBrains annotations 24.0.1 -> highest aged safe 24.x in routine (historical 24.1.0); highest eligible newer line in its own major PR. Edit version.ref, preserving alias. | No named baseline advisory expected; live snapshot decides. | Compile-only inventory target exact; comparison/cooldown PASS if aged/safe. |
| 2: catalog major | `gradle/libs.versions.toml:2` and `gradle/libs.versions.toml:6` | SLF4J 1.7.36 -> highest safe aged 2.x (or newer eligible line) in its own major PR. It is already the historical last stable 1.7 release, so a routine SLF4J move is not guaranteed. Edit the catalog version, not the alias. | No named baseline advisory expected; live snapshot decides. | Resolve the new major; compare/cooldown PASS if safe/aged; inherited security graph findings may remain. |
| 3: precompiled convention | `buildSrc/src/main/kotlin/fixture.conventions.gradle.kts:10` | Highest aged safe Commons IO 2.x routine move; source indexed and .gradle.kts edit permitted. Root compile/runtime/test locations must all land exactly. | 2.17.0 is the fix for CVE-2024-47554, not an intentional vulnerable baseline. New advisories may add work. | buildSrc and root inventory complete; baseline/updated IO API test PASS. |
| 5: named plugin | `buildSrc/build.gradle.kts:2` | Move marker 6.0.0 in routine within 6.x; newer major separate. Explicit planned buildSrc plugin move enables unnamed plugin-driven VERSION changes in that build. | Live advisories decide. | Marker resolves; compare head with the plan-applied Gradle reference, including injected graph additions/removals. Judge every changed artifact. Missing or differing reference evidence fails. |
| 5: shorthand/injected stdlib | `buildSrc/build.gradle.kts:11` | org.jetbrains.kotlin:kotlin-stdlib absent from coordinate evidence: notes only, NOT a separate move. Same for other unnamed Kotlin DSL injected libraries. Do not expand the shorthand to make a plan land. | Security inventory still includes them. | Existing unnamed modules may follow a planned buildSrc plugin update; independent unplanned changes must fail verification. |
| 4: Text plus carried Lang | build.gradle.kts, :directSecurity | Highest aged safe Text 1.x in routine; no independent transitive Lang bump. | Move Text 1.9 -> 1.14.0 for the named snapshot, combining Text's own GHSA-599f-7c49-w659 fix with its carried Lang GHSA-j288-q9x7-2f5v fix. Text 1.14.0 requests Lang 3.18.0. | Baseline declares Text only and resolves Lang 3.11. Head declares Text at the selected parent target and resolves fixed Lang. No Lang floor for this successful parent search. |
| 4: Configuration plus carried Lang | build.gradle.kts, :transitiveSecurity | Highest aged safe Configuration 2.x; no independent Lang bump. | Move Configuration 2.11.0 -> 2.15.0 for the named current snapshot. Its own GHSA-337m-mw94-2v6g requires >=2.15.0; this move also brings Lang 3.20.0. | Source-named Configuration declaration moves; Lang advisory disappears. Do not retain the obsolete expectation that Configuration stays at 2.11.0 or that this location necessarily receives a Lang floor. |
| 4: real floor fallback | build.gradle.kts, :floorSecurity | No eligible newer stable Digester 3.x parent expected; recorded floors are not routine bump targets. | Digester 3.2 brings BeanUtils 1.8.3. With no newer eligible parent, add the lowest fixing BeanUtils security floor, 1.11.0 for GHSA-wxr5-93ph-8wr9 and the named snapshot. Record the failed parent-search reason. Additional vulnerable transitives receive their own justified work. | Explicit dependency with because naming all targeted advisories, plus matching .github/dependency-floors.json record. Exact declaration and resolution >=floor must validate. No constraints-only repair. |
| 4: floor lifecycle | Future :floorSecurity declarations and .github/dependency-floors.json | Do not raise/remove security floors. | Immediately after the floor lands, retain it: unlocked resolution restores vulnerable BeanUtils through Digester 3.2. After the controlled parent-replacement commit below, propose a separate floor-removal PR only after joint unlocked proof. | Safe locked/resolved bytes alone do not justify removal. The reference after removal must match the edited head, retain complete resolution and leave no targeted advisory. |

The intended security batch combines each source-named parent's own remediation
with the Lang remediation it carries. The distinct Lang 3.11 and 3.14 baseline
findings need not share one parent. No Lang declaration/floor belongs on the
initial baseline. The isolated Digester graph preserves genuine floor insertion,
retention and removal coverage after successful parent remediation consumes the
old Lang-floor case.

Advisory sources:
- [CVE-2022-42889 / GHSA-599f-7c49-w659](https://github.com/advisories/GHSA-599f-7c49-w659): Text 1.5..1.9 affected; 1.10.0 patch.
- [CVE-2025-48924 / GHSA-j288-q9x7-2f5v](https://github.com/advisories/GHSA-j288-q9x7-2f5v): Lang 3.x below 3.18.0 affected.
- [Configuration 2.11.0 POM](https://github.com/apache/commons-configuration/blob/rel/commons-configuration-2.11.0/pom.xml): Lang 3.14.0, Text 1.12.0; optional dependencies are not installed by this fixture.

`VERIFY: ARTIFACTS` — Confirm Central POM existence and upload ages for Codec 1.16.0,
SLF4J 1.7.36, JetBrains annotations 24.0.1 and 24.1.0, IO 2.17.0, Text 1.9 and 1.10.0, Configuration 2.11.0, Lang 3.18.0,
JUnit Jupiter 5.11.0, launcher 1.11.0 and AssertJ 3.26.3. Confirm current advisory
snapshot and lowest aged security targets; Text's 1.9 Lang request may be raised
by its planned direct update, so use every actual failing/resolved copy/location.
The literal test libraries are named direct dependencies here and may themselves
be planned (including a JUnit 6 major); their launcher must stay API-compatible.

`VERIFY: KOTLIN-DSL` — The [Plugin Portal](https://plugins.gradle.org/plugin/org.gradle.kotlin.kotlin-dsl/6.0.0)
confirms 6.0.0 exists, created 2025-04-11. Still confirm its marker POM upload
age and the selected newer aged plugin/wrapper pair. Claude reported the original
baseline passes on JDK 25. Recheck the corrected buildSrc baseline with daemon
JDK 21 and the main Java 25 toolchain. Version must stay explicit so this is a planned plugin update in buildSrc.
In inventories expect buildSrc's buildscript classpath plus its compile/runtime
classpath; no missing-build or ignored-configuration coverage shortcut is allowed.

`VERIFY: BUILD` — Run ./gradlew --no-daemon check on baseline, security target and
bump routine/major heads. Confirm the precompiled plugin is applied, both security
graphs resolve, and nested buildSrc inventory is exported. Claude reported the
original baseline passes on daemon JDK 25 but fails on JDK 21 because buildSrc
required Java 25. Its explicit toolchain is now removed; rerun baseline check and
tools inventory on daemon JDK 21 with the main Java 25 toolchain installed.
Local Gradle execution was blocked by the sandbox's socket restriction.

## Shared gate and action checks

| Case | File/line | bump-it | secure-it | Gate |
|---|---|---|---|---|
| Gate adoption | `.github/workflows/supply-chain.yml:22` | Own Action update may join routine/major according to release line; repin before trials. | Only if an advisory has a selected fix. | Daily scan, PR compare, open-PR rescan; require supply-chain and cooldown after repin. |
| 8: checkout pin | `.github/workflows/ci.yml:16` | Routine: highest eligible v4 release and matching commit/comment; separate major for highest eligible newer line. | No fixed security expectation; live advisories decide. | Changed SHA must match its full release tag comment and aged release; otherwise FAIL. |
| 8: Java setup pin | `.github/workflows/ci.yml:19` | Routine v4 refresh and separate newer-major PR when eligible; preserve JDK 25 input. | Live advisories decide. | Same SHA/tag/age checks. |

Case 9: the release-age policy is `.github/supply-chain.json:2` (`releaseAgeDays: 7`).
No deliberately young fixture exists. For aged changed targets and a complete passing
comparison, cooldown should PASS. A rule-approved young security target can make
comparison PASS but cooldown FAIL (held); bump-it must not publish a held plan.
Routine and major exact targets, advisory counts and extra security units are live
registry outcomes, not fixed to the illustrative versions below. npm identity,
source or provenance breaks can block an otherwise safe version.

Gate trials use the same supply-chain commit for inventory and comparison.
The prerelease baseline pins carrier-first-security-fixes commit
87b1e9e5b54657f668bf9087b81441fb5eb251cc. After v0.3.0 is released, replace
that pin with its dereferenced release commit and full release-tag comment.
Do not claim released v0.3.0 behavior from a v0.2.2 workflow.

`VERIFY: ACTIONS` — Confirm each exact SHA/tag pair and published release date via
GitHub (dereference annotated tags). Baseline checkout v4.1.1 is
b4ffde65f46336ab88eb53be808477a3936bae11 (2023 release). Baseline setup-java v4.2.1 is
99b8673ff64fbf99d8d325f52d9a5bdedb8483e9 (2024 release).
Choose live eligible routine and newer-major releases using their actual published
times. A moved tag, missing release or new advisory changes the expected candidate.

| Case | File/line | bump-it | secure-it | Gate |
|---|---|---|---|---|
| 6: old wrapper | `gradle/wrapper/gradle-wrapper.properties:3` | Root wrapper 9.3.1 -> highest aged stable non-broken eligible Gradle, in the routine PR. Replace all four tool-generated files; gradlew remains executable; selected distribution/jar checksums verified. | No Gradle wrapper remediation mechanism. | Wrapper is not an external module in the gate; bump-it performs wrapper-specific verification. Judge any resulting resolved-module changes normally. |

Claude verified both baseline wrapper JARs against the official Gradle 9.3.1
checksum: b3a875ddc1f044746e1b1a55f645584505f4a10438c1afea9f15e92a7c42ec13.
Gradle 9.3.1 was built 2026-01-29 and is aged. The wrapper files were generated
directly by WrapperGenerator from the cached 9.3.1 distribution.

`VERIFY: WRAPPER` — Select and verify the live wrapper target with the tool; do
not invent a fixed latest version. When preparing adoption, optionally add the
official 9.3.1 binary distribution checksum.
A wrapper-only move is not the pluginDriven exemption; if embedded dependencies
change without a planned plugin move and verification rejects them, record a
regression/failure rather than adding dependency declarations to bypass it.


## Case 10: declared B is lower than A's transitive request

| Variant | File/line | bump-it | secure-it | Gate / verification |
|---|---|---|---|---|
| A baseline | `declared-lower.gradle.kts:11` | HttpClient 4.5.13 -> highest eligible safe 4.x routine; historically 4.5.14, which requests higher HttpCore. | Judge resolved advisories, no fixed security unit expected for this graph. | A's actual resolved graph is scanned. Keep this configuration independent of the other fixtures. |
| Ordinary B | `declared-lower.gradle.kts:19` | Plan HttpCore from DECLARED 4.4.10, not resolved 4.4.13. Routine target is highest eligible safe 4.x, historically 4.4.16. | An overridden-away lower declaration is not itself a finding; only the selected version is scanned. | Inventory :declaredLower should declare B 4.4.10 and resolve B 4.4.13. Gate judges 4.4.13, not absent 4.4.10. Exact-plan landing checks the declaration, without requiring resolution to equal it. |
| A-only update | Same A/B lines above; change A to historically 4.5.14 and keep ordinary B 4.4.10 | Focused acceptance plan moves A only; normal weekly routine may combine A and B moves. | Resolved B fixes/new advisories follow normal security policy. | B resolves 4.4.13 -> 4.4.16 with its declaration unchanged. Direct/declaration verification MUST accept this resolved-only B change; gate still judges changed B 4.4.16 for age/advisories. |
| Strict B, opt-in | `declared-lower.gradle.kts:15` and `declared-lower.gradle.kts:6` | This is a strict declaration, not an ineffective pin; update the strict version only if explicitly trialing that variant. | Judge the actual lower B 4.4.10 if it resolves; a vulnerability there is real. | Expected to FORCE 4.4.10 because A's transitive requirement is plain require. This variant is not expected to fail merely because A requests 4.4.13. If it does fail, inventory must fail closed/incomplete, not pass as clean. |

Use two controlled acceptance heads against the same normal baseline, independently
of the weekly combined routine:

1. **B-only below the transitive request:** a deliberately selected aged intermediate
   declaration 4.4.10 -> 4.4.12 still resolves 4.4.13. This is a verifier probe, not
   a claim that bump-it's highest-version rule selects 4.4.12. Verify declared
   landing passes with resolution unchanged. The actual routine target is tested
   separately from the candidate preview/plan.
2. **A-only:** update A 4.5.13 -> 4.5.14 with B's declaration still 4.4.10. Capture
   a focused plan and base/head inventories; declaration checks must see only A
   moved, while comparison sees B's resolved 4.4.13 -> 4.4.16. A resolved-only B
   change is not an outside-plan DIRECT change and does not need pluginDriven.

The actual weekly routine may move both A and B; that proves the combined path,
not the isolated A-only path. Do not claim all three paths ran from one combined
PR. A-only verification needs a matching focused plan; a manual edit does not
magically change the tool's existing plan. Use a local verifier probe or a focused
review trial in Claude's networked environment, not an invented CLI dry-run flag.

Run the reproducible probes below and compare their dependencyInsight and inventory outputs. Do not
freeze tests to a baseline resolved version that legitimate updates should change.
The strict variant uses -Pfixture.strictLower=true; no strict default is committed.
If adding a genuinely incompatible hard requirement later, resolution can fail;
that would be a different variant with an expected incomplete/red inventory.
[Gradle version semantics](https://docs.gradle.org/current/userguide/dependency_versions.html)
document direct strict declarations overriding ordinary transitive requirements;
confirm behavior on the seeded 9.3.1 wrapper rather than relying on current docs
alone. B is a dependency declaration, not a recorded security floor.

`VERIFY: DECLARED-LOWER-GRADLE` — Confirm Central existence/POM upload age >=7 days
for HttpClient 4.5.13/4.5.14 and HttpCore 4.4.10/4.4.12/4.4.13/4.4.16. Confirm the
published HttpClient POM chain actually requests 4.4.13 and 4.4.16 respectively;
these graph values are unconfirmed locally. Check current advisories and selected
highest eligible routine targets. Run default check, dependencyInsight, actual
candidate planning and the B-only/A-only verification probes; capture declaration
and resolution separately. No full Gradle run is claimed in this sandbox.

`VERIFY: STRICT-LOWER` — On Gradle 9.3.1, run the strict commands and confirm selected
B=4.4.10, including after A=4.5.14. Check any new module metadata/hard constraints
that could instead make resolution fail. If unresolved, preserve that failure in
the inventory and report the variant as failed/incomplete; do not ignore it.

## Case 10 reproducible probes

Start from the seeded declarations, not an already-bumped working copy. Install
Node 24+, JDK 25 for the main toolchain, a supported daemon JVM (JDK 21 here),
osv-scanner and the supply-chain checkout's dependencies. Network access is needed
for Gradle, Central, OSV and GitHub. Set GITHUB_TOKEN only if required for private
Action metadata/rate limits; the helper does not print it.

```sh
export SC_CHECKOUT="/Users/leandro.aguiar/dev/repos/leanish/wt-030"
# For a post-release rerun, point SC_CHECKOUT at the actual v0.3.0 checkout instead.
node scripts/case10-probes.mjs --prepare-only
node scripts/case10-probes.mjs
```

Each command prints its new `.validation/case10-*` directory. Prepare-only creates
copies for inspection; the full command creates a fresh, independent set and runs
it. Nothing modifies the original fixture or creates/uses a Git repository. The
runner does not call bump-it publication or invent a candidate-selection CLI flag.
It imports the installed tool's inventory and verifyPlan/runCompare APIs.
All five copies run check, dependencyInsight and the full inventory export.
The B-only and A-only copies then use focused exact plans, real declaration
locations and the tool's full verifier. A second, separate compare invocation
retains full findings/cooldown; its snapshot can differ from the verifier's first
snapshot. Nonzero exit means a failure or incomplete probe, not a clean result.

In the printed output directory compare:

| Probe | Edit / command used by helper | What to compare |
|---|---|---|
| baseline | No edits; `./gradlew --no-daemon check` and `dependencyInsight --configuration declaredLower --dependency httpcore` | `baseline-inventory.json`, build `.` / `:declaredLower`: declared A=4.5.13, B=4.4.10, resolved B=4.4.13. Check/insight JSON code=0. |
| b-only | Only ordinary B literal 4.4.10 -> 4.4.12; same commands | `b-only-plan.json` plans ONLY B from 4.4.10. Base/head inventory: declared B changes to 4.4.12, resolved B remains 4.4.13. A unchanged. Verification problems empty; compare failures empty and cooldown evaluated with no held versions, subject to live policy. |
| a-only | Only A literal 4.5.13 -> 4.5.14, ordinary B unchanged; same commands | `a-only-plan.json` plans ONLY A. Declared B stays 4.4.10; resolved B rises 4.4.13 -> 4.4.16. Verification/compare/cooldown as above; inspect reports to distinguish direct-plan rejection from legitimate security/age failure. |
| strict | No declaration edit; add `-Pfixture.strictLower=true` to commands/export | Strict B declaration/selected B=4.4.10. Check/insight/inventory must succeed. This is a resolver probe, not a published bump plan. |
| strict-a-only | Only A=4.5.14; add same strict property | B still strictly 4.4.10 and resolved 4.4.10. If it fails resolution, preserve check/insight process output and inventory failure; do not count an incomplete inventory as a pass. |

All inventory configurations must resolve, including buildSrc. Inspect the
`*-verification.json`, `*-compare.json` and any `*-error.json` /
`*-inventory-process-failure.json`; do not use dependencyInsight alone to claim
verification passed. Generated files in ignored copies are not plan edits. The
helper records only the one controlled script edit per focused plan; do not make
manual edits in copies and reuse that assertion of changedFiles. Prepare a new
probe set instead. Case 10 does not use the plugin-driven exemption, so B1/B2/B3
are not an excuse for accepting an unplanned case-10 declaration change.

`VERIFY: CASE10-PROBES` — Run the full helper outside the socket-restricted sandbox
against the v0.3.0 candidate and the shipped v0.3.0. Retain all five actual inventories, both
focused verification/compare reports and each variant's `*-reference-inventory.json`. Prepare-only and syntax checks do not prove
resolution or a passing live gate.

## Run acceptance and evidence (cases 8–9 and deferred majors)

EXPECTED.md is the single source of expectations. README.md contains setup and
entry commands; VALIDATION.md and REGISTRY-EVIDENCE.json contain observed evidence.
Exact live targets, advisory counts, identities and release dates depend on the
registry snapshot. A missing or too-young eligible candidate is timing-dependent,
not proof that the fixture or tool failed. No young release is fabricated.

Run secure-it and bump-it independently against the same seeded main revision;
capture evidence before merging either tool's PRs. Do not merge parent updates
before testing transitive security fixes. Preserve the seed revision externally
for repeat trials; after merges, do not expect the same candidates/findings.
Unchanged weeks may review already-open PRs rather than create new ones.

bump-it's default maximum is **three new major PRs per run**, in addition to the
routine unit. An eligible major can be deferred behind higher-priority packages,
including Actions and the new glob direct dependency. An absent wordwrap,
annotations, setup-java or other major PR is not a bug solely because it is absent.
Check the deployed limit and each unit's status/priority; distinguish deferred,
already-open, held, failed, no eligible candidate and notes-only dependencies.
Capture later runs (or a deliberately configured larger limit) to exercise every
eligible major. Never mark a deferred unit as a successful update.

For every run, retain the tool version/commit, configuration, baseline/head IDs,
complete tool record **including units, waiting, gaps, notes and errors**, candidate
versions, plan payloads, PR title/body/diff, and verification/build logs. Include
failed units' attempted plans and diffs even when no PR exists. If the runner does
not retain a failed plan/diff, report that evidence gap rather than inventing a
root cause. Preserve inventories for both sides, resolved and declared versions,
findings fixed/inherited/added, the full compare report, cooldown verdict/held
versions and CI results. Store scratch evidence in ignored .validation/ or outside
the repository; no generated diagnostics belong in a remediation PR.

Default-branch scan must fail on the seeded unexcepted advisories. A successful
fix PR can pass compare with other findings inherited; this does not mean main
is clean. CLI exit zero, creation of a PR, or a passing smoke test alone is not
acceptance. Judge every changed resolved artifact for advisories, age and identity.
Missing registry data/inventory coverage is incomplete, never a clean pass.

`VERIFY: RUN-EVIDENCE` — Confirm the deployed maxNewMajorsPerRun value and retain
all fields and failed-unit artifacts above in the next weekly report. Reconcile
all cases against units, not only the published PR list.

## supply-chain 0.3.0 verification

| Behavior | Expected result |
|---|---|
| Declarations after resolution | Lazy/default plugin declarations are captured consistently after all configurations resolve. Missing or unresolved coverage remains incomplete/failing. |
| Planned plugin graph changes | Base with the plan applied by Gradle supplies the reference. Head declarations and resolved modules must match it configuration by configuration, including additions/removals and versionless declarations. Every changed/added module still faces the gate. |
| Security plugin move plus floor | Both planned changes are applied to the same reference. Accept faithful combined fallout; reject differences from that reference. The obsolete version-swap proof is not the acceptance mechanism. |
| Plugin-declared vulnerable dependency | When no eligible source-named declaration can carry its fix, secure-it adds an explicit because dependency and matching security-floor record next to the plugin declaration. Report the unsuccessful parent search. Do not edit an injected declaration as if the consumer sources held it. |
| Unsupported plugin defaults/strict versions | A floor that changes defaults beyond the reference contract or cannot resolve against a strict declaration remains a failure. Do not suppress it. |

Ignored Kotlin diagnostics do not remove already tracked files. Preserve complete
base/reference/head inventories, plans, findings, cooldown and build evidence.
A passing dependency test alone does not prove remediation or reference equality.

## C: Gradle parent remediation

| Variant | File/configuration | bump-it | secure-it | Gate / inventory |
|---|---|---|---|---|
| C: transitive only | build.gradle.kts, :okhttpSecurity, OkHttp 4.9.3 seed | Highest eligible aged safe OkHttp 4.x in routine; no direct Okio bump. | Move source-named OkHttp 4.9.3 -> 4.12.0, carrying the Okio GHSA-w33c-445m-f8w7 fix. No Okio floor for a successful search. | Baseline resolves vulnerable Okio 2.8.0. Reference/head resolve the fixed JVM graph at 3.6.0 and contain no targeted advisory under either okio or okio-jvm. |
| C: combined own and transitive | Separate seed commit changing only OkHttp to 4.9.1 | Normal direct update rules. | One OkHttp 4.9.1 -> 4.12.0 declaration move fixes its own GHSA-3cqm-mf7h-prrj and carries the Okio fix. Do not stop at the own-only 4.9.2 target. | Same complete JVM inventory and reference checks; both advisory groups disappear. |
| C: search bounds | Reference resolutions for :okhttpSecurity | Not a routine candidate-depth assertion. | Lowest aged eligible parent first; no more than six reference probes for a vulnerable module. A failed/spent search retains a floor with a note. | Retain attempted parent versions and actual reference inventories; a seventh probe is a regression. |

### Seed of 2026-10-11: combined OkHttp and plugin move plus floor

The 4.9.3 baseline above was run first (leanish/test-supply-chain-gradle#12 at
seed 3078549). This seed changes only OkHttp to 4.9.1 and adds
`id("org.ajoberstar.grgit") version "5.3.0" apply false` to the root plugins.

| Case | File/configuration | bump-it | secure-it | Gate / inventory |
|---|---|---|---|---|
| C: combined own and transitive | build.gradle.kts, :okhttpSecurity, OkHttp 4.9.1 | Normal direct update rules. | One OkHttp 4.9.1 -> 4.12.0 move: its own GHSA-3cqm-mf7h-prrj and the carried Okio fix. Not the own-only 4.9.2. | Both advisory groups disappear from okio/okio-jvm and okhttp. |
| Plugin move plus floor | build.gradle.kts plugins, `:buildscript.classpath` | Routine may move grgit within 5.x on its own rules. | Grgit 5.3.0 brings JGit 6.10.0.202406032230-r (GHSA-vrpq-qp53-qv56); 5.3.2 brings 6.10.1.202505221210-r. Move the grgit marker 5.3.0 -> 5.3.2 (one reference probe), carrying the JGit fix, in the same security unit as the BeanUtils and kotlin-gradle-plugin floors. No JGit floor. | One reference applies the plugin move and the floors together; head equals it configuration by configuration. |

Use schemaVersion 2 inventories for base, reference and head. Every configuration
must resolve completely; preserve non-constraint graph edges showing which
source-named declaration reaches each vulnerable component.

With Gradle Module Metadata, the fixed JVM graph must include the selected
com.squareup.okio:okio-jvm:3.6.0 component. Retain any okio:3.6.0 component and
redirect/dependency edges that Gradle actually exports. Do not require a synthetic
okio node if the selected-component model exports only its JVM replacement.

The parent declaration is com.squareup.okhttp3:okhttp; neither Okio artifact is
a manually added baseline declaration. Scan every selected component, including
okio-jvm and Kotlin modules. POM requests alone are not inventory proof.

OkHttp 4.10.0 brings Okio 3.0.0 and 4.11.0 brings 3.2.0; these remain vulnerable.
An artifact-name change or disappearance of the old okio coordinate is not
successful remediation if the advisory survives on okio-jvm. The verifier checks
carried advisories on replacement coordinates too.

Run the 4.9.3 and 4.9.1 baselines independently; do not put both versions in
different configurations/builds and expect this planner to move them separately.

### Controlled floor-removal lifecycle commit

After the tool's floor PR is merged, leave its generated floor declarations and
records untouched. Replace only the original Digester dependency with
`floorSecurity("commons-beanutils:commons-beanutils:1.11.0")`. A normal
declaration then supplies the fixed BeanUtils version, and the separate
floor-removal PR must establish jointly that the floor became redundant.

For each independent seed and remediation head:

```sh
./gradlew --no-daemon check
./gradlew --no-daemon dependencyInsight --configuration okhttpSecurity --dependency okio
./gradlew --no-daemon dependencyInsight --configuration transitiveSecurity --dependency commons-lang3
./gradlew --no-daemon dependencyInsight --configuration floorSecurity --dependency commons-beanutils
```

Capture the tool's complete inventories as well. `dependencyInsight` alone does
not establish reference verification.
