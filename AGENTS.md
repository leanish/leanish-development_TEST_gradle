# Synthetic supply-chain fixture

Run the repository check command in README.md after edits. Keep tests compatible with
both baseline and planned versions. Cases 11 and 12 (EXPECTED.md) intentionally use APIs
that their major upgrade removes: adapt that code to the new version while keeping what
the tests assert; never delete or weaken a test to make an upgrade pass. Do not publish a package. Dependency changes are
intentional; do not modernize unrelated pins. Keep README.md and EXPECTED.md out of
Gradle source files: source evidence includes comments in scripts and catalogs.
