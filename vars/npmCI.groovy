def call(Map pipelineParameters) {

    node("nodejs") {

        echo "Executing CI for application ${pipelineParameters.appName}: Git server (${pipelineParameters.gitUrl}) and branch (${pipelineParameters.gitBranch})"

        stage("Clone application sources") {
            sh "git config --global credential.helper 'cache --timeout 7200'"
            git branch: pipelineParameters.gitBranch, credentialsId: pipelineParameters.gitCredentials, url: pipelineParameters.gitUrl

        }

        stage("Build the Project") {
                sh 'npm install'

                sh 'npm run build:live' 

                if (pipelineParameters.buildProject) {
                    // in case we had a build project, it means we need to save the target
                    sh 'mkdir target; tar --exclude=".git" --exclude="./node_modules" -cvf target/archive.tar ./ || [[ $? -eq 1 ]]'
                    //sh 'rsync -av . target --exclude=node_modules --exclude=.git || [[ $? -eq 1 ]]'
                    stash name: "app-binary", includes: "dist/*,target/*"
                }
        }

        stage('Test & QA') {
            parallel (
                'Unit testing': {
                    sh 'npm test'
                },
                'Static Analysis': {

                echo "TODO:Sonar"  

                }   
            )
        }    

        def pkg = readJSON file: 'package.json'
        def version = pkg.version
        def versionRelease = version+"-${BUILD_NUMBER}"

        variables.save("BUILD_VERSION", versionRelease)

        if (pipelineParameters.gitBranch == 'master' || pipelineParameters.gitBranch.startsWith('hotfix')) {

            stage("Git tag") {

                    sh "git config --global user.email jenkins@jenkins.com"
                    sh "npm --no-git-tag-version version ${versionRelease}"
                    sh "git commit -am '[Jenkins] prepare release ${versionRelease}'"
                    sh "git push origin master"
                    sh "git tag ${versionRelease} -m '${versionRelease}'"
                    sh "git push origin ${versionRelease}"
                     
            }

            // With nodejs - v4.6.2 not working, need to upgrade to read .npmrc file.
            if (pipelineParameters.type == 'library') {
                stage("Publish npm package") {
                    configFileProvider([configFile(fileId: 'npmrc', variable: 'npmrcLocation')]) {
                        sh "cp ${npmrcLocation} .npmrc"
                        sh "cat .npmrc"
                        sh "npm publish"
                        sh "git checkout ."
                    }
                }
            }
            

            stage("Git release") {
                    sh "npm --no-git-tag-version version ${version}"
                    sh "git commit -am '[Jenkins] prepare for next development iteration'"
                    sh "git push origin master"
            }
        } else {
            // With nodejs - v4.6.2 not working, need to upgrade to read .npmrc file.
            if (pipelineParameters.type == 'library') {
                stage("Publish npm package") {
                    configFileProvider([configFile(fileId: 'npmrc', variable: 'npmrcLocation')]) {
                        // overwrite default .npmrc file to include the auth token.
                        sh "cp ${npmrcLocation} .npmrc"
                        sh "cat .npmrc"
                        // Upload a version for development
                        def versionDevelopment = version+"-dev-${BUILD_NUMBER}"
                        sh "npm --no-git-tag-version version ${versionDevelopment}"
                        sh "npm publish"
                        sh "git checkout ."
                    }
                }
            }
        }
    }

}
