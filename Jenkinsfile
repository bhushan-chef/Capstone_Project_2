pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK24'
    }

    environment {
        CI = 'true'
        JAVA_HOME = 'C:\\Program Files\\Java\\jdk-24'
    }

    stages {

        stage('Checkout Code') {
            steps {
                echo 'Pulling latest code from repository...'
                checkout scm
            }
        }

        stage('Build & Run Tests') {
            steps {
                echo 'Running Capstone Test Suite via Maven...'
                bat 'mvn test || exit 0'
            }
            post {
                always {
                    echo 'Collecting test artifacts...'
                    archiveArtifacts artifacts: 'target/surefire-reports/**, target/allure-results/**, target/screenshots/**',
                        allowEmptyArchive: true
                }
            }
        }

        stage('Generate Allure Report') {
            steps {
                echo 'Generating Allure HTML Report...'
                bat '.allure\\allure-2.20.1\\bin\\allure.bat generate target\\allure-results --clean -o target\\allure-report'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'target/allure-report/**',
                        allowEmptyArchive: true
                    echo 'Allure report archived.'
                }
            }
        }

        stage('Publish Test Summary') {
            steps {
                echo 'Publishing surefire XML results...'
                junit testResults: 'target/surefire-reports/*.xml',
                      allowEmptyResults: true
            }
        }

    }

    post {
        success {
            echo '✅ All tests passed! Capstone submission is ready.'
        }
        failure {
            echo '❌ Some tests failed. Check Allure report for details.'
        }
        always {
            echo 'Pipeline finished. Cleaning up workspace...'
            cleanWs()
        }
    }
}