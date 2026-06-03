#!/bin/bash
# ============================================================
# backup-incremental.sh ??? C??pia de seguretat INCREMENTAL
# ============================================================
# ??s: sudo ./scripts/backup-incremental.sh
# 
# Estrat??gia:
#   - La c??pia completa es fa cada diumenge (full)
#   - Les c??pies incrementals es fan di??riament
#   - Utilitzem els binary logs de MySQL per al backup incremental
# ============================================================

# ---------- CONFIGURACI?? ----------
MYSQL_CONTAINER="bumeran-mysql"
MYSQL_USER="root"
MYSQL_PASSWORD="toor"
BACKUP_DIR_INCREMENTAL="/backups/incremental"
BACKUP_DIR_FULL="/backups/full"
RETENTION_DAYS=15
# -----------------------------------

sudo mkdir -p "$BACKUP_DIR_INCREMENTAL"
sudo chmod 750 "$BACKUP_DIR_INCREMENTAL"
sudo chown root:root "$BACKUP_DIR_INCREMENTAL"

TIMESTAMP=$(date +"%Y-%m-%d-%H%M%S")

# 1. Comprovar si els binary logs estan activats
BINLOG_STATUS=$(sudo docker exec "$MYSQL_CONTAINER" \
    mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "SHOW VARIABLES LIKE 'log_bin';" 2>/dev/null \
    | grep -c "ON")

if [ "$BINLOG_STATUS" -eq 0 ]; then
    echo "[$(date)] ??? Els binary logs NO estan activats. Activant-los..."
    # En Docker, cal afegir --log-bin al my.cnf
    echo "[$(date)] Per activar-los: afegir 'log-bin=mysql-bin' al fitxer de configuraci?? de MySQL"
    echo "[$(date)] Es far?? una c??pia completa alternativa..."
    
    # Fallback: copia completa dels fitxers de dades
    sudo docker exec "$MYSQL_CONTAINER" \
        mysqldump -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" \
        --single-transaction --all-databases \
        > "${BACKUP_DIR_INCREMENTAL}/bumeran-incremental-${TIMESTAMP}.sql"
else
    # 2. Obtenir el nom del binary log actual
    MASTER_STATUS=$(sudo docker exec "$MYSQL_CONTAINER" \
        mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "SHOW MASTER STATUS;" 2>/dev/null)
    
    # 3. Copiar els binary logs nous
    echo "[$(date)] Binary logs actius. Copiant logs incrementals..."
    sudo docker exec "$MYSQL_CONTAINER" \
        mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "FLUSH LOGS;" 2>/dev/null
    
    sudo docker cp "$MYSQL_CONTAINER":/var/lib/mysql/mysql-bin.* "$BACKUP_DIR_INCREMENTAL/" 2>/dev/null
    
    # 4. Registrar la posici?? actual
    sudo docker exec "$MYSQL_CONTAINER" \
        mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "SHOW MASTER STATUS;" \
        > "${BACKUP_DIR_INCREMENTAL}/master-status-${TIMESTAMP}.txt" 2>/dev/null
fi

# 5. Protegir els fitxers
sudo chmod 640 "$BACKUP_DIR_INCREMENTAL"/*.sql "$BACKUP_DIR_INCREMENTAL"/*.txt "$BACKUP_DIR_INCREMENTAL"/*.bin 2>/dev/null
sudo chown root:root "$BACKUP_DIR_INCREMENTAL"/* 2>/dev/null

# 6. Neteja
find "$BACKUP_DIR_INCREMENTAL" -name "bumeran-incremental-*.sql" -type f -mtime +$RETENTION_DAYS -delete
find "$BACKUP_DIR_INCREMENTAL" -name "mysql-bin.*" -type f -mtime +$RETENTION_DAYS -delete

echo "[$(date)] ??? C??pia incremental completada"
logger -t backup-mysql "C??pia incremental: ${BACKUP_DIR_INCREMENTAL}"

exit 0
