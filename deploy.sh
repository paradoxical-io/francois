#!/usr/bin/env bash

if [ "${IS_SECURE_BUILD}" ]; then

    if [ -n "$TRAVIS_TAG" ]; then
        mvn clean deploy --settings settings.xml -DskipTests -P release -Drevision=''
    elif [ "$TRAVIS_BRANCH" = "master" ]; then
        mvn clean deploy --settings settings.xml -DskipTests
    fi

  SHA_SHORT=`git rev-parse --short HEAD`

  if [ ! -z "${DOCKER_EMAIL}" ]; then

      docker tag -f paradoxical/francois:${SHA_SHORT}_dev paradoxical/francois

      if [ -n "$TRAVIS_TAG" ]; then
        docker tag -f paradoxical/francois:${SHA_SHORT}_dev paradoxical/francois:${TRAVIS_TAG}
      fi

      docker login -e ${DOCKER_EMAIL} -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}

      docker push paradoxical/francois:${SHA_SHORT}_dev

      if [ -n "$TRAVIS_TAG" ]; then
        docker push paradoxical/francois:${TRAVIS_TAG}
      fi

      docker push paradoxical/francois

  fi
fi