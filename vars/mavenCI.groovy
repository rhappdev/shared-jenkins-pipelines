def call(Map pipelineParameters) {

   node("maven") {

      echo "Executing CI for application ${pipelineParameters.appName}: Git server (${pipelineParameters.gitUrl}) and branch (${pipelineParameters.gitBranch})"

      stage("Clone application sources") {

         git branch: pipelineParameters.gitBranch, credentialsId: pipelineParameters.gitCredentials, url: pipelineParameters.gitUrl

      }

      stage("Build the Project") {
         sh 'mvn -s $MAVEN_SETTINGS clean package -DskipTests=true'

         if (pipelineParameters.buildProject) {
            // in case we had a build project, it means we need to save the target
            stash name: "app-binary", includes: "target/*"
         } 

      }

      stage('Test & QA') {
         parallel (
            'Unit testing': {
               sh 'mvn -s $MAVEN_SETTINGS test -DskipTests=true'
            },
            'Static Analysis': {
               echo "TODO:Sonar"  
            }   
         )
      }    

      def pom = readMavenPom file: "pom.xml"
      def developmentVersion = pom.version

      if (pipelineParameters.gitBranch != 'master' && !pipelineParameters.gitBranch.startsWith('hotfix')) {
    
         stage("Maven deploy") {
               variables.save("BUILD_VERSION", developmentVersion)
            }

         }

      } else  {

         stage("Maven release") {
               def releaseVersion = pom.version.replace("-SNAPSHOT", "-${BUILD_NUMBER}")
               variables.save("BUILD_VERSION", releaseVersion)
            }

	    }

      }
   }

}
