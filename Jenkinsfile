pipeline {
    agent any

    environment {
        GIT_CREDENTIALS_ID = 'github-token'
        GIT_REPO_URL = 'https://github.com/baha-es/Groupe1-2ALINFO5-2425.git'
        GIT_BRANCH = 'Mohamed-Amine-Chouria-Universite'
        SONARQUBE_SERVER = 'SonarQube-Server'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: "${GIT_BRANCH}",
                    credentialsId: "${GIT_CREDENTIALS_ID}",
                    url: "${GIT_REPO_URL}"
            }
        }

        stage('Clean') {
            steps {
                sh 'mvn clean'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn compile'
            }
        }

        stage('Test + Coverage Check') {
            steps {
                sh 'mvn verify'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE_SERVER}") {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package'
            }
        }

        stage('Deploy to Nexus') {
            steps {
                sh 'mvn deploy'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t foyer:1.4.0 .'
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-token', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker tag foyer:1.4.0 $DOCKER_USER/foyer:1.4.0
                        docker push $DOCKER_USER/foyer:1.4.0
                    '''
                }
            }
        }

        stage('Docker Compose Up') {
            steps {
                sh '''
                    docker compose -f docker-compose.yml down || true
                    docker compose -f docker-compose.yml up -d --build
                    sleep 20
                '''
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }

        success {
            script {
                def jacocoReport = fileExists('target/site/jacoco/index.html')
                if (jacocoReport) {
                    publishHTML(target: [
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage'
                    ])
                } else {
                    echo 'JaCoCo HTML report not found — skipping report publish.'
                }
            }
        }

        failure {
            echo 'Build failed.'
        }
    }
}