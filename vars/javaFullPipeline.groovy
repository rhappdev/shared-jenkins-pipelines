def call(Map pipelineParameters) {

   echo "Executing full CI/CD Java pipeline for application ${pipelineParameters.appName}: Git server (${pipelineParameters.gitUrl}) and branch (${pipelineParameters.gitBranch})"

   mavenCI(pipelineParameters)

   openshiftCD(pipelineParameters)

}
