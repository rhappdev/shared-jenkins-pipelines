# Shared pipeline templates

Pipeline templates using [Shared Libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/) from [Jenkins Pipeline Plugin](https://wiki.jenkins.io/display/JENKINS/Pipeline+Plugin)

## Setup

1. Manage Jenkins
2. Configure System
3. In section Global Pipeline Libraries, add the following
   - Click "Add" button.
   - Name: pipelines
   - Default verison: master
   - Retrieval Method: Modern SCM
   - Click Git
   - Project Repository: <your git repo>
   - Credentials: <if needed add your credentials to access the repo>
   - Click "Save" button.

## Project Examples
 - Node.js
 - Springboot
 
### Dependencies

This templates requires the following plugins to work:

* [Openshift Client Plugin] - Manages openshift cluster.
* [Config File Provider Plugin] - Manage config files (maven credentials and npm token).
* [Git Plugin] - Manage git repository.

## TODO
 - [] Add more configuration setup (secrets, configmap files,...)
 - [] New deployment strategies (blue-green)
 - [] More integration test strategies

## Thanks to
[David Sancho Ruiz](https://es.linkedin.com/in/dsanchoruiz)

   [Openshift Client Plugin]: <https://github.com/openshift/jenkins-client-plugin>
   [Config File Provider Plugin]: <https://wiki.jenkins.io/display/JENKINS/Config+File+Provider+Plugin>
   [Git Plugin]: <https://wiki.jenkins.io/display/JENKINS/Git+Plugin>