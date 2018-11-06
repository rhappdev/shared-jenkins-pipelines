## Setup
### Jenkins
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