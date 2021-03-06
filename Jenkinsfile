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
              gradle clean build --no-daemon
            """
          }
      }
    }

    stage('Publish Archives') {
      when {
          branch 'master'
      }
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

              sh "gradle uploadArchives -Dorg.gradle.project.nexus_user=$USER -Dorg.gradle.project.nexus_pass=$PASS"
          }
      }
    }
  }
}