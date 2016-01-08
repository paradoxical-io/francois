# io.paradoxical.francois

[![Build Status](https://travis-ci.org/paradoxical-io/francois.svg?branch=master)](https://travis-ci.org/paradoxical-io/francois)

François is a dropwizard.io api and web application for templatizing jenkins jobs. François is available as a docker image: https://hub.docker.com/r/paradoxical/francois/

The app deploys to the port `9090` with its dropwizard admin port on `9099`.

All templates are stored and tracked using jenkins views, so no external database is required.

### Configuration
Francois gets its running configuration from either its configuration yaml file (configuration.yml) or the environment.

The following environment variables are avaiable

- `JENKINS_URL`: this should point to your jenkins machine. example: `http://jenkins.jakeswenson.github.com/`
- `JENKINS_USER`: this is the user created to manage jenkins and create jobs. example: `francois`
- `JENKINS_TOKEN`: this should be the user token for that jenkins user above. You can get this from the users jenkins configuration page.

Francois useses two views to manage all available tempaltes and templatized jobs:

- `Templates` - this is used to get a list of templates
- `Templatized` - this is used to track all jobs created from templates

so be sure create them in your jenkins instance.

It doesn't matter what type of view they are, but it's simplest to create list views.

### Using the docker image

Run the docker image with:

```
docker run -it \
    -e JENKINS_URL='http://jenkins.jakeswenson.github.com/' \
    -e JENKINS_USER=francois \
    -e JENKINS_TOKEN=USER_JENKINS_TOKEN \
    -p 9090:9090 \
    -p 9099:9099 \
    -v `pwd`/logs/core:/data/logs \
    paradoxical/francois
```

# Adding a template
Adding templates are easy, simply create a job in jenkins and add it to the `Templates` view.

Variables are do using `{% %}` tags. example: `{% Git.Repo %}`.
You can also supply a default value in a variable using `|`. example: `{% Git.UserName | jakeswenson %}`

## Building your own francois docker container

Builds are done using maven and maven docker plugin. Use:

```
mvn clean package

./scripts/run-core.sh
```

## Making changes to the UI

François UI is built with Angular2 and typescript. make sure you have typscript installed:
```
sudo npm install --global typescript
```

and then install the dependencies:

```
npm install
```

Please file any issues you might find. Enjoy!
