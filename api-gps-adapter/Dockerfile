FROM adoptopenjdk/openjdk11:jdk-11.0.2.9-slim

ARG appName
ARG version

RUN useradd --create-home --uid 1001 runtime-user
USER runtime-user
WORKDIR /home/runtime-user/
COPY ./build/libs/$appName-$version.jar /home/runtime-user/app.jar
COPY ./deploy /home/runtime-user/deploy
COPY ./cfg /home/runtime-user/cfg
ENV JAVA_OPTS "-XX:+UseContainerSupport"
ENV ADDED_JAVA_OPTS ""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS $ADDED_JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /home/runtime-user/app.jar"]
