node {
    def mvnHome
    stage('Pull source code') {
       git 'https://chensoul.github.io/spring-boot3-monolith'
       mvnHome = tool 'maven'
    }
    dir('monolith') {
         stage('Code scanning'){
             sh '''
             '''
         }

        stage('Maven build and Unit Test'){
            sh '''
            mvn clean package
            '''
        }

       stage('Docker build'){
            sh '''
            docker build -t monolith .
            docker tag monolith:latest chensoul/monolith:latest
            docker push chensoul/monolith:latest
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
