def call(Map postmanParameters) {

   node("nodejs") {

      echo "Executing Postman test collection against environment definition ${postmanParameters.environment}"

      stage("Get Postman tests") {
        git branch: postmanParameters.gitBranch, credentialsId: postmanParameters.gitCredentials, url: postmanParameters.gitUrl
      }

      stage("Run tests") {
         // TODO: think about creating a Jenkins slave with newman install to improve response time
         sh "npm install -g newman@3.10.0"
         sh "newman run configuration/${postmanParameters.environment}/postman/postman_collection.json -e configuration/${postmanParameters.environment}/postman/${postmanParameters.environment}.json"
      }

   }

}
