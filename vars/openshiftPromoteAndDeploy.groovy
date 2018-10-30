def call(Map pipelineParameters) {

   node("maven") {

      echo "Executing Promote and Deploy in ${pipelineParameters.promotedProject} for application ${pipelineParameters.appName}"

      def version = variables.load("BUILD_VERSION")

      stage("Openshift deploy in ${pipelineParameters.promotedProject}") {

         openshift.withCluster() {
            openshift.withProject(pipelineParameters.promotedProject) {
               openshift.tag("${pipelineParameters.buildProject}/${pipelineParameters.appName}:${pipelineParameters.deployTag}", "${pipelineParameters.promotedProject}/${pipelineParameters.appName}:${pipelineParameters.promotedTag}")
               def dc = openshift.selector( "dc/${pipelineParameters.appName}")
               if (!dc.exists()) {
                  echo "Creating deployment for application ${pipelineParameters.appName}"
                  openshift.newApp("--image-stream=${pipelineParameters.promotedProject}/${pipelineParameters.appName}:${pipelineParameters.promotedTag}","--name=${pipelineParameters.appName}").narrow('svc').expose()
                  openshift.set("triggers", "dc/${pipelineParameters.appName}", "--from-config", "--remove")
                  openshift.set("triggers", "dc/${pipelineParameters.appName}", "--manual")
                  openshift.set("probe", "dc/${pipelineParameters.appName}", "--liveness", "-- echo ok")           
               } else {
                  dc.rollout().latest()
               }

               dc = openshift.selector( "dc/${pipelineParameters.appName}")
               def deployment = dc.object()
               deployment.metadata.labels['current-version'] = version
               openshift.apply(deployment)

               echo "Application deployment has been rolled out"
               
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
     buildProject: pipelineParameters.promotedProject,
     appName: pipelineParameters.appName, 
     environment: pipelineParameters.promotedTag)

   runTests(
     strategy: pipelineParameters.testStrategy, 
     environment: pipelineParameters.promotedTag, 
     gitBranch: pipelineParameters.gitBranch, 
     gitCredentials: pipelineParameters.gitCredentials, 
     gitUrl: pipelineParameters.gitUrl)
}
