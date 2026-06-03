#!/bin/bash
# ============================================================
# setup-cron-backups.sh ??? Programa les c??pies autom??tiques
# ============================================================
# Configura cron per executar:
#   - C??pia completa: cada diumenge a les 02:00
#   - C??pia incremental: cada dia a les 02:00 (de dilluns a dissabte)
# ============================================================

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Donar permisos d'execuci?? als scripts
chmod +x "$SCRIPT_DIR/backup-full.sh"
chmod +x "$SCRIPT_DIR/backup-incremental.sh"
chmod +x "$SCRIPT_DIR/restore.sh"

# Afegir les tasques a cron (sudo perqu?? necessita docker)
(crontab -l 2>/dev/null; echo "") | crontab -

# C??pia completa: diumenge a les 02:00
(crontab -l 2>/dev/null; echo "0 2 * * 0 $SCRIPT_DIR/backup-full.sh") | crontab -

# C??pia incremental: dilluns a dissabte a les 02:00
(crontab -l 2>/dev/null; echo "0 2 * * 1-6 $SCRIPT_DIR/backup-incremental.sh") | crontab -

echo "??? Tasques programades a cron:"
crontab -l

exit 0
