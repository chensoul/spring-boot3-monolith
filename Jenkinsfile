node {
    def mvnHome
    stage('Pull source code') {
       git 'https://github.com/alexwang66/notebook-k8s.git'

       mvnHome = tool 'maven'
    }
    dir('notebook-service') {
         stage('Code scanning'){
                    sh '''
                    '''
                }

        stage('Maven build and Unit Test'){
            sh '''
            mvn package
            curl  -H \"X-JFrog-Art-Api: AKCp5ekw5hzGB6gTaU6YUWU5TCiEWS1dfNvJLmVQxYrFGqxVDAZ6kWnhy36sxwG3iTqPDfQ7T\" \
                   -X PUT \
                   -T target/notebook-service-0.0.1-SNAPSHOT.jar \
                   'http://localhost:8082/artifactory/generic-dev-local/notebook-k8s/notebook-service/notebook-service-0.0.1-SNAPSHOT.jar;Test.UnitTestPassed=true;Test.CodeScanningPassed=true'
            '''
        }

       stage('Docker build'){
            sh '''
            docker build -t art.local:8081/docker-testing-local/notebook-k8s/notebook-service:\$BUILD_NUMBER .
            docker push art.local:8081/docker-testing-local/notebook-k8s/notebook-service:\$BUILD_NUMBER
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
            curl  -H \"X-JFrog-Art-Api: AKCp5ekw5hzGB6gTaU6YUWU5TCiEWS1dfNvJLmVQxYrFGqxVDAZ6kWnhy36sxwG3iTqPDfQ7T\" \
                               -X PUT \
                               -T target/notebook-service-0.0.1-SNAPSHOT.jar \
                               'http://localhost:8082/artifactory/generic-testing-local/notebook-k8s/notebook-service/notebook-service-0.0.1-SNAPSHOT.jar;Test.UnitTestPassed=true;Test.CodeScanningPassed=true;Test.IntegrationTestPassed=true'
            '''
        }

        stage('UI Testing'){
            sh '''
            '''
        }

        stage('Promote jar and image to release repo'){
            sh '''
                curl  -H \"X-JFrog-Art-Api: AKCp5ekw5hzGB6gTaU6YUWU5TCiEWS1dfNvJLmVQxYrFGqxVDAZ6kWnhy36sxwG3iTqPDfQ7T\" \
                               -X PUT \
                               -T target/notebook-service-0.0.1-SNAPSHOT.jar \
                               'http://localhost:8082/artifactory/generic-release-local/notebook-k8s/notebook-service/notebook-service-0.0.1-SNAPSHOT.jar;Test.UnitTestPassed=true;Test.CodeScanningPassed=true;Test.IntegrationTestPassed=true;Released=true'
                docker tag art.local:8081/docker-testing-local/notebook-k8s/notebook-service:\$BUILD_NUMBER art.local:8081/docker-release-local/notebook-k8s/notebook-service:\$BUILD_NUMBER
                docker push art.local:8081/docker-release-local/notebook-k8s/notebook-service:\$BUILD_NUMBER
            '''
            def commandText = "curl  -H \"X-JFrog-Art-Api: AKCp5ekw5hzGB6gTaU6YUWU5TCiEWS1dfNvJLmVQxYrFGqxVDAZ6kWnhy36sxwG3iTqPDfQ7T\" -X PUT \"http://art.local:8082/artifactory/api/storage/docker-release-local/notebook-k8s/notebook-service/${env.BUILD_NUMBER}?properties=Test.UnitTestPassed=true;Test.CodeScanningPassed=true;Test.IntegrationTestPassed=true;Released=true\" ";
            sh commandText
        }
    }

 }
