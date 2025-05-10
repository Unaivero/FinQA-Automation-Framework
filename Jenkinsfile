pipeline {
    agent any
    
    tools {
        maven 'Maven 3.8.6'
        jdk 'JDK 11'
    }
    
    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'], description: 'Browser to run tests on')
        choice(name: 'ENVIRONMENT', choices: ['dev', 'qa', 'staging', 'prod'], description: 'Environment to run tests against')
        booleanParam(name: 'RUN_API_TESTS', defaultValue: true, description: 'Run API tests')
        booleanParam(name: 'RUN_UI_TESTS', defaultValue: true, description: 'Run UI tests')
        booleanParam(name: 'RUN_VISUAL_TESTS', defaultValue: false, description: 'Run visual validation tests')
        string(name: 'TEST_TAGS', defaultValue: 'not @wip', description: 'Cucumber tags to run')
    }
    
    options {
        ansiColor('xterm')
        timestamps()
        timeout(time: 60, unit: 'MINUTES')
        disableConcurrentBuilds()
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        
        stage('Test') {
            parallel {
                stage('API Tests') {
                    when {
                        expression { return params.RUN_API_TESTS }
                    }
                    steps {
                        sh "mvn test -Dtest=AccountAPITest -Denvironment=${params.ENVIRONMENT}"
                    }
                }
                
                stage('UI Tests') {
                    when {
                        expression { return params.RUN_UI_TESTS }
                    }
                    steps {
                        sh "mvn test -Dtest=CucumberRunner -Dbrowser=${params.BROWSER} -Denvironment=${params.ENVIRONMENT} -Dcucumber.filter.tags='${params.TEST_TAGS}'"
                    }
                }
                
                stage('Visual Tests') {
                    when {
                        expression { return params.RUN_VISUAL_TESTS }
                    }
                    steps {
                        sh "mvn test -Dtest=CucumberRunner -Dbrowser=${params.BROWSER} -Denvironment=${params.ENVIRONMENT} -Dcucumber.filter.tags='@visual'"
                    }
                }
            }
        }
        
        stage('Generate Reports') {
            steps {
                sh 'mvn allure:report'
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/site/allure-maven-plugin',
                    reportFiles: 'index.html',
                    reportName: 'Allure Report',
                    reportTitles: 'Allure Report'
                ])
                
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/cucumber-reports',
                    reportFiles: 'cucumber-html-report.html',
                    reportName: 'Cucumber Report',
                    reportTitles: 'Cucumber Report'
                ])
            }
        }
    }
    
    post {
        always {
            // Archive test artifacts
            archiveArtifacts artifacts: 'test-output/**/*.*', allowEmptyArchive: true
            archiveArtifacts artifacts: 'target/cucumber-results/**/*.*', allowEmptyArchive: true
            archiveArtifacts artifacts: 'target/allure-results/**/*.*', allowEmptyArchive: true
            
            // Clean up workspace
            cleanWs(
                cleanWhenNotBuilt: true,
                deleteDirs: true,
                disableDeferredWipeout: true,
                notFailBuild: true
            )
        }
        
        success {
            // Send success notification
            emailext (
                subject: "SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """<p>SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'</p>
                <p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a></p>
                <p>Test Summary: All tests passed</p>""",
                recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']]
            )
        }
        
        failure {
            // Send failure notification
            emailext (
                subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """<p>FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'</p>
                <p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a></p>
                <p>Test Summary: Some tests failed. Check the reports for details.</p>""",
                recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']]
            )
        }
    }
}