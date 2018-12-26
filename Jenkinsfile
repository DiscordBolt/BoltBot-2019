String buildDiscordEmbed() {
    def COLOR_MAP = ['SUCCESS': 3779158, 'UNSTABLE': 15588927, 'FAILURE': 14370117, 'ABORTED': 10329501]
    def RESULT_MAP = ['SUCCESS': 'Passed', 'UNSTABLE': 'Unstable', 'FAILURE': 'Failed', 'ABORTED': 'Aborted']

    def repoURL =  "${GIT_URL}".replace(".git", "")
    def shortCommit = "${GIT_COMMIT}".substring(0, 7)
    def buildTime = "Build completed in ${currentBuild.durationString}".replace(' and counting', '')
    def timestamp = new Date().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone('UTC'))

    return """
      {
        "embeds": [
          {
            "color": ${COLOR_MAP[currentBuild.currentResult]},
            "author": {
              "name": "Build #${currentBuild.number} ${RESULT_MAP[currentBuild.currentResult]}",
              "url": "${RUN_DISPLAY_URL}"
            },
            "title": "[${JOB_NAME}]",
            "url": "${repoURL}/tree/${GIT_BRANCH}",
            "description": "Commit: [`${shortCommit}`](${repoURL}/commit/${GIT_COMMIT})",
            "footer": {
              "text": "${buildTime}"
            },
            "timestamp": "${timestamp}"
          }
        ]
      }
    """
}

def isPRMergeBuild() {
    return (env.BRANCH_NAME ==~ /^PR-\d+$/)
}

pipeline {
    agent {
        docker {
            image 'gradle:4.9-jdk10'
        }
    }

    stages {
        stage('Build') {
            environment {
                DISCORD_TOKEN = credentials('discordToken')
                TWITCH_CLIENT_ID = credentials('twitchClientId')
            }
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew build -x test --stacktrace'
                archiveArtifacts artifacts: 'build/libs/**/*.jar', fingerprint: true
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test --stacktrace'
            }
            post {
                always {
                    junit 'build/test-results/**/*.xml'
                }
            }
        }
        stage('Deploy') {
            when {
                branch 'master'
            }
            steps {
                withCredentials([string(credentialsId: 'dockerPassword', variable: 'password')]) {
                    sh "./gradlew jib -PDockerPassword=${password}"
                    sh "./gradlew jib -PDockerPassword=${password} -PDockerTag=latest"
                }
            }
        }
    }
    post {
        always {
            withCredentials([string(credentialsId: 'discordWebhook', variable: 'url')]) {
                script {
                    try {
                        httpRequest url: "${url}", httpMode: 'POST', contentType: 'APPLICATION_JSON', requestBody: buildDiscordEmbed()
                    } catch (Exception e) {}
                }
            }
        }
    }
}
