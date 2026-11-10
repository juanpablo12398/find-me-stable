import fs from 'node:fs';
import path from 'node:path';

const candidatePaths = [
  path.resolve('coverage/coverage-summary.json'),
  path.resolve('frontend/coverage/coverage-summary.json'),
];
const summaryPath = candidatePaths.find((p) => fs.existsSync(p));
if (!summaryPath) {
  console.error('[coverage] No existe coverage-summary.json. Ejecuta primero test:js:cov.');
  process.exit(1);
}

const summary = JSON.parse(fs.readFileSync(summaryPath, 'utf8'));

const GLOBAL_THRESHOLDS = {
  statements: 16,
  branches: 18,
  functions: 13,
  lines: 16,
};

const MODULE_THRESHOLDS = [
  {
    name: 'src/services',
    include: ['/src/services/'],
    thresholds: { statements: 55, branches: 45, functions: 50, lines: 55 },
  },
  {
    name: 'src/components/map',
    include: ['/src/components/map/'],
    thresholds: { statements: 50, branches: 40, functions: 45, lines: 50 },
  },
  {
    name: 'src/utils',
    include: ['/src/utils/'],
    thresholds: { statements: 80, branches: 50, functions: 80, lines: 80 },
  },
];

function normalizeKey(k) {
  return k.replaceAll('\\', '/');
}

function ratio(covered, total) {
  return total === 0 ? 100 : (covered / total) * 100;
}

function assertThresholds(label, stats, thresholds) {
  const failures = [];
  for (const key of ['statements', 'branches', 'functions', 'lines']) {
    const actual = Number(stats[key].pct.toFixed(2));
    const required = thresholds[key];
    if (actual < required) {
      failures.push(`${label} ${key}: ${actual}% < ${required}%`);
    }
  }
  return failures;
}

let failures = [];

if (!summary.total) {
  console.error('[coverage] Formato invalido: falta resumen total.');
  process.exit(1);
}

failures = failures.concat(assertThresholds('global', summary.total, GLOBAL_THRESHOLDS));

const entries = Object.entries(summary).filter(([k]) => k !== 'total');

for (const moduleRule of MODULE_THRESHOLDS) {
  const acc = {
    statements: { covered: 0, total: 0 },
    branches: { covered: 0, total: 0 },
    functions: { covered: 0, total: 0 },
    lines: { covered: 0, total: 0 },
  };

  for (const [key, value] of entries) {
    const normalized = normalizeKey(key);
    const matches = moduleRule.include.some((pattern) => normalized.includes(pattern));
    if (!matches) continue;

    for (const metric of ['statements', 'branches', 'functions', 'lines']) {
      acc[metric].covered += value[metric].covered;
      acc[metric].total += value[metric].total;
    }
  }

  const stats = {
    statements: { pct: ratio(acc.statements.covered, acc.statements.total) },
    branches: { pct: ratio(acc.branches.covered, acc.branches.total) },
    functions: { pct: ratio(acc.functions.covered, acc.functions.total) },
    lines: { pct: ratio(acc.lines.covered, acc.lines.total) },
  };

  failures = failures.concat(assertThresholds(moduleRule.name, stats, moduleRule.thresholds));

  console.log(
    `[coverage] ${moduleRule.name} => lines=${stats.lines.pct.toFixed(2)}% branches=${stats.branches.pct.toFixed(2)}% functions=${stats.functions.pct.toFixed(2)}% statements=${stats.statements.pct.toFixed(2)}%`
  );
}

console.log(
  `[coverage] global => lines=${summary.total.lines.pct.toFixed(2)}% branches=${summary.total.branches.pct.toFixed(2)}% functions=${summary.total.functions.pct.toFixed(2)}% statements=${summary.total.statements.pct.toFixed(2)}%`
);

if (failures.length > 0) {
  console.error('[coverage] Fallaron umbrales:');
  for (const failure of failures) {
    console.error(`- ${failure}`);
  }
  process.exit(1);
}

console.log('[coverage] OK: umbrales globales y por modulo cumplidos.');
