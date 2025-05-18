pipeline {
    agent any

    environment {
        GOOGLE_CHAT_WEBHOOK = credentials('GOOGLE_CHAT_WEBHOOK')
    }

    stages {
        stage('Check Branch') {
            when {
                branch 'develop'
            }
            steps {
                script {
                    def commitMessage = sh(script: "git log -1 --pretty=%B", returnStdout: true).trim()
                    def author = sh(script: "git log -1 --pretty=format:'%an'", returnStdout: true).trim()
                    def sourceBranch = sh(script: "git log -1 --pretty=%D", returnStdout: true).trim()

                    def message = """
                    {
                      "text": "*Git Merge Notification*\\n
                      Author: ${author}\\n
                      Commit: ${commitMessage}\\n
                      Branch: ${env.BRANCH_NAME}\\n
                      Info: Merged into develop"
                    }
                    """

                    sh """
                      curl -X POST -H 'Content-Type: application/json' \
                      -d '${message}' \
                      '${GOOGLE_CHAT_WEBHOOK}'
                    """
                }
            }
        }
    }
}
