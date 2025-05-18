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
                                                def author = sh(script: "git log -1 --pretty=%an", returnStdout: true).trim()
                                                def fromBranchMatch = (commitMessage =~ /Merge pull request #\d+ from [^\/]+\/([^\n]+)/)
                                                def fromBranch = fromBranchMatch ? fromBranchMatch[0][1] : "unknown"

                                                def message = """{
                          "text": "*Git Merge Notification*\\n\\n*From:* ${fromBranch}\\n*To:* develop\\n*Commit:* ${commitMessage}\\n*Merged by:* ${author}"
                        }"""

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
