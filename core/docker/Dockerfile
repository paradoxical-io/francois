 FROM alpine:3.2

 RUN echo "http://dl-4.alpinelinux.org/alpine/edge/community" >> /etc/apk/repositories \
     && apk add --update openjdk8-jre-base \
     && rm /var/cache/apk/*

ADD data /data

# set the service to run
ENTRYPOINT ["/data/bin/francois"]