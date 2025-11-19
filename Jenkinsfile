pipeline {
    agent any
    stages {
        stage('Build & Test') {
            steps {
                // Maven Wrapper ile test
                sh './mvnw clean test'
            }
        }
    }
    post {
        always {
            // Test sonuçlarını archive et
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
