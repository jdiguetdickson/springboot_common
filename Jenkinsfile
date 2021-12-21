pipeline {

    agent any
    tools {
        maven 'MAVEN_HOME'
        jdk 'jdk17'
    }
    options {
        timestamps()
        ansiColor("xterm")
    }
    environment {
        IMAGE = readMavenPom().getArtifactId()
        VERSION = readMavenPom().getVersion().replace("-SNAPSHOT", "")
        BRANCH_NAME = getCurrentBranch()
        SERVICE_NAME = "${env.BRANCH_NAME == "develop" ? "${IMAGE}-${params.deploy_to}" : "${IMAGE}-prod"}"
        REMOTE_SERVER = "${env.BRANCH_NAME == "develop" ? "sb_${params.deploy_to}" : "sb_prod"}"
        FOLDER = "${env.BRANCH_NAME == "develop" ? "${params.deploy_to}" : "prod"}"
    }
    stages {
        stage("Build test") {
        when {
              branch 'develop'
           }
            steps {
                println "deploy_to ${SERVICE_NAME} ${REMOTE_SERVER} ${FOLDER}";
                sh 'set +x'
                withCredentials([usernamePassword(credentialsId: 'access_gitlab', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                sh "mvn build-helper:parse-version versions:set -DnewVersion='\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion}-SNAPSHOT'"
                sh 'mvn scm:checkin -Dincludes=pom.xml -Dmessage="Setting version, preping for release." -Dusername=$USERNAME -Dpassword=$PASSWORD'
                sh 'mvn package'
                }
                sh 'set -x'
            }
        }

        stage("Build prod") {
        when {
              branch 'master'
           }
           steps {
               script {
                    sh 'set +x'
                    withCredentials([usernamePassword(credentialsId: 'access_gitlab', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                     sh "mvn build-helper:parse-version versions:set -DnewVersion='\${parsedVersion.majorVersion}.\${parsedVersion.nextMinorVersion}.0'"
                     sh 'mvn scm:checkin -Dincludes=pom.xml -Dmessage="Setting version, preping for release." -Dusername=$USERNAME -Dpassword=$PASSWORD'
                     sh 'mvn package'
                     def NEW_VERSION = readMavenPom().getVersion()
                     sh 'git tag -d ${NEW_VERSION}'
                     sh 'git fetch --tags'
                     echo "New release version ${NEW_VERSION}"
                     sh "git tag -a ${NEW_VERSION} -m 'Release version ${NEW_VERSION}'"
                     sh "git push origin ${NEW_VERSION}"
                    }
                    sh 'set -x'
               }
           }
        }
        stage('Deploy') {
            steps {
                script {
                    def NEW_VERSION = readMavenPom().getVersion()
                    def jarWithVersion = "target/${IMAGE}-${NEW_VERSION}.jar"
                    println "jarWithVersion=${jarWithVersion} + ${NEW_VERSION} + ${VERSION}"
                    if (fileExists(file: jarWithVersion)) {
                               sshagent(['springboot_prod_access']) {
                                   sh 'ssh -v -o StrictHostKeyChecking=no -l springboot ${REMOTE_SERVER} "sudo service ${SERVICE_NAME} stop"'
                                   sh "scp -r ${jarWithVersion} springboot@sb_test:/home/springboot/${IMAGE}/${FOLDER}/magento.jar"
                                   sh 'ssh -v -o StrictHostKeyChecking=no -l springboot ${REMOTE_SERVER} "sudo service ${SERVICE_NAME} start"'
                                   if(FOLDER=="prod"){
                                    sh "scp -r ${jarWithVersion} springboot@sb_test:/home/springboot/${IMAGE}/${FOLDER}/archive/${IMAGE}-${NEW_VERSION}.jar"
                                   }
                               }
                        }
                    }
                }
            }
        }

    post {
        success {
            deleteDir()
        }
    }
}
        def getCurrentBranch () {
            return sh (
                script: 'git rev-parse --abbrev-ref HEAD',
                returnStdout: true
            ).trim()
        }
