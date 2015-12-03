#!/usr/bin/env bash

if [ "$TRAVIS_BRANCH" == "master" ]; then
  mvn clean deploy --settings settings.xml -DskipTests

  SHA_SHORT=`git rev-parse --short HEAD`

  if [ ! -z "${DOCKER_EMAIL}" ]; then

      docker tag -f paradoxical/francois:${SHA_SHORT}_dev paradoxical/francois

      docker login -e ${DOCKER_EMAIL} -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}
      docker push paradoxical/francois:${SHA_SHORT}_dev
      docker push paradoxical/francois
  fi
fi