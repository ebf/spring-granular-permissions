pipeline {
  agent {
    label 'slave'
  }

  stages {
    stage('Build') {
      agent {
        docker {
          image 'gradle:4.10.2-jdk8-slim'
          reuseNode true
        }
      }
      steps {
        withCredentials([usernamePassword(credentialsId: 'ossr_credentials', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
          sh """
            gradle clean build uploadArchives -PossrUsername=$USER -PossrPassword=$PASS
          """
        }
      }
    }
  }
}