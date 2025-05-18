pipeline {
    agent any

    environment {
        GOOGLE_CHAT_WEBHOOK = credentials('GOOGLE_CHAT_WEBHOOK')
    }

    stages {
        stage('Check and Notify') {
            steps {
                script {
                    // More reliable branch detection in detached HEAD
                    def branch = sh(script: "git name-rev --name-only HEAD", returnStdout: true).trim()
                    echo "Detected branch: ${branch}"

                    if (branch.contains('develop')) {
                        def commitMessage = sh(script: "git log -1 --pretty=%B", returnStdout: true).trim()
                        def author = sh(script: "git log -1 --pretty=format:'%an'", returnStdout: true).trim()
                        def message = """
                        {
                          "text": "*Git Merge Notification*\\n
                          Author: ${author}\\n
                          Commit: ${commitMessage}\\n
                          Branch: ${branch}\\n
                          Info: Merged into develop"
                        }
                        """
                        sh """
                          curl -X POST -H 'Content-Type: application/json' \
                          -d '${message}' \
                          '${GOOGLE_CHAT_WEBHOOK}'
                        """
                    } else {
                        echo "Not develop branch. Skipping notification."
                    }
                }
            }
        }
    }
}
