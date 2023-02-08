FROM adoptopenjdk/openjdk11:jdk-11.0.2.9-slim

ARG appName
ARG version

RUN useradd --create-home --uid 1001 runtime-user
USER runtime-user
WORKDIR /home/runtime-user/
COPY ./build/libs/$appName-$version.jar /home/runtime-user/app.jar
ENV JAVA_OPTS "-XX:+UseContainerSupport"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /home/runtime-user/app.jar"]