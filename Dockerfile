FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
ENV GRADLE_VERSION=9.5.1
RUN apt-get update \
    && apt-get install -y --no-install-recommends wget unzip \
    && wget -q https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
    && unzip gradle-${GRADLE_VERSION}-bin.zip -d /opt \
    && ln -s /opt/gradle-${GRADLE_VERSION}/bin/gradle /usr/bin/gradle \
    && rm gradle-${GRADLE_VERSION}-bin.zip \
    && rm -rf /var/lib/apt/lists/*
COPY settings.gradle build.gradle ./
COPY src ./src
RUN gradle bootJar --no-daemon -x test \
    && find build/libs -name '*.jar' ! -name '*-plain.jar' -exec cp {} /app/application.jar \;

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/application.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
