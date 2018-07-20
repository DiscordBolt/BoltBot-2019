void setBuildStatus(String message, String state, String context) {
    step([
        $class: "GitHubCommitStatusSetter",
        reposSource: [$class: "ManuallyEnteredRepositorySource", url: "https://github.com/DiscordBolt/BoltBot"],
        contextSource: [$class: "ManuallyEnteredCommitContextSource", context: context],
        errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
        statusResultSource: [$class: "ConditionalStatusResultSource", results: [[$class: "AnyBuildResult", message: message, state: state]]]
    ]);
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
                DISCORD_TOKEN = credentials('discordToken');
            }
            steps {
                echo 'Stage:Build'
                withCredentials([string(credentialsId: 'discordToken', variable: 'token')]) {
                    sh "gradle build -x test -PDiscordToken=${token}"
                }
                archiveArtifacts artifacts: 'build/libs/**/*.jar', fingerprint: true
            }
        }
        stage('Test') {
            steps {
                echo 'Stage:Test'
                sh 'gradle test'
                junit 'build/test-results/**/*.xml'
            }
        }
        stage('Check') {
            steps {
                echo 'Stage:Check'
                step([$class: 'hudson.plugins.checkstyle.CheckStylePublisher', pattern: '**/reports/checkstyle/main.xml'])
                script {
                    def warnings = tm('$CHECKSTYLE_COUNT').toInteger();
                    def warnings_new = tm('$CHECKSTYLE_NEW').toInteger();
                    if (warnings > 0) {
                        setBuildStatus('This commit has ' + warnings + ' checkstyle warnings. (' + warnings_new + ' new)', 'FAILURE', 'continuous-integration/jenkins/checkstyle');
                    }
                }
            }
        }
        stage('Deploy') {
            when {
                branch 'master'
            }
            steps {
                echo 'Stage:Deploy'
                withCredentials([string(credentialsId: 'dockerPassword', variable: 'password')]) {
                    sh "gradle jib -PDockerPassword=${password}"
                }
            }
        }
    }
}
