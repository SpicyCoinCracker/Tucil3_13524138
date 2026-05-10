ROOT="$(cd "$(dirname "$0")/.." && pwd)"
CP="$ROOT/bin/classes"
if ! test -f "$CP/MainCli.class"; then
  sh "$ROOT/bin/javacompile.sh"
fi
exec java -cp "$CP" MainCli "$@"
