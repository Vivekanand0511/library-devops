pipeline {
    agent any
    
    stages {
        stage('Build Java App & Docker Image') {
            steps {
                script {
                    // Compiles the app and packages it into a Docker image
                    bat 'docker build -t library-app:latest .' 
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    // Removes the old container if it exists, then deploys the new one
                    catchError(buildResult: 'SUCCESS', stageResult: 'SUCCESS') {
                        bat 'docker stop library-container'
                        bat 'docker rm library-container'
                    }
                    bat 'docker run -d -p 8081:8081 --name library-container library-app:latest'
                }
            }
        }
    }
}