#!/bin/bash
# runs the suite through the offline Katalon Runtime Engine on this machine
# (the katalon wrapper in ~/.local/bin starts Studio's console mode),
# one JVM at a time, log to ~/katalon_cli_runs/, verdict printed at the end
cd "$(dirname "$0")"

SUITE="${1:-Test Suites/api/api_smoke}"
PROFILE="${PROFILE:-default}"
mkdir -p ~/katalon_cli_runs
TS=$(date +%Y-%m-%d_%H-%M-%S)
LOG="$HOME/katalon_cli_runs/api-$TS.log"

echo "running $SUITE -> $LOG"
# optional live progress watcher, point KATALON_WATCH at any script that takes the log path
WPID=""
if [ -x "${KATALON_WATCH:-}" ]; then "$KATALON_WATCH" "$LOG" & WPID=$!; fi
"${KATALON_BIN:-$HOME/.local/bin/katalon}" "$(pwd)" \
    -testSuitePath="$SUITE" \
    -executionProfile="$PROFILE" \
    -browserType=Chrome \
    -retry=0 -data ~/.katalon_console/ws-api > "$LOG" 2>&1
RC=$?
[ -n "$WPID" ] && kill $WPID 2>/dev/null

echo "--- summary ---"
grep -E "Test Suites:|Test Cases:|FAILED|ERROR" "$LOG" | tail -8
if [ $RC -eq 0 ]; then
    echo "GREEN: katalonc exited 0, report in Reports/"
else
    echo "RED: katalonc exit $RC, full log: $LOG"
fi
exit $RC
