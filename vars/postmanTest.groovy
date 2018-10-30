def call(Map postmanParameters) {

   node("nodejs") {

      echo "Executing Postman test collection against environment definition ${postmanParameters.environment}"
      echo "Executing pipeline with parameters: ${postmanParameters}"
      stage("Get Postman tests") {
        git branch: postmanParameters.gitBranch, credentialsId: postmanParameters.gitCredentials, url: postmanParameters.gitUrl
      }

      stage("Run tests") {
         // TODO: think about creating a Jenkins slave with newman install to improve response time
         sh "npm install -g newman@3.10.0"
         sh "newman run configuration/postman/postman_collection.json -e configuration/postman/${postmanParameters.environment}.json"
      }

   }

}
