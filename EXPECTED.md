# Expected outcomes — Gradle declarations and security

Baseline authored 2026-10-09. Case 4 graphs must remain separate: normal Gradle
conflict resolution would otherwise hide vulnerable Text 1.9 behind Text 1.12.0.

| Case | File/line | bump-it | secure-it | Gate |
|---|---|---|---|---|
| 2: literal routine | `build.gradle.kts:26` | Highest aged safe 1.x in routine; no code change expected for encodeHexString. | No named baseline advisory expected; live snapshot decides. | Changed artifact aged/safe => comparison PASS; unrelated fixture findings inherited. |
| 2: catalog routine | `gradle/libs.versions.toml:3` and `gradle/libs.versions.toml:7` | JetBrains annotations 24.0.1 -> highest aged safe 24.x in routine (historical 24.1.0); highest eligible newer line in its own major PR. Edit version.ref, preserving alias. | No named baseline advisory expected; live snapshot decides. | Compile-only inventory target exact; comparison/cooldown PASS if aged/safe. |
| 2: catalog major | `gradle/libs.versions.toml:2` and `gradle/libs.versions.toml:6` | SLF4J 1.7.36 -> highest safe aged 2.x (or newer eligible line) in its own major PR. It is already the historical last stable 1.7 release, so a routine SLF4J move is not guaranteed. Edit the catalog version, not the alias. | No named baseline advisory expected; live snapshot decides. | Resolve the new major; compare/cooldown PASS if safe/aged; inherited security graph findings may remain. |
| 3: precompiled convention | `buildSrc/src/main/kotlin/fixture.conventions.gradle.kts:10` | Highest aged safe Commons IO 2.x routine move; source indexed and .gradle.kts edit permitted. Root compile/runtime/test locations must all land exactly. | 2.17.0 is the fix for CVE-2024-47554, not an intentional vulnerable baseline. New advisories may add work. | buildSrc and root inventory complete; baseline/updated IO API test PASS. |
| 5: named plugin | `buildSrc/build.gradle.kts:2` | Move marker 6.0.0 in routine within 6.x; newer major separate. Explicit planned buildSrc plugin move enables unnamed plugin-driven VERSION changes in that build. | Live advisories decide. | Marker resolves; judge every changed injected artifact. v0.2.2 rejects graph additions/removals; B2 can accept them under complete planned-swap proof (see version boundary). |
| 5: shorthand/injected stdlib | `buildSrc/build.gradle.kts:11` | org.jetbrains.kotlin:kotlin-stdlib absent from coordinate evidence: notes only, NOT a separate move. Same for other unnamed Kotlin DSL injected libraries. Do not expand the shorthand to make a plan land. | Security inventory still includes them. | Existing unnamed modules may follow a planned buildSrc plugin update; independent unplanned changes must fail verification. |
| 4: direct vulnerability | `build.gradle.kts:32` | Routine highest aged safe 1.x can also fix it; snapshot still governs. | Non-major security PR edits 1.9 to the rule's lowest aged fixing 1.x; historically 1.10.0 for CVE-2022-42889. | Baseline scan FAIL at :directSecurity; compare marks targeted advisory fixed and PASS if no new findings. |
| 4: transitive vulnerability | `build.gradle.kts:33` (parent); same package also through directSecurity | Never bump the transitive on its own. Parent may have a routine 2.x bump; that does not satisfy this independent secure-it trial. | Commons Lang 3.14.0 (and Text 1.9's lower copy) -> lowest aged fixing 3.x, historically 3.18.0. Add explicit floor dependencies with because naming CVE-2025-48924/GHSA-j288-q9x7-2f5v at every targeted config and create .github/dependency-floors.json. Keep Configuration parent at 2.11.0 in the security plan. | Baseline FAIL in :transitiveSecurity and :directSecurity. Record selectors/declaredIn/version/advisories/purpose=security/reason/added; exact declarations and resolved >=floor must validate. Plain constraints do not satisfy floor contract. |
| 4: floor lifecycle | Future build.gradle.kts declarations + .github/dependency-floors.json (no baseline file/line) | Do not raise or remove a recorded floor. | Initially retain: removing it restores vulnerable Lang. After parent/Text updates make it unnecessary, a SEPARATE floor-removal PR is allowed only after joint unlocked proof. | No targeted advisory remains in any copy after a successful fix/removal. |

The intended secure-it non-major batch fixes Text and every failing Lang copy;
full scan then passes for these two named advisories. Additional live findings may
remain or require separate units; compare can pass with inherited findings. There
must be no Lang declaration/floor on the initial baseline: it is genuinely
transitive and can be checked in the inventory's declared/resolved lists.

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

Gate: pinned to the supply-chain v0.2.2 release commit `a8c3f1010bc321ab3c45603eb5600ff9dda7ff3a` (`# v0.2.2`), as the
other adopters are.

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
export SC_CHECKOUT="$HOME/.local/share/leanish/supply-chain-v0.2.2"
# For a post-release rerun, point SC_CHECKOUT at the actual v0.2.3 checkout instead.
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
against v0.2.2 and the shipped v0.2.3. Retain all five actual inventories and both
focused verification/compare reports. Prepare-only and syntax checks do not prove
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

## v0.2.2 versus the proposed v0.2.3 fixes

The gate remains pinned to v0.2.2. These are version-specific expectations for
B1/B2/B3 from the run-1 review, not permission to weaken verification:

| Fix | v0.2.2 behavior | With the reviewed fix in v0.2.3 |
|---|---|---|
| B1: capture declarations after resolution | Lazy/default dependencies can appear only on one side of inventory, e.g. Checkstyle at :checkstyle. Apparent declaration additions are not evidence of an actual fixture edit. | Two-phase export resolves configurations before recording declarations, so unchanged default dependencies are recorded consistently. Any unresolved configuration still fails closed. |
| B2: plugin graph additions/removals | bump-it's planned-swap proof exempts only existing unnamed modules' version changes. Kotlin buildSrc graph removals/additions and Lombok newly present at :testCompileClasspath can reject otherwise mechanical plugin bumps. | Additions/removals of unnamed plugin-driven dependencies can be accepted ONLY in the same build with a planned classpath plugin move, when the whole edit (including file modes) proves nothing but planned swaps and independently verified npm/Action/wrapper bytes changed. Planned declarations must still land exactly. Every added/changed resolved artifact still faces the gate. |
| B3: secure-it shares the plugin proof | Security verification does not apply bump-it's equivalent plugin-driven proof. Kotlin buildSrc version changes may reject a security plan that includes a plugin move. | Such changes can be accepted under the same complete planned-swap proof when a matching plugin classpath move is actually planned. Security floor insertions are not version swaps: a mixed floor/plugin edit needs its own exact proof; do not assume B3 permits it. |

Ignoring **/.kotlin/ is the fixture repair, not a tool-wide exemption for extra
edits. No Kotlin error log is present in these local baselines. Previously opened
PRs containing tracked logs still need those files removed in their own heads;
adding this ignore rule alone does not remove tracked files. Arbitrary source or
policy edits, unexplained new/removed files, newly named dependencies, other-build
changes and missing evidence must continue to fail the plugin-driven proof.

B1 can remove false inventory deltas; B2 is relevant to bump-it's conventions
major and Gradle routine; B3 is conditionally relevant to security plugin plans.
Without the failed security plan/diff, do not attribute the observed Checkstyle
addition entirely to B1 or promise B3 alone makes a floor-bearing PR pass.
These repairs do not change case-10 declaration versus resolution semantics.
The independent buildSrc Commons IO convention declaration is a normal project
dependency, not a classpath plugin trigger; unrelated induced changes still use
the documented limitation in packages/ci/README.md's Gradle declarations section.

`VERIFY: SUPPLY-CHAIN-0.2.3` — Check the shipped implementation and changelog for
B1/B2/B3, then rerun tool verification and gate CI with recorded versions. Do not
claim v0.2.3 behavior from the still-pinned v0.2.2 workflow. Retain failures caused
by age, advisories, unsupported edits or insufficient proof as failures.
