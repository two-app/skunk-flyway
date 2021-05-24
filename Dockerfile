FROM eed3si9n/sbt:jdk11-alpine

# Update & Download Dependencies
RUN ["sbt", "update"]

SHELL ["/bin/sh"]
