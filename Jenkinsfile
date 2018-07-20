void setBuildStatus(String message, String state, String context) {
  step([
      $class: "GitHubCommitStatusSetter",
      reposSource: [$class: "ManuallyEnteredRepositorySource", url: "https://github.com/DiscordBolt/BoltBot"],
      contextSource: [$class: "ManuallyEnteredCommitContextSource", context: context],
      errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
      statusResultSource: [ $class: "ConditionalStatusResultSource", results: [[$class: "AnyBuildResult", message: message, state: state]] ]
  ]);
}

def isPRMergeBuild() {
    return (env.BRANCH_NAME ==~ /^PR-\d+$/)
}

pipeline {
  agent {
    docker {
      image 'gradle:4.8-jdk8-alpine'
    }

  }
  stages {
    stage('Checkout') {
      steps {
        echo 'Stage:Checkout'
        git 'https://github.com/DiscordBolt/BoltBot'
      }
    }
    stage('Build') {
      steps {
        echo 'Stage:Build'
        sh 'chmod +x gradlew'
        sh './gradlew build -x test'
      }
    }
    stage('Test') {
      steps {
        echo 'Stage:Test'
        sh './gradlew test'
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
            setBuildStatus("This commit has " + warnings + " checkstyle warnings. (" + warnings_new + " new)", "FAILURE", "continuous-integration/jenkins/checkstyle");
          }
        }
      }
    }
    stage('Deploy') {
      steps {
        echo 'Stage:Deploy'
      }
    }
  }
  post {
    always {
      archiveArtifacts artifacts: 'build/libs/**/*.jar', fingerprint: true
      junit 'build/test-results/**/*.xml'
    }
  }
}
