def call(Map pipelineParameters) {

   node("maven") {

      echo "Executing Build and Deploy in ${pipelineParameters.buildProject} for application ${pipelineParameters.appName}"

      def version = variables.load("BUILD_VERSION")

      stage("Openshift build in ${pipelineParameters.buildProject}") {

	    unstash name: "app-binary"

         openshift.withCluster() {
            openshift.withProject(pipelineParameters.buildProject) {
               if (!openshift.selector( "bc/${pipelineParameters.appName}").exists()) {
                  echo "Creating build for application ${pipelineParameters.appName}"
                  openshift.newBuild("--name=${pipelineParameters.appName}", "--image-stream=${pipelineParameters.baseImage}", "--binary", "--to=${pipelineParameters.appName}:${pipelineParameters.buildTag}")
               }
               // TODO extract from-file value as parameter
               if (pipelineParameters.baseImage.contains('nodejs')) {
                  echo "Building from archive"
                  openshift.selector("bc", pipelineParameters.appName).startBuild("--from-archive=target/archive.tar", "--wait")
               } else {
                  echo "Building from file"
                  openshift.selector("bc", pipelineParameters.appName).startBuild("--from-file=target", "--wait")
               }
               openshift.tag("${pipelineParameters.buildProject}/${pipelineParameters.appName}:${pipelineParameters.buildTag}", "${pipelineParameters.buildProject}/${pipelineParameters.appName}:${version}")
            }
         }
      }

      stage("Openshift deploy in ${pipelineParameters.buildProject}") {

         openshift.withCluster() {
            openshift.withProject(pipelineParameters.buildProject) {
               openshift.tag("${pipelineParameters.buildProject}/${pipelineParameters.appName}:${pipelineParameters.buildTag}", "${pipelineParameters.buildProject}/${pipelineParameters.appName}:${pipelineParameters.deployTag}")
               def dc = openshift.selector( "dc/${pipelineParameters.appName}")
               if (!dc.exists()) {
                  echo "Creating deployment for application ${pipelineParameters.appName}"
                  openshift.newApp("--image-stream=${pipelineParameters.buildProject}/${pipelineParameters.appName}:${pipelineParameters.deployTag}","--name=${pipelineParameters.appName}").narrow('svc').expose()
                  openshift.set("triggers", "dc/${pipelineParameters.appName}", "--from-config", "--remove")
                  openshift.set("triggers", "dc/${pipelineParameters.appName}", "--manual")
                  openshift.set("probe", "dc/${pipelineParameters.appName}", "--liveness", "-- echo ok")
                  // TODO: set readiness with common endpoint. /health? /docs?        
               } else {
                  dc.rollout().latest()
               }

               dc = openshift.selector( "dc/${pipelineParameters.appName}")
               def deployment = dc.object()
               echo "Deployment: ${deployment}"
               deployment.metadata.labels['current-version'] = version
               openshift.apply(deployment)

               echo "Application deployment has been rolled out"
               def dcObj = dc.object()
               def podSelector = openshift.selector('pod', [deployment: "${pipelineParameters.appName}-${dcObj.status.latestVersion}"])
               podSelector.untilEach {
                  echo "VERIFY pod: ${it.name()}"
                  return it.object().status.containerStatuses[0].ready
               }
            }
         }
      }
   }

   setConfiguration(
     gitBranch: pipelineParameters.gitBranch, 
     gitCredentials: pipelineParameters.gitCredentials, 
     gitUrl: pipelineParameters.gitUrl,
     buildProject: pipelineParameters.buildProject,
     appName: pipelineParameters.appName, 
     environment: pipelineParameters.deployTag)

   runTests(
     strategy: pipelineParameters.testStrategy, 
     environment: pipelineParameters.deployTag, 
     gitBranch: pipelineParameters.gitBranch, 
     gitCredentials: pipelineParameters.gitCredentials, 
     gitUrl: pipelineParameters.gitUrl)
}
