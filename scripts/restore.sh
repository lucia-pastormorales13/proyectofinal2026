#!/bin/bash
# ============================================================
# restore.sh ??? Procediment de RECUPERACI?? de la base de dades
# ============================================================
# ??s: sudo ./scripts/restore.sh [fitxer.sql]
#
# Exemples:
#   sudo ./scripts/restore.sh /backups/full/bumeran-full-2026-06-01-020000.sql
#   sudo ./scripts/restore.sh /backups/full/bumeran-full-2026-06-01-020000.sql.gpg (xifrat)
# ============================================================

# ---------- CONFIGURACI?? ----------
MYSQL_CONTAINER="bumeran-mysql"
MYSQL_USER="root"
MYSQL_PASSWORD="toor"
MYSQL_DATABASE="bumeran"
# -----------------------------------

set -e  # Aturar en cas d'error

# 1. Validar arguments
if [ $# -lt 1 ]; then
    echo "??s: sudo $0 <fitxer_dump.sql>"
    echo "Exemple: sudo $0 /backups/full/bumeran-full-2026-06-01-020000.sql"
    exit 1
fi

DUMP_FILE="$1"
DECRYPTED_FILE=""

# 2. Comprovar que el fitxer existeix
if [ ! -f "$DUMP_FILE" ]; then
    echo "??? ERROR: El fitxer $DUMP_FILE no existeix"
    exit 1
fi

# 3. Si est?? xifrat amb GPG, el desxifrem
if [[ "$DUMP_FILE" == *.gpg ]]; then
    echo "[$(date)] Desxifrant el fitxer..."
    DECRYPTED_FILE="${DUMP_FILE%.gpg}"
    gpg --batch --yes --passphrase "BumeranBackup2026" \
        --decrypt "$DUMP_FILE" > "$DECRYPTED_FILE"
    if [ $? -ne 0 ]; then
        echo "??? ERROR: No s'ha pogut desxifrar el fitxer (contrasenya incorrecta?)"
        exit 1
    fi
    echo "??? Fitxer desxifrat: $DECRYPTED_FILE"
    DUMP_FILE="$DECRYPTED_FILE"
fi

# 4. CONFIRMAR l'operaci?? (seguretat)
echo ""
echo "??? ATENCI??: Esborrarem la base de dades '$MYSQL_DATABASE' i la RESTAURAREM"
echo "   Fitxer: $DUMP_FILE ($(du -h "$DUMP_FILE" | cut -f1))"
echo ""
read -p "Est??s segur? (escriu 'CONFIRM' per continuar): " confirmation
if [ "$confirmation" != "CONFIRM" ]; then
    echo "Operaci?? cancel??lada."
    [ -n "$DECRYPTED_FILE" ] && rm -f "$DECRYPTED_FILE"
    exit 0
fi

# 5. Importar el dump a MySQL
echo "[$(date)] Restaurant la base de dades..."
echo "   Eliminant base de dades existent..."
sudo docker exec -i "$MYSQL_CONTAINER" \
    mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "DROP DATABASE IF EXISTS \`$MYSQL_DATABASE\`; CREATE DATABASE \`$MYSQL_DATABASE\`;" 2>/dev/null

echo "   Important el dump..."
sudo docker exec -i "$MYSQL_CONTAINER" \
    mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" 2>/dev/null < "$DUMP_FILE"

# 6. Comprovar resultat
if [ $? -eq 0 ]; then
    echo "[$(date)] ??? RESTAURACI?? COMPLETADA correctament"
    # Obtenir recompte de taules
    TABLE_COUNT=$(sudo docker exec "$MYSQL_CONTAINER" \
        mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='$MYSQL_DATABASE';" 2>/dev/null | tail -1)
    echo "   Taules restaurades: $TABLE_COUNT"
else
    echo "[$(date)] ??? ERROR: La restauraci?? ha fallat"
    exit 1
fi

# 7. Neteja
[ -n "$DECRYPTED_FILE" ] && rm -f "$DECRYPTED_FILE"

# 8. Reintentar l'aplicaci?? (Spring Boot es reconnecta sola, per?? podem for??ar)
echo "[$(date)] Reintant el contenidor de l'aplicaci??..."
sudo docker restart bumeran-app 2>/dev/null || true

echo "[$(date)] ??? Proc??s finalitzat. La web hauria de funcionar correctament."
logger -t restore-mysql "Restauraci?? completada des de: $(basename "$DUMP_FILE")"

exit 0
