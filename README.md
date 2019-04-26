# Shared pipeline templates

Pipeline templates using [Shared Libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/) from [Jenkins Pipeline Plugin](https://wiki.jenkins.io/display/JENKINS/Pipeline+Plugin)

## Index 

- [Shared pipeline templates](#shared-pipeline-templates)
  * [About](#about)
  * [Sections](#sections)
    + [Setup](sections/setup.md)
    + [Architecture](sections/architecture.md)
  * [Project Examples](#project-examples)
  * [Dependencies](#dependencies)
  * [TODO](#todo)
  * [Thanks to](#thanks-to)

## About
We are going to cover and explain the pipeline templates made by the Application Development Center of Excellence.
The pipelines are used with the Jenkins PiPipelines are going to be used with [Shared Libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/). This allows to reuse the pipelines across projects and have them controlled in our git repository.
The following sections have all the information or steps that are required to launch the pipelines and explain what does each of the templates.
The shared pipeline templates can bee seen as a small pieces that can be used all together to achieve the CI/CD process in Openshift.

## Sections
- [Setup](sections/setup.md)
- [Architecture](sections/architecture.md)
- [Create Custom Jenkins image in Openshift](https://github.com/rhappdev/custom-jenkins-image)

## Project Examples
 - [Springboot](https://github.com/rhappdev/springboot-template)
 - [Node.js](https://github.com/rhappdev/nodejs-template)

 
## Dependencies
This templates requires the following plugins to work:

* [Openshift Client Plugin] - Manages openshift cluster.
* [Config File Provider Plugin] - Manage config files (maven credentials and npm token).
* [Git Plugin] - Manage git repository.

## TODO
 - [-] Add more configuration setup (secrets, configmap files,...)
 - [X] New deployment strategies (blue-green)
 - [-] More integration test strategies

## Thanks to
[David Sancho Ruiz](https://es.linkedin.com/in/dsanchoruiz)

   [Openshift Client Plugin]: <https://github.com/openshift/jenkins-client-plugin>
   [Config File Provider Plugin]: <https://wiki.jenkins.io/display/JENKINS/Config+File+Provider+Plugin>
   [Git Plugin]: <https://wiki.jenkins.io/display/JENKINS/Git+Plugin>
   

