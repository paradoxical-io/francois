FROM alpine:3.3

RUN apk --no-cache add openjdk8  &&\
    rm -rf /tmp/*

ADD data /data

# set the service to run
ENTRYPOINT ["/data/bin/francois"]