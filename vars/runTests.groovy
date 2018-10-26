def call(Map testParameters) {

   switch (testParameters.strategy) {
        case "postman" :
           postmanTest(
                environment: testParameters.environment, 
                gitBranch: testParameters.gitBranch, 
                gitCredentials: testParameters.gitCredentials, 
                gitUrl: testParameters.gitUrl)
           break
        default:
           echo "Test strategy has not been set, skipping test phase"
           break
   } 

}


