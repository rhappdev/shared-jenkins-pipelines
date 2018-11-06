# Architecture

## Index 

- [Architecture](#architecture)
  * [Pipeline Definition](#pipeline-definition)
    + [javaFullPipeline](#javafullpipeline)
    + [npmFullPipeline](#npmfullpipeline)
    + [javaLibraryPipeline](#javalibrarypipeline)
    + [npmLibraryPipeline](#npmlibrarypipeline)
    + [Templates](#templates)
      - [CI Pipeline](#ci-pipeline)
        * [mavenCI](#mavenci)
        * [npmCI](#npmci)
      - [CD Pipeline](#cd-pipeline)
        * [openshiftBuildAndDeploy](#openshiftbuildanddeploy)
        * [setConfiguration](#setconfiguration)
        * [runTests](#runtests)
        * [openshiftPromoteAndDeploy](#openshiftpromoteanddeploy)

## Pipeline Definition
The pipelines have been developed to be reusable across them. openshiftCD will be composed of the following pipeline templates:
![Full Pipeline](https://github.com/rhappdev/assets/blob/master/architecture/full_pipeline.png)

### javaFullPipeline

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

### npmFullPipeline

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

### javaLibraryPipeline

| Name          | Description   | Example |  Required |
| ------------- |:-------------:| -------| :-------: |
| appName      | Application name | nodejs | Yes |
| gitUrl      | Application git URL      |   https://github.com/rhappdev/springboot-template.git | Yes |
| gitBranch | Desired branch to checkout      |   master| Yes |
| gitCredentials      | Jenkins credential id needed to access the repository | gitlab-credentials | Yes |

### npmLibraryPipeline

| Name          | Description   | Example |  Required |
| ------------- |:-------------:| -------| :-------: |
| appName      | Application name | nodejs | Yes |
| gitUrl      | Application git URL      |   https://github.com/rhappdev/nodejs-template.git | Yes |
| gitBranch | Desired branch to checkout      |   master| Yes |
| gitCredentials      | Jenkins credential id needed to access the repository | gitlab-credentials | Yes |
| type      | Project Type | Library | Yes |
### Templates
#### CI Pipeline
If we want to have a good way to administrate the pipelines, we need to store them in a git repository and try to define pipelines as templates, reusable between projects. This means that depending the languages that we are using, we are going to define the following CI pipelines:

##### mavenCI
Maven CI pipeline for Java projects.
![mavenCI](https://github.com/rhappdev/assets/blob/master/architecture/mavenCI.png)

| Name          | Description   | Example |  Required |
| ------------- |:-------------:| -------| :-------: |
| appName      | Application name | nodejs | Yes |
| gitUrl      | Application git URL | https://github.com/rhappdev/nodejs-template.git | Yes |
| gitBranch | Desired branch to checkout      | master | Yes |
| gitCredentials      | Jenkins credential id needed to access the repository | gitlab-credentials | Yes |
| buildProject      | Openshift namespace | test | Optional |

##### npmCI
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

#### CD Pipeline
At this stage, we are going to run openshift, create images, push them to the registry and promote them between environments.
![openshiftCD Components](https://github.com/rhappdev/assets/blob/master/architecture/openshiftCD-components.png)

##### openshiftBuildAndDeploy
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

##### setConfiguration
| Name          | Description   | Example |  Required |
| ------------- |:-------------:| -------| :-------: |
| appName      | Application name | nodejs | Yes |
| gitUrl      | Application git URL      |   https://github.com/rhappdev/nodejs-template.git | Yes |
| gitBranch | Desired branch to checkout      |   master | Yes |
| gitCredentials      | Jenkins credential id needed to access the repository | gitlab-credentials | Yes |
| buildProject      | Openshift namespace | test | Yes |
| environment      | Environment to set up, Valid: dev, test, uat and prod | test | Yes |

##### runTests
| Name          | Description   | Example |  Required |
| ------------- |:-------------:| -------| :-------: |
| gitUrl      | Application git URL      |   https://github.com/rhappdev/nodejs-template.git | Yes |
| gitBranch | Desired branch to checkout      |   master | Yes |
| gitCredentials      | Jenkins credential id needed to access the repository | gitlab-credentials | Yes |
| buildProject      | Openshift namespace | test | Yes |
| environment      | Environment to set up, Valid: dev, test, uat and prod | test | Yes |
| strategy      | Integration test strategy to use | postman | Optional |

##### openshiftPromoteAndDeploy
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