# ===== Etapa 1: compilacion con Maven =====
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace

# Se copian primero los pom.xml para aprovechar la cache de dependencias
COPY pom.xml ./
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
COPY user-service/pom.xml user-service/pom.xml
RUN mvn -B -ntp -pl auth-service -am dependency:go-offline

# Codigo fuente y empaquetado
COPY auth-service/src auth-service/src
RUN mvn -B -ntp -pl auth-service -am clean package -DskipTests

# ===== Etapa 2: imagen de ejecucion liviana (solo JRE) =====
FROM eclipse-temurin:21-jre
WORKDIR /app
# Usuario sin privilegios (uid 1001) - minimo privilegio
RUN useradd --system --uid 1001 spring
COPY --from=build /workspace/auth-service/target/*.jar app.jar
USER spring
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
