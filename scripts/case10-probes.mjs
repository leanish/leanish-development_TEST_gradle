// Local copies only: no git commands, publication, or edits to the baseline.
import { cp, mkdir, mkdtemp, readFile, readdir, writeFile } from 'node:fs/promises';
import { basename, join, resolve } from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';

const root = fileURLToPath(new URL('../', import.meta.url));
if (process.argv.slice(2).some(arg => arg !== '--prepare-only')) {
  throw new Error('Usage: node scripts/case10-probes.mjs [--prepare-only]');
}
const prepareOnly = process.argv.includes('--prepare-only');
if (!prepareOnly && !process.env.SC_CHECKOUT) throw new Error('Set SC_CHECKOUT to the installed supply-chain checkout');
await mkdir(join(root, '.validation'), { recursive: true });
const output = await mkdtemp(join(root, '.validation/case10-'));
const excluded = new Set(['.git', '.validation', '.gradle', '.kotlin', 'build', 'node_modules']);
const variants = ['baseline', 'b-only', 'a-only', 'strict', 'strict-a-only'];
const script = 'declared-lower.gradle.kts';
for (const variant of variants) {
  const dir = join(output, variant);
  await mkdir(dir);
  // Copy entries separately: fs.cp rejects copying a root into its own child,
  // even when the destination's .validation ancestor is excluded by the filter.
  for (const entry of await readdir(root)) {
    if (excluded.has(entry)) continue;
    await cp(join(root, entry), join(dir, entry), {
      recursive: true, filter: path => !excluded.has(basename(path)),
    });
  }
  let text = await readFile(join(dir, script), 'utf8');
  if (variant === 'b-only') {
    const before = 'org.apache.httpcomponents:httpcore:4.4.10';
    if (text.split(before).length !== 2) throw new Error('B-only probe needs exactly one seeded ordinary HttpCore declaration');
    text = text.replace(before, 'org.apache.httpcomponents:httpcore:4.4.12');
  }
  if (variant === 'a-only' || variant === 'strict-a-only') {
    const before = 'org.apache.httpcomponents:httpclient:4.5.13';
    if (text.split(before).length !== 2) throw new Error('A-only probe needs exactly one seeded HttpClient declaration');
    text = text.replace(before, 'org.apache.httpcomponents:httpclient:4.5.14');
  }
  await writeFile(join(dir, script), text);
}
console.log(`Probe copies and evidence: ${output}`);
if (prepareOnly) process.exit(0);

// Use the installed implementation, not a model of its declaration checks.
const source = relative => pathToFileURL(join(resolve(process.env.SC_CHECKOUT), relative)).href;
const { runProcess } = await import(source('packages/ci/src/process.ts'));
const { workingTree } = await import(source('packages/ci/src/tree.ts'));
const { runGradleInventory, gradleLocation } = await import(source('packages/ci/src/gradle.ts'));
const { runCompare } = await import(source('packages/ci/src/gate.ts'));
const { verifyPlan } = await import(source('packages/bump-it/src/verify.ts'));
await mkdir(join(output, 'tmp'));
process.env.TMPDIR = join(output, 'tmp');
const inventories = new Map();
let failed = false;
const save = (name, data) => writeFile(join(output, name), JSON.stringify(data, null, 2) + '\n');
for (const variant of variants) {
  const dir = join(output, variant);
  const properties = variant.startsWith('strict') ? ['-Pfixture.strictLower=true'] : [];
  const run = async (command, args, options) => {
    const result = await runProcess(command, [...args, ...properties], options);
    if (result.code !== 0) await save(`${variant}-inventory-process-failure.json`, result);
    return result;
  };
  for (const [name, args] of [
    ['check', ['--no-daemon', ...properties, 'check']],
    ['insight', ['--no-daemon', ...properties, 'dependencyInsight', '--configuration', 'declaredLower', '--dependency', 'httpcore']],
  ]) {
    const result = await runProcess(join(dir, 'gradlew'), args, { cwd: dir });
    await save(`${variant}-${name}.json`, result);
    if (result.code !== 0) failed = true;
  }
  try {
    const inventory = await runGradleInventory(dir, ['.'], 'worktree', run);
    inventories.set(variant, inventory);
    await save(`${variant}-inventory.json`, inventory);
  } catch (error) {
    failed = true;
    await save(`${variant}-inventory-error.json`, { error: String(error) });
  }
}
const base = workingTree(join(output, 'baseline'));
const env = { run: runProcess, fetch: globalThis.fetch, now: () => new Date(),
  osvScanner: process.env.OSV_SCANNER ?? 'osv-scanner', githubToken: process.env.GITHUB_TOKEN ?? process.env.GH_TOKEN };
for (const [variant, artifact, from, to] of [
  ['b-only', 'httpcore', '4.4.10', '4.4.12'],
  ['a-only', 'httpclient', '4.5.13', '4.5.14'],
]) {
  const gradle = { base: inventories.get('baseline'), head: inventories.get(variant) };
  if (!gradle.base || !gradle.head) { failed = true; continue; }
  const name = `org.apache.httpcomponents:${artifact}`;
  const locations = gradle.base.builds.flatMap(build => build.configurations
    .filter(config => config.declared.some(dep => `${dep.group}:${dep.name}` === name && dep.version === from))
    .map(config => gradleLocation(build.build, config.id)));
  if (locations.length === 0) throw new Error(`No baseline declaration for ${name}@${from}`);
  const plan = { kind: 'routine', topic: `case10-${variant}`, moves: [
    { ecosystem: 'Maven', name, from, to, mechanism: 'gradle-declared', locations, major: false, declarations: [] },
  ], npmFiles: [], changes: [], changeCount: 0, notes: [] };
  await save(`${variant}-plan.json`, plan);
  const head = workingTree(join(output, variant));
  try {
    const problems = await verifyPlan({ plan, npmFiles: new Map(), base, head, env, gradle,
      changedFiles: [script], modeChanged: [] });
    await save(`${variant}-verification.json`, { problems });
    if (problems.length) failed = true;
    // Separate gate invocation to retain full findings/cooldown; a second snapshot.
    const comparison = await runCompare(base, head, env, gradle);
    await save(`${variant}-compare.json`, comparison);
    if (comparison.failures.length || !comparison.cooldown.evaluated || comparison.cooldown.held.length) failed = true;
  } catch (error) {
    failed = true;
    await save(`${variant}-verification-error.json`, { error: String(error) });
  }
}
if (failed) process.exitCode = 1;
