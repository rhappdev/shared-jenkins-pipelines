def call(Map pipelineParameters) {

   echo "Executing Openshift CD for application ${pipelineParameters.appName}"

   openshiftBuildAndDeploy(pipelineParameters)

   if (pipelineParameters.gitBranch == 'master' || pipelineParameters.gitBranch.startsWith('hotfix')) {

      node () {
         stage ("Approval required: from ${pipelineParameters.buildProject} to UAT project") {
            input "Would you like to deploy ${pipelineParameters.appName} in UAT?"
         }
      }
      // Deploy from test to uat
      openshiftPromoteAndDeploy(
        appName: pipelineParameters.appName,
        gitBranch: pipelineParameters.gitBranch,
        gitCredentials: pipelineParameters.gitCredentials,
        gitUrl: pipelineParameters.gitUrl,
        promotedProject: pipelineParameters.uatProject,
        deployTag: 'test',
        buildProject: pipelineParameters.buildProject,
        promotedTag: 'uat',
        testStrategy: pipelineParameters.testStrategy)
      
      node () {
         stage ("Approval required: from ${pipelineParameters.uatProject} to PROD project") {
            input "Would you like to deploy ${pipelineParameters.appName} in PROD?"
         }
      }

      def deploymentStrategy
      node () {
         stage ("Deployment Strategy: from ${pipelineParameters.uatProject} to PROD project") {
            deploymentStrategy = input message: "Enter deployment strategy",
                    parameters: [choice(name: 'Deployment', choices:  'default\nBlue/Green')]
         }
      }

      openshiftPromoteAndDeploy(
        appName: pipelineParameters.appName,
        gitBranch: pipelineParameters.gitBranch,
        gitCredentials: pipelineParameters.gitCredentials,
        gitUrl: pipelineParameters.gitUrl,
        promotedProject: pipelineParameters.prodProject,
        deployTag: 'uat',
        buildProject: pipelineParameters.uatProject,
        promotedTag: 'prod',
        testStrategy: pipelineParameters.testStrategy,
        deploymentStrategy: deploymentStrategy)
   }
}
