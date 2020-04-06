pipeline {
  agent {
    label 'slave'
  }

  stages {
    stage('Gradle Build and Test') {
      agent {
        docker {
          image 'gradle:4.10.2-jdk8-slim'
          reuseNode true
        }
      }
      steps {
          echo '----------------------------------------------------------------------------------------'
          echo 'Building Backend...'
          echo '----------------------------------------------------------------------------------------'
          withCredentials([usernamePassword(credentialsId: 'nexus-maven-ebf-releases-deployment',
                                                          usernameVariable: 'USER',
                                                          passwordVariable: 'PASS')]) {
            sh """
              gradle clean build --no-daemon -Pnexus_user=$USER -Pnexus_pass=$PASS
            """
          }
      }
    }

    stage('Publish Archives') {
      agent {
          docker {
            image 'gradle:4.10.2-jdk8-slim'
            reuseNode true
          }
      }

      steps {
          withCredentials([usernamePassword(
              credentialsId: 'nexus-maven-ebf-releases-deployment',
              usernameVariable: 'USER',
              passwordVariable: 'PASS'
          )]) {
              echo '----------------------------------------------------------------------------------------'
              echo 'Publish Archives'
              echo '----------------------------------------------------------------------------------------'

              sh "gradle uploadArchives -Pnexus_user=$USER -Pnexus_pass=$PASS"
          }
      }
    }
  }
}