pipeline {
    agent any

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

        stage('Build') {
            steps {
                bat 'java -version'
                bat 'javac -version'
                bat 'echo JAVA_HOME=%JAVA_HOME%'
                bat 'mvn compile'
            }
        }

        stage('Test') {
            steps {
                echo "Город доставки: ${params.DELIVERY_CITY}"
                bat 'mvn test'
            }
        }

        stage('Package') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }

    }
}