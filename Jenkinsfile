pipeline {
    agent any

    tools {
        // These names must match what is configured in your Jenkins Global Tool Configuration
        maven 'Maven-3.9'
        jdk 'Java-24'
    }

    stages {
        stage('Checkout Code') {
            steps {
                // Pulls the latest code from your GitHub repository
                checkout scm
            }
        }

        stage('Execute Capstone Test Suite') {
            steps {
                echo 'Running the Parallel TestNG Suite via Maven...'
                // This triggers the testng.xml file we created earlier
                sh 'mvn clean test'
            }
            post {
                always {
                    echo 'Collecting Artifacts (Screenshots, Logs, Traces)...'
                    // Archives test results and any screenshots your framework takes on failure
                    archiveArtifacts artifacts: 'target/surefire-reports/**, target/allure-results/**', allowEmptyArchive: true
                }
            }
        }

        stage('Publish Allure HTML Report') {
            steps {
                echo 'Generating Allure Test Report...'
                script {
                    allure([
                        includeProperties: false,
                        jdk: '',
                        properties: [],
                        reportBuildPolicy: 'ALWAYS',
                        results: [[path: 'target/allure-results']]
                    ])
                }
            }
        }
    }

    post {
        success {
            echo 'Capstone Execution Passed! All tests are green.'
        }
        failure {
            echo 'Capstone Execution Failed! Check the Allure Report for defect details.'
        }
    }
}