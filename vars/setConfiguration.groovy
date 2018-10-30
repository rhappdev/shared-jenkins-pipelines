def call(Map pipelineParameters) {

   node("maven") {

      echo "Executing Confimaps, Secrets in ${pipelineParameters.buildProject} for application ${pipelineParameters.appName}"
      echo "Executing pipeline with parameters: ${pipelineParameters}"
      
      stage("Get Configuration files") {
        git branch: pipelineParameters.gitBranch, credentialsId: pipelineParameters.gitCredentials, url: pipelineParameters.gitUrl
      }

      def cmEnv = "configuration/${pipelineParameters.environment}/cm/variables.properties"
      
      stage("Remove old ConfigMaps and Secrets") {
        openshift.withCluster() {
            openshift.withProject(pipelineParameters.buildProject) {
              def cfgEnv = openshift.selector("cm/${pipelineParameters.appName}-cm-env")
              if (cfgEnv.exists()) {
                cfgEnv.delete()
              }
              if (fileExists(cmEnv)) {
                  openshift.create("configmap", "${pipelineParameters.appName}-cm-env", "--from-env-file=${cmEnv}")
              } else {
                  echo 'No CM-ENV file Exists'
              }
              
            }
        }
        /* oc create configmap <app>-cm-env --from-env-file=configuration/<environment>/cm/env/variables.properties
        oc create configmap <app>-cm-file --from-file=configuration/<environment>/cm/file/
        oc create secret generic <app>-secret-env --from-env-file=configuration/<environment>/environment */

      }

      stage("Set ConfiMaps and Secrets") {
        openshift.withCluster() {
            openshift.withProject(pipelineParameters.buildProject) {
               def dc = openshift.selector( "dc/${pipelineParameters.appName}")
               if (dc.exists()) {
                  def cfgEnv = openshift.selector("cm/${pipelineParameters.appName}-cm-env")
                  if (cfgEnv.exists()) {
                    // oc set env dc/<app> --from=configmap/<app>-cm-env
                    openshift.set("env", "dc/${pipelineParameters.appName}", "--from=configmap/${pipelineParameters.appName}-cm-env")
                    dc.rollout().latest()
                  }
               }
               
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

}
