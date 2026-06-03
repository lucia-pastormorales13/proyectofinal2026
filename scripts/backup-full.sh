#!/bin/bash
# ============================================================
# backup-full.sh ??? C??pia de seguretat COMPLETA de la BD MySQL
# ============================================================
# ??s: sudo ./scripts/backup-full.sh
# Genera un fitxer .sql a /backups/full/ amb la data actual
# ============================================================

# ---------- CONFIGURACI?? ----------
MYSQL_CONTAINER="bumeran-mysql"          # Nom del contenidor MySQL
MYSQL_USER="root"
MYSQL_PASSWORD="toor"
MYSQL_DATABASE="bumeran"                  # Base de dades a respatllar
BACKUP_DIR="/backups/full"                # Directori on es guarden les c??pies
RETENTION_DAYS=30                         # Dies que es conserven les c??pies
# -----------------------------------

# 1. Crear el directori de backups amb permisos segurs
sudo mkdir -p "$BACKUP_DIR"
sudo chmod 750 "$BACKUP_DIR"
sudo chown root:root "$BACKUP_DIR"

# 2. Nom del fitxer: bumeran-YYYY-MM-DD-HHMMSS.sql
TIMESTAMP=$(date +"%Y-%m-%d-%H%M%S")
FILENAME="bumeran-full-${TIMESTAMP}.sql"
FILEPATH="${BACKUP_DIR}/${FILENAME}"

# 3. Fer el dump complet de la base de dades
echo "[$(date)] Iniciant c??pia completa: $FILENAME ..."
sudo docker exec "$MYSQL_CONTAINER" \
    mysqldump -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" \
    --single-transaction \
    --routines \
    --triggers \
    --events \
    --databases "$MYSQL_DATABASE" \
    > "$FILEPATH"

# 4. Comprovar si el dump s'ha fet correctament
if [ $? -eq 0 ] && [ -s "$FILEPATH" ]; then
    echo "[$(date)] ??? C??pia completa realitzada: $FILEPATH ($(du -h "$FILEPATH" | cut -f1))"
else
    echo "[$(date)] ??? ERROR: La c??pia completa ha fallat"
    rm -f "$FILEPATH"
    exit 1
fi

# 5. Xifrar la c??pia amb GPG (protecci?? de dades)
gpg --batch --yes --passphrase "BumeranBackup2026" \
    --symmetric --cipher-algo AES256 "$FILEPATH"
if [ $? -eq 0 ]; then
    rm -f "$FILEPATH"  # Eliminem el fitxer sense xifrar
    FILEPATH="${FILEPATH}.gpg"
    echo "[$(date)] ??? C??pia xifrada: $FILEPATH"
fi

# 6. Eliminar c??pies m??s antigues de RETENTION_DAYS dies
find "$BACKUP_DIR" -name "bumeran-full-*.gpg" -type f -mtime +$RETENTION_DAYS -delete
echo "[$(date)] ??? Neteja completada (c??pies > ${RETENTION_DAYS} dies eliminades)"

# 7. Registrar al log del sistema
logger -t backup-mysql "C??pia completa: $FILENAME ($(du -h "$FILEPATH" | cut -f1))"

exit 0
