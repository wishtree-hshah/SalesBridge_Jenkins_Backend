pipeline {
    agent any

    environment {
        GOOGLE_CHAT_WEBHOOK = credentials('google_chat_webhook')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Spring Boot') {
            when {
                branch 'develop'
            }
            steps {
                sh './mvnw clean install' // or 'mvn clean install' if you’re not using wrapper
            }
        }

        stage('Notify on Merge to Develop') {
            when {
                branch 'develop'
            }
            steps {
                script {
                    // Check if this was a merge commit
                    def isMerge = sh(script: "git log -1 --pretty=%P | wc -w", returnStdout: true).trim().toInteger() > 1

                    if (isMerge) {
                        def msg = """
                        {
                          "text": "🚀 A branch was merged into *develop* in project `${env.JOB_NAME}`.\n🔀 Commit: `${env.GIT_COMMIT}`"
                        }
                        """
                        sh """
                        curl -X POST -H 'Content-Type: application/json' \
                        -d '${msg}' "${GOOGLE_CHAT_WEBHOOK}"
                        """
                    } else {
                        echo "This was not a merge commit into develop."
                    }
                }
            }
        }
    }
}
