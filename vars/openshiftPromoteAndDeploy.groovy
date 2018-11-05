def call(Map pipelineParameters) {
   def appName = pipelineParameters.appName
   node("maven") {

      echo "Executing Promote and Deploy in ${pipelineParameters.promotedProject} for application ${pipelineParameters.appName}"

      def version = variables.load("BUILD_VERSION")
      def remApp = pipelineParameters.appName

      stage("Openshift deploy in ${pipelineParameters.promotedProject}") {
         openshift.withCluster() {
            openshift.withProject(pipelineParameters.promotedProject) {
              openshift.tag("${pipelineParameters.buildProject}/${pipelineParameters.appName}:${pipelineParameters.deployTag}", "${pipelineParameters.promotedProject}/${pipelineParameters.appName}:${pipelineParameters.promotedTag}")
              if (pipelineParameters.deploymentStrategy == "Blue/Green") {
                def route = openshift.selector("route/${appName}")
                if (route.exists()) {
                  def routeObj = route.object()
                  remApp = routeObj.spec.to.name
                  if (remApp == "${appName}-green") {
                    appName = "${appName}-blue"
                  } else {
                    appName = "${appName}-green"
                  }
                } else {
                  appName = appName + "-green"
                }
              }
              
              def dc = openshift.selector( "dc/${appName}")
              if (!dc.exists()) {
                echo "Creating deployment for application ${appName}"
                openshift.newApp("--image-stream=${pipelineParameters.promotedProject}/${pipelineParameters.appName}:${pipelineParameters.promotedTag}","--name=${appName}")
                def route = openshift.selector("route/${pipelineParameters.appName}")
                if (!route.exists()) {
                  // If it doesn't exists create it.
                  openshift.selector("svc/${appName}").expose("--name=${pipelineParameters.appName}")
                }
                openshift.set("triggers", "dc/${appName}", "--from-config", "--remove")
                openshift.set("triggers", "dc/${appName}", "--manual")
                openshift.set("probe", "dc/${appName}", "--liveness", "-- echo ok")           
              } else {
                dc.rollout().latest()
              }

              dc = openshift.selector( "dc/${appName}")
              def deployment = dc.object()
              deployment.metadata.labels['current-version'] = version
              openshift.apply(deployment)
              
              echo "Application deployment has been rolled out"
              def dcObj = dc.object()
              def podSelector = openshift.selector('pod', [deployment: "${appName}-${dcObj.status.latestVersion}"])
              podSelector.untilEach {
                echo "VERIFY pod: ${it.name()}"
                return it.object().status.containerStatuses[0].ready
              }

              def route = openshift.selector("route/${pipelineParameters.appName}").object()
              route.spec.to.name = "${appName}"
              openshift.apply(route)

            }
         }
      }
   }

   setConfiguration(
     gitBranch: pipelineParameters.gitBranch, 
     gitCredentials: pipelineParameters.gitCredentials, 
     gitUrl: pipelineParameters.gitUrl,
     buildProject: pipelineParameters.promotedProject,
     appName: appName, 
     environment: pipelineParameters.promotedTag)

   runTests(
     strategy: pipelineParameters.testStrategy, 
     environment: pipelineParameters.promotedTag, 
     gitBranch: pipelineParameters.gitBranch, 
     gitCredentials: pipelineParameters.gitCredentials, 
     gitUrl: pipelineParameters.gitUrl)
}
