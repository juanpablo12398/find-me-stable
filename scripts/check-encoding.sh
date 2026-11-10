#!/usr/bin/env bash
set -euo pipefail

patterns=(
  '*.java' '*.js' '*.jsx' '*.mjs' '*.cjs' '*.ts' '*.tsx'
  '*.json' '*.md' '*.yml' '*.yaml' '*.properties' '*.xml'
  '*.sql' '*.sh' '*.css' '*.html' '*.txt' '*.toml' '*.conf'
  '.gitignore' '.editorconfig' 'Dockerfile*' 'mvnw' 'mvnw.cmd'
)

mapfile -t files < <(git ls-files "${patterns[@]}")

failed=0
for f in "${files[@]}"; do
  [ -f "$f" ] || continue

  case "$f" in
    target/*|frontend/dist/*|frontend/coverage/*|frontend/node_modules/*)
      continue
      ;;
  esac

  if [ "$(xxd -p -l 3 "$f" 2>/dev/null || true)" = "efbbbf" ]; then
    echo "[encoding] BOM detectado: $f"
    failed=1
  fi

  if ! iconv -f UTF-8 -t UTF-8 "$f" >/dev/null 2>&1; then
    echo "[encoding] No es UTF-8 valido: $f"
    failed=1
  fi
done

if [ "$failed" -ne 0 ]; then
  echo "[encoding] Fallo la validacion de encoding (UTF-8 sin BOM)."
  exit 1
fi

echo "[encoding] OK: UTF-8 sin BOM"