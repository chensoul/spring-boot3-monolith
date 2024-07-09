docker exec -i docker-postgres-1 /bin/bash -c "PGPASSWORD=postgres pg_dump --username postgres monolith" > dump/monolith-dump.sql
