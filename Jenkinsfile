pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        disableConcurrentBuilds()
    }

    triggers {
        pollSCM('* * * * *')
    }

    environment {
        JAVA_HOME = 'C:\\Program Files\\Java\\jdk-21.0.12'
        PATH = "${JAVA_HOME}\\bin;${env.PATH}"
    }

    parameters {
        string(
            name: 'DELIVERY_CITY',
            defaultValue: 'Moscow',
            description: 'Город доставки'
        )
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'mvn -B compile'
            }
        }

        stage('Static Analysis') {
            steps {
                bat 'mvn -B checkstyle:check'
            }
        }

        stage('Test') {
            steps {
                echo "Город доставки: ${params.DELIVERY_CITY}"
                bat 'mvn -B test'
            }
        }

        stage('Package') {
            steps {
                bat 'mvn -B package -DskipTests'
            }
        }
    }

    post {
        always {
            junit(
                testResults: 'target/surefire-reports/*.xml',
                allowEmptyResults: true
            )
            archiveArtifacts(
                artifacts: 'target/checkstyle-result.xml, target/logs/*.log, target/*.jar',
                allowEmptyArchive: true
            )
        }
    }
}