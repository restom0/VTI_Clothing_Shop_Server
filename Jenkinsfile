pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '20'))
        skipDefaultCheckout(true)
        timeout(time: 60, unit: 'MINUTES')
        timestamps()
    }

    parameters {
        booleanParam(name: 'RUN_SONAR', defaultValue: true, description: 'Run SonarCloud analysis when organization and project key are configured.')
        string(name: 'SONAR_HOST_URL', defaultValue: 'https://sonarcloud.io', description: 'SonarCloud/SonarQube host URL.')
        string(name: 'SONAR_ORGANIZATION', defaultValue: '', description: 'SonarCloud organization. Leave empty to skip SonarCloud safely.')
        string(name: 'SONAR_PROJECT_KEY', defaultValue: '', description: 'SonarCloud project key. Leave empty to skip SonarCloud safely.')
        string(name: 'SONAR_TOKEN_CREDENTIAL_ID', defaultValue: 'sonar-token', description: 'Jenkins secret text credential id for the Sonar token.')

        booleanParam(name: 'RUN_DEPENDENCY_CHECK', defaultValue: true, description: 'Run OWASP Dependency-Check and archive vulnerability reports.')
        string(name: 'NVD_API_KEY_CREDENTIAL_ID', defaultValue: '', description: 'Optional Jenkins secret text credential id for the NVD API key.')

        booleanParam(name: 'BUILD_DOCKER_IMAGE', defaultValue: true, description: 'Build the Docker image after Maven verify.')
        string(name: 'DOCKER_REGISTRY', defaultValue: '', description: 'Optional registry host, for example registry.example.com/team. Leave empty for local image only.')
        string(name: 'DOCKER_IMAGE_NAME', defaultValue: 'vti-clothing-shop-server', description: 'Docker image name without tag.')
        booleanParam(name: 'PUSH_DOCKER_IMAGE', defaultValue: false, description: 'Push image tags to DOCKER_REGISTRY. Requires Docker registry credentials.')
        string(name: 'DOCKER_REGISTRY_CREDENTIAL_ID', defaultValue: 'docker-registry-credentials', description: 'Jenkins username/password credential id for Docker registry.')

        booleanParam(name: 'DEPLOY_K8S', defaultValue: false, description: 'Optional Kubernetes deploy. Requires a manual confirmation step.')
        choice(name: 'DEPLOY_ENV', choices: ['int', 'qa', 'production'], description: 'Spring profile to set on the Kubernetes deployment.')
        booleanParam(name: 'APPLY_MONITORING', defaultValue: false, description: 'Also apply k8s/monitoring before the package deployment.')
        string(name: 'KUBECONFIG_CREDENTIAL_ID', defaultValue: 'kubeconfig', description: 'Jenkins secret file credential id containing kubeconfig.')
        string(name: 'K8S_APP_SECRET_ENV_CREDENTIAL_ID', defaultValue: 'clothing-shop-app-secret-env', description: 'Jenkins secret file credential id for k8s/package/app-secret.env.')
        string(name: 'K8S_GRAFANA_SECRET_ENV_CREDENTIAL_ID', defaultValue: 'grafana-secret-env', description: 'Jenkins secret file credential id for k8s/monitoring/grafana-secret.env.')
    }

    environment {
        MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository'
        SPRING_PROFILES_ACTIVE = 'int'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_SHA = captureCommand('git rev-parse --short=12 HEAD', 'git rev-parse --short=12 HEAD')
                    currentBuild.displayName = "#${env.BUILD_NUMBER} ${env.GIT_SHA}"
                }
            }
        }

        stage('Build and Test') {
            steps {
                runMaven('clean verify')
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'clothing_shop/target/surefire-reports/*.xml'
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'clothing_shop/target/*.jar,clothing_shop/target/site/jacoco/jacoco.xml'
                }
            }
        }

        stage('Dependency Vulnerability Check') {
            when {
                expression { return params.RUN_DEPENDENCY_CHECK }
            }
            steps {
                script {
                    def nvdCredentialId = params.NVD_API_KEY_CREDENTIAL_ID?.trim()
                    if (nvdCredentialId) {
                        withCredentials([string(credentialsId: nvdCredentialId, variable: 'NVD_API_KEY')]) {
                            runMaven('org.owasp:dependency-check-maven:check -DnvdApiKeyEnvironmentVariable=NVD_API_KEY')
                        }
                    } else {
                        runMaven('org.owasp:dependency-check-maven:check')
                    }
                }
            }
            post {
                always {
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'clothing_shop/target/dependency-check-report.*'
                }
            }
        }

        stage('SonarCloud Analysis') {
            when {
                expression {
                    return params.RUN_SONAR
                            && params.SONAR_ORGANIZATION?.trim()
                            && params.SONAR_PROJECT_KEY?.trim()
                }
            }
            steps {
                withCredentials([string(credentialsId: params.SONAR_TOKEN_CREDENTIAL_ID, variable: 'SONAR_TOKEN')]) {
                    withEnv([
                            "SONAR_HOST_URL=${params.SONAR_HOST_URL}",
                            "SONAR_ORGANIZATION=${params.SONAR_ORGANIZATION}",
                            "SONAR_PROJECT_KEY=${params.SONAR_PROJECT_KEY}"
                    ]) {
                        runCommand(
                                'cd clothing_shop && ./mvnw -B org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.host.url="$SONAR_HOST_URL" -Dsonar.organization="$SONAR_ORGANIZATION" -Dsonar.projectKey="$SONAR_PROJECT_KEY" -Dsonar.token="$SONAR_TOKEN" -Dsonar.qualitygate.wait=true',
                                'cd clothing_shop && mvnw.cmd -B org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.host.url="%SONAR_HOST_URL%" -Dsonar.organization="%SONAR_ORGANIZATION%" -Dsonar.projectKey="%SONAR_PROJECT_KEY%" -Dsonar.token="%SONAR_TOKEN%" -Dsonar.qualitygate.wait=true'
                        )
                    }
                }
            }
        }

        stage('Build Docker Image') {
            when {
                expression { return params.BUILD_DOCKER_IMAGE }
            }
            steps {
                script {
                    def imageName = requiredTrim(params.DOCKER_IMAGE_NAME, 'DOCKER_IMAGE_NAME')
                    def registry = params.DOCKER_REGISTRY?.trim()
                    def imagePrefix = registry ? "${registry}/${imageName}" : imageName
                    env.IMAGE_TAG = "${env.BUILD_NUMBER}-${env.GIT_SHA}"
                    env.IMAGE_REF = "${imagePrefix}:${env.IMAGE_TAG}"
                    env.LATEST_IMAGE_REF = "${imagePrefix}:latest"
                }
                withEnv(["IMAGE_REF=${env.IMAGE_REF}", "LATEST_IMAGE_REF=${env.LATEST_IMAGE_REF}"]) {
                    runCommand('docker version', 'docker version')
                    runCommand(
                            'docker build --pull -t "$IMAGE_REF" -t "$LATEST_IMAGE_REF" ./clothing_shop',
                            'docker build --pull -t "%IMAGE_REF%" -t "%LATEST_IMAGE_REF%" .\\clothing_shop'
                    )
                }
            }
        }

        stage('Push Docker Image') {
            when {
                expression {
                    return params.BUILD_DOCKER_IMAGE && params.PUSH_DOCKER_IMAGE
                }
            }
            steps {
                script {
                    requiredTrim(params.DOCKER_REGISTRY, 'DOCKER_REGISTRY')
                }
                withCredentials([usernamePassword(credentialsId: params.DOCKER_REGISTRY_CREDENTIAL_ID, usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                    withEnv([
                            "DOCKER_REGISTRY=${params.DOCKER_REGISTRY.trim()}",
                            "IMAGE_REF=${env.IMAGE_REF}",
                            "LATEST_IMAGE_REF=${env.LATEST_IMAGE_REF}"
                    ]) {
                        runCommand(
                                'printf "%s" "$DOCKER_PASSWORD" | docker login "$DOCKER_REGISTRY" --username "$DOCKER_USERNAME" --password-stdin',
                                'echo %DOCKER_PASSWORD% | docker login %DOCKER_REGISTRY% --username %DOCKER_USERNAME% --password-stdin'
                        )
                        runCommand('docker push "$IMAGE_REF"', 'docker push "%IMAGE_REF%"')
                        runCommand('docker push "$LATEST_IMAGE_REF"', 'docker push "%LATEST_IMAGE_REF%"')
                    }
                }
            }
        }

        stage('Deploy Kubernetes (manual)') {
            when {
                expression { return params.DEPLOY_K8S }
            }
            steps {
                script {
                    if (!env.IMAGE_REF?.trim()) {
                        error('DEPLOY_K8S requires BUILD_DOCKER_IMAGE so IMAGE_REF is available.')
                    }
                }
                input message: "Deploy ${env.IMAGE_REF} to Kubernetes with profile ${params.DEPLOY_ENV}?", ok: 'Deploy'
                withCredentials([
                        file(credentialsId: params.KUBECONFIG_CREDENTIAL_ID, variable: 'KUBECONFIG_FILE'),
                        file(credentialsId: params.K8S_APP_SECRET_ENV_CREDENTIAL_ID, variable: 'K8S_APP_SECRET_ENV_FILE')
                ]) {
                    withEnv([
                            "KUBECONFIG=${env.KUBECONFIG_FILE}",
                            "K8S_APP_SECRET_ENV_FILE=${env.K8S_APP_SECRET_ENV_FILE}",
                            "DEPLOY_IMAGE=${env.IMAGE_REF}",
                            "DEPLOY_ENV=${params.DEPLOY_ENV}"
                    ]) {
                        runCommand(
                                'cp "$K8S_APP_SECRET_ENV_FILE" k8s/package/app-secret.env',
                                'copy /Y "%K8S_APP_SECRET_ENV_FILE%" "k8s\\package\\app-secret.env"'
                        )
                        script {
                            if (params.APPLY_MONITORING) {
                                withCredentials([file(credentialsId: params.K8S_GRAFANA_SECRET_ENV_CREDENTIAL_ID, variable: 'K8S_GRAFANA_SECRET_ENV_FILE')]) {
                                    withEnv(["K8S_GRAFANA_SECRET_ENV_FILE=${env.K8S_GRAFANA_SECRET_ENV_FILE}"]) {
                                        runCommand(
                                                'cp "$K8S_GRAFANA_SECRET_ENV_FILE" k8s/monitoring/grafana-secret.env',
                                                'copy /Y "%K8S_GRAFANA_SECRET_ENV_FILE%" "k8s\\monitoring\\grafana-secret.env"'
                                        )
                                        runCommand('kubectl apply -k k8s/monitoring', 'kubectl apply -k k8s\\monitoring')
                                    }
                                }
                            }
                        }
                        runCommand('kubectl apply -k k8s/package', 'kubectl apply -k k8s\\package')
                        runCommand(
                                'kubectl -n vti-clothing-shop set env deployment/clothing-shop-server SPRING_PROFILES_ACTIVE="$DEPLOY_ENV"',
                                'kubectl -n vti-clothing-shop set env deployment/clothing-shop-server SPRING_PROFILES_ACTIVE="%DEPLOY_ENV%"'
                        )
                        runCommand(
                                'kubectl -n vti-clothing-shop set image deployment/clothing-shop-server server="$DEPLOY_IMAGE"',
                                'kubectl -n vti-clothing-shop set image deployment/clothing-shop-server server="%DEPLOY_IMAGE%"'
                        )
                        runCommand(
                                'kubectl -n vti-clothing-shop rollout status deployment/clothing-shop-server --timeout=180s',
                                'kubectl -n vti-clothing-shop rollout status deployment/clothing-shop-server --timeout=180s'
                        )
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline completed successfully. Docker image: ${env.IMAGE_REF ?: 'not built'}"
        }
        failure {
            echo 'Pipeline failed. Check the failed stage logs above.'
        }
    }
}

def runMaven(String arguments) {
    runCommand(
            "cd clothing_shop && chmod +x ./mvnw && ./mvnw -B ${arguments}",
            "cd clothing_shop && mvnw.cmd -B ${arguments}"
    )
}

def runCommand(String unixCommand, String windowsCommand) {
    if (isUnix()) {
        sh unixCommand
    } else {
        bat windowsCommand
    }
}

def captureCommand(String unixCommand, String windowsCommand) {
    if (isUnix()) {
        return sh(script: unixCommand, returnStdout: true).trim()
    }
    return bat(script: windowsCommand, returnStdout: true).trim().readLines().last().trim()
}

def requiredTrim(Object value, String name) {
    def text = value == null ? '' : value.toString().trim()
    if (!text) {
        error("${name} is required.")
    }
    return text
}
