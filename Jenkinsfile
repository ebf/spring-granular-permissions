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
          sh """
            gradle clean build
          """
      }
    }

    stage('Publish Archives') {
      agent {
          docker {
              image 'ebfdev/openjdk:13-jdk-alpine'
              args '-u root:root'
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

              sh "gradle publish -Pnexus_user=$USER -Pnexus_pass=$PASS"
          }
      }
    }
  }
}