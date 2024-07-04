docker exec -i docker-postgres-1 /bin/bash -c "PGPASSWORD=postgres pg_dump --username postgres spring-boot3-monolith" > dump/spring-boot3-monolith-dump.sql
