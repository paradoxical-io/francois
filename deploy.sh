#!/usr/bin/env bash

set -x

if [ "$TRAVIS_BRANCH" == "master" ]; then
  mvn clean deploy --settings settings.xml -DskipTests

  if [ ! -z "${DOCKER_EMAIL}" ]; then

      docker tag -f paradoxical/francois:${TRAVIS_COMMIT}_dev paradoxical/francois

      docker login -e ${DOCKER_EMAIL} -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}
      docker push paradoxical/francois:${TRAVIS_COMMIT}_dev
      docker push paradoxical/francois
  fi
fi