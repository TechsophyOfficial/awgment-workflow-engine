FROM adoptopenjdk/openjdk11
RUN addgroup spring && adduser  --ingroup spring --disabled-password spring
USER 10001
WORKDIR /home/spring/app
ARG JAR_FILE=build/libs/*.jar
ARG TELEMETRY=opentelemetry-javaagent-all.jar
COPY --chown=spring ${JAR_FILE} /home/spring/app/bin/tp-workflow-engine-1.2.0.jar
COPY --chown=spring ${TELEMETRY} /home/spring/app/bin/opentelemetry-javaagent-all.jar
ENTRYPOINT ["java", "-Dotel.exporter=jaeger", "-Dotel.exporter.jaeger.endpoint=http://jaeger:14250", "-Dotel.exporter.jaeger.service.name=workflow-engine", "-Dapplication.name=tp-workflow-engine", "-javaagent:/home/spring/app/bin/opentelemetry-javaagent-all.jar", "-jar","/home/spring/app/bin/tp-workflow-engine-1.2.0.jar"]
EXPOSE 8080

