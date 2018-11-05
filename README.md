# Shared pipeline templates

Pipeline templates using [Shared Libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/) from [Jenkins Pipeline Plugin](https://wiki.jenkins.io/display/JENKINS/Pipeline+Plugin)

## About
We are going to cover and explain the pipeline templates made by the Application Development Center of Excellence.
The pipelines are used with the Jenkins PiPipelines are going to be used with [Shared Libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/). This allows to reuse the pipelines across projects and have them controlled in our git repository.
The following sections have all the information or steps that are required to launch the pipelines and explain what does each of the templates.
The shared pipeline templates can bee seen as a small pieces that can be used all together to achieve the CI/CD process in Openshift.

## Sections
- Setup
- Architecture

### Architecture
#### Pipeline Definition
The pipelines have been developed to be reusable across them. openshiftCD will be composed of the following pipeline templates:
![Full Pipeline](https://github.com/rhappdev/assets/blob/master/architecture/full_pipeline.png)

##### javaFullPipeline

| Name          | Description   | Example |  Required |
| ------------- |:-------------:| -------| :-------: |
| appName      | Application name | springboot | Yes |
| gitUrl      | Application git URL      |   https://github.com/rhappdev/springboot-template.git | Yes |
| gitBranch | Desired branch to checkout      |   master| Yes |
| gitCredentials      | Jenkins credential id needed to access the repository | gitlab-credentials | Yes |
| buildProject      | Openshift namespace | test | Yes |
| baseImage      | Base image name used to create the docker image | redhat-openjdk8-openshift:1.2 | Yes |
| buildTag      | Build Tag | latest | Yes |
| deployTag      | Deploy Tag to be used in the deployment config | test | Yes |
| testStrategy      | Integration test strategy to be used | postman | Optional |
| uatProject      | namespace name in UAT | uat | Only master branch |
| prodProject      | namespace name in prod | prod | Only master branch |

##### npmFullPipeline

| Name          | Description   | Example |  Required |
| ------------- |:-------------:| -------| :-------: |
| appName      | Application name | nodejs | Yes |
| gitUrl      | Application git URL      |   https://github.com/rhappdev/nodejs-template.git | Yes |
| gitBranch | Desired branch to checkout      |   master| Yes |
| gitCredentials      | Jenkins credential id needed to access the repository | gitlab-credentials | Yes |
| buildProject      | Openshift namespace | test | Yes |
| baseImage      | Base image name used to create the docker image | nodejs:8 | Yes |
| buildTag      | Build Tag | latest | Yes |
| deployTag      | Deploy Tag to be used in the deployment config | test | Yes |
| testStrategy      | Integration test strategy to be used | postman | Optional |
| uatProject      | namespace name in UAT | uat | Only master branch |
| prodProject      | namespace name in prod | prod | Only master branch |

##### javaLibraryPipeline

| Name          | Description   | Example |  Required |
| ------------- |:-------------:| -------| :-------: |
| appName      | Application name | nodejs | Yes |
| gitUrl      | Application git URL      |   https://github.com/rhappdev/springboot-template.git | Yes |
| gitBranch | Desired branch to checkout      |   master| Yes |
| gitCredentials      | Jenkins credential id needed to access the repository | gitlab-credentials | Yes |

##### npmLibraryPipeline

| Name          | Description   | Example |  Required |
| ------------- |:-------------:| -------| :-------: |
| appName      | Application name | nodejs | Yes |
| gitUrl      | Application git URL      |   https://github.com/rhappdev/nodejs-template.git | Yes |
| gitBranch | Desired branch to checkout      |   master| Yes |
| gitCredentials      | Jenkins credential id needed to access the repository | gitlab-credentials | Yes |
| type      | Project Type | Library | Yes |
#### Templates
##### CI Pipeline
If we want to have a good way to administrate the pipelines, we need to store them in a git repository and try to define pipelines as templates, reusable between projects. This means that depending the languages that we are using, we are going to define the following CI pipelines:

###### mavenCI
Maven CI pipeline for Java projects.
![mavenCI](https://github.com/rhappdev/assets/blob/master/architecture/mavenCI.png)
| Name          | Description   | Example |  Required |
| ------------- |:-------------:| -------| :-------: |
| appName      | Application name | nodejs | Yes |
| gitUrl      | Application git URL      |   https://github.com/rhappdev/nodejs-template.git | Yes |
| gitBranch | Desired branch to checkout      |   master| Yes |
| gitCredentials      | Jenkins credential id needed to access the repository | gitlab-credentials | Yes |
| buildProject      | Openshift namespace | test | Optional |

###### npmCI
Javascript CI pipeline for javascript projects.
![mavenCI](https://github.com/rhappdev/assets/blob/master/architecture/npmCI.png)
| Name          | Description   | Example |  Required |
| ------------- |:-------------:| -------| :-------: |
| appName      | Application name | nodejs | Yes |
| gitUrl      | Application git URL      |   https://github.com/rhappdev/nodejs-template.git | Yes |
| gitBranch | Desired branch to checkout      |   master| Yes |
| gitCredentials      | Jenkins credential id needed to access the repository | gitlab-credentials | Yes |
| buildProject      | Openshift namespace | test | Optional |
| type      | App Type | library | Optional |

##### CD Pipeline
At this stage, we are going to run openshift, create images, push them to the registry and promote them between environments.
![openshiftCD Components](https://github.com/rhappdev/assets/blob/master/architecture/openshiftCD-components.png)
###### openshiftBuildAndDeploy
| Name          | Description   | Example |  Required |
| ------------- |:-------------:| -------| :-------: |
| appName      | Application name | nodejs | Yes |
| gitUrl      | Application git URL      |   https://github.com/rhappdev/nodejs-template.git | Yes |
| gitBranch | Desired branch to checkout      |   master | Yes |
| gitCredentials      | Jenkins credential id needed to access the repository | gitlab-credentials | Yes |
| buildProject      | Openshift namespace | test | Yes |
| baseImage      | Base image name used to create the docker image | nodejs:8 | Yes |
| buildTag      | Build Tag | latest | Yes |
| deployTag      | Deploy Tag to be used in the deployment config | test | Yes |
| testStrategy      | Integration test strategy to be used | postman | Optional |

###### setConfiguration
| Name          | Description   | Example |  Required |
| ------------- |:-------------:| -------| :-------: |
| appName      | Application name | nodejs | Yes |
| gitUrl      | Application git URL      |   https://github.com/rhappdev/nodejs-template.git | Yes |
| gitBranch | Desired branch to checkout      |   master | Yes |
| gitCredentials      | Jenkins credential id needed to access the repository | gitlab-credentials | Yes |
| buildProject      | Openshift namespace | test | Yes |
| environment      | Environment to set up, Valid: dev, test, uat and prod | test | Yes |

###### runTests
| Name          | Description   | Example |  Required |
| ------------- |:-------------:| -------| :-------: |
| gitUrl      | Application git URL      |   https://github.com/rhappdev/nodejs-template.git | Yes |
| gitBranch | Desired branch to checkout      |   master | Yes |
| gitCredentials      | Jenkins credential id needed to access the repository | gitlab-credentials | Yes |
| buildProject      | Openshift namespace | test | Yes |
| environment      | Environment to set up, Valid: dev, test, uat and prod | test | Yes |
| strategy      | Integration test strategy to use | postman | Optional |

###### openshiftPromoteAndDeploy
| Name          | Description   | Example |  Required |
| ------------- |:-------------:| -------| :-------: |
| appName      | Application name | nodejs | Yes |
| gitUrl      | Application git URL      |   https://github.com/rhappdev/nodejs-template.git | Yes |
| gitBranch | Desired branch to checkout      |   master | Yes |
| gitCredentials      | Jenkins credential id needed to access the repository | gitlab-credentials | Yes |
| buildProject      | Openshift namespace | test | Yes |
| baseImage      | Base image name used to create the docker image | nodejs:8 | Yes |
| buildTag      | Build Tag | latest | Yes |
| promotedProject      | Openshift promoted namespace | uat | Yes |
| promotedTag      | Promoted Tag name | uat | Yes |
| deployTag      | Deploy Tag to be used in the deployment config | test | Yes |
| testStrategy      | Integration test strategy to be used | postman | Optional |

### Setup
#### Jenkins
1. Manage Jenkins
![Manage Logo](https://github.com/rhappdev/assets/blob/master/shared_pipelines/manage_jenkins.png)
2. Configure System
![Configure Logo](https://github.com/rhappdev/assets/blob/master/shared_pipelines/configure_system.png)
3. In section Global Pipeline Libraries, add the following
![Shared Libraries](https://github.com/rhappdev/assets/blob/master/shared_pipelines/shared_libraries.png)
   - Click "Add" button.
   - Name: pipelines
   - Default verison: master
   - Retrieval Method: Modern SCM
   - Click Git
   - Project Repository: <your git repo>
   - Credentials: <if needed add your credentials to access the repo>
   - Click "Save" button.

### Openshift Pipeline Build
A Jenkins pipeline build is a build strategy that includes a mechanism to trigger a Jenkins build from within OpenShift via an API call. The build logic resides in Jenkins.
Using the Openshift CLI type the following:
```
oc new-build <repo#develop> --strategy=pipeline --name=springboot-develop --source-secret=<secret> --build-secret=<secret> -n <jenkinsProject>
```

As you notice, ```oc new-build``` uses ```--strategy=pipeline```, this means that in the repository added, will try to find our Jenkinsfile. Later you can see the required parameters needed to fire a pipeline template. To add the parameters, update the build parameters as required.
```
oc env bc/springboot-develop APP_NAME=springboot GIT_BRANCH="develop" GIT_CREDENTIALS=<credentials> GIT_URL=<repo> BUILD_PROJECT=<dev-project> BASE_IMAGE=redhat-openjdk8-openshift:1.2 BUILD_TAG='latest' DEPLOY_TAG='dev' STRATEGY='postman' -n <jenkinsProject>
```

### Jenkinsfile
Pipeline templates can be used with ```@Library('pipelines')``` and then use one of the templates available
```
@Library('pipelines') _

javaFullPipeline(
  appName: params.APP_NAME, 
  gitBranch: params.GIT_BRANCH, 
  gitCredentials: params.GIT_CREDENTIALS, 
  gitUrl: params.GIT_URL,
  buildProject: params.BUILD_PROJECT,
  uatProject: params.UAT_PROJECT,
  prodProject: params.PROD_PROJECT,
  baseImage: params.BASE_IMAGE,
  buildTag: params.BUILD_TAG,
  deployTag: params.DEPLOY_TAG,
  testStrategy: params.STRATEGY
)
```

### Project Examples
 - [Node.js](https://github.com/rhappdev/springboot-template)
 - [Springboot](https://github.com/rhappdev/nodejs-template)
 
### Dependencies
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
   

