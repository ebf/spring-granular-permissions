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
                    registryUrl 'https://repository.hosting.ebf.de'
                    registryCredentialsId 'credentials-repository-hosting-id'
                    image 'repository.hosting.ebf.de/ebfdev/openjdk:17-jdk-alpine'
                    args '-u root:root'
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
