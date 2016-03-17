# io.paradoxical.francois

[![Build Status](https://travis-ci.org/paradoxical-io/francois.svg?branch=master)](https://travis-ci.org/paradoxical-io/francois)

François is a dropwizard.io api and web application for templatizing jenkins jobs. François is available as a docker image: https://hub.docker.com/r/paradoxical/francois/

All templates are stored and tracked using jenkins views, so no external database is required.

### Using the docker image

Run the docker image with:

```
docker run -it \
    -e JENKINS_URL='http://jenkins.paradoxical.io/' \
    -e JENKINS_USER=francois \
    -e JENKINS_TOKEN=USER_JENKINS_TOKEN \
    -p 9090:9090 \ 
    -p 9099:9099 \ 
    -v `pwd`/logs/core:/data/logs \
    paradoxical/francois
```

The app deploys to the port `9090` with its dropwizard admin port on `9099`.

### Walkthrough

Lets get started!

First, lets create a template by clicking "new template" in the top nav bar:

![Create a template](/images/create_template_francois.png)

This will create a template jenkins job that is in the `Templates` view.

![Template in jenkins](/images/templates_view_jenkins.png)

Now we can edit our template and add in template fields on jenkins. Notice how we're creating a template of:

```
{% Build.Command | foo %}
```

Which says that we want there to be a template value of "Build.Command" with a default value of "foo".

![Editing a template](/images/jenkins_template_sample.png)

If we go back to francois and hit refresh, we should see our templated fields, and you can see that `Build.Command` is picked up a `foo`.

![Francois fields](/images/francois_template_sample.png)

From here we can create jobs off our template. Again, notice how the default value of `foo` is printed next to the template value, but 
we can override it with `bar`

![Creating job](/images/creating_job.png)

Notice how it lets us override the default value of a template, or use the default value.  Since we override the value it shows us the overriden value.

Had we used the default value, the parameter list would be empty. Francois only shows you things that are overriden.

If we hit save, our job will create and we can see it in francois

![Created job](/images/created_job.png)

And we should see it in jenkins

![Created jenkins](/images/created_jenkins_job.png)

If we want to see what our actual job looks like we can see our templated values actually applied!  Notice the value in the `execute shell` block

![Jenkins config](/images/configured_job_jenkins.png)


### Creating templates
 
Create a template that will serve as a template for other jobs.  Templatable fields can be added _anywhere_ in the jenkins config 
and are of the form

```
{% TemplateName | defaultValue %}
```

For example:

```
echo {% EchoParam %}
```

Creates a template that has no default value. However,

```
echo {% EchoParam | foo %}
```

Will use `foo` if there is no supplied parameter.

Templates are stored in a view called `Templates` and jobs that are created from templates are stored in a view called `Templatized`.

Templated values are stored in the jenkins jobs description, so they can be reapplied later!

Template parameters can be nested with dots, so you can create:

```
echo {% Build.Properties.Names %}
```

As well.


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
