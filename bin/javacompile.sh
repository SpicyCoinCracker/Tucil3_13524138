set -eu
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SRC="$ROOT/src"
OUT="$ROOT/bin/classes"
mkdir -p "$OUT"



n="$(find "$SRC" -maxdepth 1 -type f \( -name '*.java' ! -path '*/.*' \) -print | wc -l)"
n="$(echo "$n" | tr -d '[:space:]')"
if [ "${n:-0}" -eq 0 ]; then
  echo "No .java sources under $SRC" >&2
  exit 1
fi

find "$SRC" -maxdepth 1 -type f \( -name '*.java' ! -path '*/.*' \) \
  -exec javac --release 17 -encoding UTF-8 -d "$OUT" {} +
