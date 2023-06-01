pipeline {
  agent {
    label 'slave'
  }

  stages {
    stage('Publish Archives') {
      when {
          branch 'master'
      }
      agent {
          docker {
            image 'ebfdev/openjdk:17-jdk-alpine'
            reuseNode true
          }
      }

      steps {
          configFileProvider([configFile(
                  fileId: '2948ab4c-9add-401f-9bda-d22642238c6e',
                  variable: 'MAVEN_SETTINGS'
          )]) {
              echo '----------------------------------------------------------------------------------------'
              echo 'Publish Archives'
              echo '----------------------------------------------------------------------------------------'

              sh "./gradlew -Dmaven.settings=$MAVEN_SETTINGS publish"
          }
      }
    }
  }
}
