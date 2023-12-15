FROM --platform=linux/amd64 gradle:8.5.0-jdk21-alpine

ARG USER_API_PORT
ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD

ENV USER_API_PORT=$USER_API_PORT
ENV DB_URL=$DB_URL
ENV DB_USERNAME=$DB_USERNAME
ENV DB_PASSWORD=$DB_PASSWORD

WORKDIR todolist-user-api

COPY . .

RUN ./gradlew build

ENTRYPOINT ["./gradlew","bootRun"]