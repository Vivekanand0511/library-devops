pipeline {
    agent any
    
    stages {
        stage('Build Java App & Docker Image') {
            steps {
                script {
                    sh 'docker build -t library-app:latest .' 
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    catchError(buildResult: 'SUCCESS', stageResult: 'SUCCESS') {
                        sh 'docker stop library-container'
                        sh 'docker rm library-container'
                    }
                    sh 'docker run -d -p 8081:8081 --name library-container library-app:latest'
                }
            }
        }
    }
}