def call(Map pipelineParameters) {

   node("maven") {

      echo "Executing CI for application ${pipelineParameters.appName}: Git server (${pipelineParameters.gitUrl}) and branch (${pipelineParameters.gitBranch})"

      stage("Clone application sources") {

         git branch: pipelineParameters.gitBranch, credentialsId: pipelineParameters.gitCredentials, url: pipelineParameters.gitUrl

      }

      stage("Build the Project") {

         configFileProvider(
            [configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
            sh 'mvn -s $MAVEN_SETTINGS clean package -DskipTests=true'
         }

         if (pipelineParameters.buildProject) {
            // in case we had a build project, it means we need to save the target
            stash name: "app-binary", includes: "target/*"
         } 

      }

      stage('Test & QA') {
         parallel (
            'Unit testing': {

               configFileProvider(
                  [configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
                   sh 'mvn -s $MAVEN_SETTINGS test -DskipTests=true'
               }

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

            configFileProvider([configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
               sh 'mvn -s $MAVEN_SETTINGS clean deploy -DskipTests=true'

               variables.save("BUILD_VERSION", developmentVersion)
            }

         }

      } else  {

         stage("Maven release") {

            configFileProvider([configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {

               sh "git config --global user.email jenkins@jenkins.com"
 
               def releaseVersion = pom.version.replace("-SNAPSHOT", "-${BUILD_NUMBER}")

               variables.save("BUILD_VERSION", releaseVersion)

               echo "Generating tag ${releaseVersion} in git and uploading artifact to artifact manager. Development version will be set to ${developmentVersion}"

               sh "mvn -s $MAVEN_SETTINGS org.apache.maven.plugins:maven-release-plugin:2.5:clean org.apache.maven.plugins:maven-release-plugin:2.5:prepare org.apache.maven.plugins:maven-release-plugin:2.5:perform -DreleaseVersion=${releaseVersion} -DdevelopmentVersion=${developmentVersion} -DautoVersionSubmodules=true -Dtag=${releaseVersion} -Darguments=\"-Dmaven.javadoc.skip=true\""
          
            }

	    }

      }
   }

}
