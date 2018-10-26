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

               def deployment = dc.object()
               deployment.metadata.labels['current-version'] = version
               openshift.apply(deployment)

               echo "Application deployment has been rolled out"
               // TODO add deployment verification
            }
         }
      }
   }

   setConfiguration(
     gitBranch: pipelineParameters.gitBranch, 
     gitCredentials: pipelineParameters.gitCredentials, 
     gitUrl: pipelineParameters.gitUrl,
     buildProject: pipelineParameters.buildProject
     appName: pipelineParameters.appName, 
     environment: pipelineParameters.deployTag)

   runTests(
     strategy: pipelineParameters.testStrategy, 
     environment: pipelineParameters.deployTag, 
     gitBranch: pipelineParameters.gitBranch, 
     gitCredentials: pipelineParameters.gitCredentials, 
     gitUrl: pipelineParameters.gitUrl)
}
