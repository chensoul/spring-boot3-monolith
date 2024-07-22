# syntax=docker/dockerfile:1

# https://docs.docker.com/reference/dockerfile/
# https://docs.docker.com/build/guide/multi-stage/

FROM eclipse-temurin:21-jre-jammy AS extract
WORKDIR /build
COPY target/*.jar target/app.jar
RUN java -Djarmode=layertools -jar target/app.jar extract --destination target/extracted

FROM eclipse-temurin:21-jre-jammy AS final
WORKDIR /app
# See https://docs.docker.com/go/dockerfile-user-best-practices/
ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser
USER appuser
COPY --from=extract /build/target/extracted/dependencies/ ./
COPY --from=extract /build/target/extracted/spring-boot-loader/ ./
COPY --from=extract /build/target/extracted/snapshot-dependencies/ ./
COPY --from=extract /build/target/extracted/application/ ./
EXPOSE 8080
ENTRYPOINT [ "java",  "org.springframework.boot.loader.launch.JarLauncher" ]
