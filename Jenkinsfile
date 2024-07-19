node {
    def mvnHome
    stage('Pull source code') {
       git 'https://chensoul.github.io/spring-boot3-monolith'
       mvnHome = tool 'maven'
    }
    dir('.') {
         stage('Code scanning'){
             sh '''
             '''
         }

        stage('Maven build and Unit Test'){
            sh '''
            mvn -ntp -B -U clean package
            '''
        }

       stage('Docker build'){
            sh '''
            docker build . --file Dockerfile.simple -t spring-boot3-monolith
            docker tag spring-boot3-monolith chensoul/spring-boot3-monolith:latest
            docker push chensoul/spring-boot3-monolith:latest
            '''
        }

        stage('Deploy to Kubernetes and do integration test'){
            sh '''
            '''
        }

        stage('Integration Test'){
            sh '''
            '''
        }

        stage('Promote Jar to Testing Repo'){
            sh '''
            '''
        }

        stage('UI Testing'){
            sh '''
            '''
        }

        stage('Promote jar and image to release repo'){
            sh '''
            '''
        }
    }
 }
