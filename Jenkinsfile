import groovy.json.JsonOutput

pipeline {
    agent any

    environment {
        GOOGLE_CHAT_WEBHOOK = credentials('google_chat_webhook') // your Jenkins credential ID
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Check and Notify') {
            steps {
                script {
                    // Get current branch name
                    def branch = sh(script: "git rev-parse --abbrev-ref HEAD", returnStdout: true).trim()
                    echo "Detected branch: ${branch}"

                    if (branch == 'develop') {
                        // Get commit details
                        def commitMessage = sh(script: "git log -1 --pretty=%B", returnStdout: true).trim()
                        def author = sh(script: "git log -1 --pretty=format:'%an'", returnStdout: true).trim()

                        // Clean strings for JSON safety
                        def cleanCommitMessage = commitMessage.replace('\n', '\\n').replace('"', '\\"').trim()
                        def cleanAuthor = author.replace('"', '\\"').trim()
                        def cleanBranch = branch.replace('"', '\\"').trim()

                        // Build JSON message
                        def messageMap = [
                            text: """*Git Merge Notification*\n
*Author:* ${cleanAuthor}\n
*Commit:* ${cleanCommitMessage}\n
*Branch:* ${cleanBranch}\n
*Info:* Code has been merged into *develop*"""
                        ]

                        def jsonMessage = JsonOutput.toJson(messageMap)

                        // Send notification to Google Chat webhook
                        sh """
                        curl -X POST -H 'Content-Type: application/json' \
                             -d '${jsonMessage}' \
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
