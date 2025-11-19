pipeline {
    agent any

    stages {
        stage('Test') {
            steps {
                echo 'Testler çalıştırılıyor...'
                sh 'mvn clean test'
            }
        }
        stage('Deploy') {
            when {
                expression { currentBuild.result == null || currentBuild.result == 'SUCCESS' }
            }
            steps {
                echo 'Deploy aşaması başlatılıyor...'
                sh 'mvn clean package'
                // Buraya deploy komutunu ekleyebilirsin, örn: sh './deploy.sh'
            }
        }
    }
    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
        failure {
            echo 'Build başarısız oldu.'
        }
        success {
            echo 'Build ve deploy başarılı.'
        }
    }
}

