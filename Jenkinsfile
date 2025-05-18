// pipeline {
//     agent any
//
//     environment {
//         GOOGLE_CHAT_WEBHOOK = credentials('GOOGLE_CHAT_WEBHOOK')
//     }
//
//     stages {
//         stage('Check and Notify') {
//             steps {
//                 script {
//                     // More reliable branch detection in detached HEAD
//                     def branch = sh(script: "git name-rev --name-only HEAD", returnStdout: true).trim()
//                     echo "Detected branch: ${branch}"
//
//                     if (branch.contains('develop')) {
//                         def commitMessage = sh(script: "git log -1 --pretty=%B", returnStdout: true).trim()
//                                                 def author = sh(script: "git log -1 --pretty=%an", returnStdout: true).trim()
//
//                                                 // Get second parent of the merge commit (source branch)
//                                                 def fromBranch = sh(script: "git log -1 --pretty=%P | cut -d' ' -f2 | xargs -I{} git name-rev --name-only {}", returnStdout: true).trim()
//
//                                                 def message = """{
//                           "text": "*Git Merge Notification*\\n\\n*From:* ${fromBranch}\\n*To:* develop\\n*Commit:* ${commitMessage}\\n*Merged by:* ${author}"
//                         }"""
//                         sh """
//                           curl -X POST -H 'Content-Type: application/json' \
//                           -d '${message}' \
//                           '${GOOGLE_CHAT_WEBHOOK}'
//                         """
//                     } else {
//                         echo "Not develop branch. Skipping notification."
//                     }
//                 }
//             }
//         }
//     }
// }


pipeline {
    agent any

    environment {
        GOOGLE_CHAT_WEBHOOK = credentials('GOOGLE_CHAT_WEBHOOK')
    }

    stages {
        stage('Check and Notify') {
            steps {
                script {
                    // Detect current branch
                    def branch = sh(script: "git name-rev --name-only HEAD", returnStdout: true).trim()
                    echo "Detected branch: ${branch}"

                    // Normalize branch name (remove remotes/origin/)
                    branch = branch.replaceAll('remotes/origin/', '')

                    // Get commit info
                    def commitMessage = sh(script: "git log -1 --pretty=%B", returnStdout: true).trim()
                    def author = sh(script: "git log -1 --pretty=%an", returnStdout: true).trim()

                    // Check if it is a merge commit (has two parents)
                    def parentCount = sh(script: "git log -1 --pretty=%P | wc -w", returnStdout: true).trim().toInteger()

                    def message = ""

                    if (branch == "develop") {
                        if (parentCount == 2) {
                            // Merge into develop
                            def fromBranch = sh(script: "git log -1 --pretty=%P | cut -d' ' -f2 | xargs -I{} git name-rev --name-only {}", returnStdout: true).trim()
                            message = """{
                              "text": "*Git Merge Notification*\\n\\n*From:* ${fromBranch}\\n*To:* develop\\n*Commit:* ${commitMessage}\\n*Merged by:* ${author}"
                            }"""
                        } else {
                            // Direct push to develop
                            message = """{
                              "text": "*Direct Push to Develop*\\n\\n*Branch:* develop\\n*Commit:* ${commitMessage}\\n*Author:* ${author}"
                            }"""
                        }
                    } else if (branch == "main") {
                        if (parentCount == 2) {
                            // Merge into main
                            def fromBranch = sh(script: "git log -1 --pretty=%P | cut -d' ' -f2 | xargs -I{} git name-rev --name-only {}", returnStdout: true).trim()
                            message = """{
                              "text": "*Git Merge Notification*\\n\\n*From:* ${fromBranch}\\n*To:* main\\n*Commit:* ${commitMessage}\\n*Merged by:* ${author}"
                            }"""
                        } else {
                            // Direct push to main
                            message = """{
                              "text": "*⚠️ Direct Push to Main*\\n\\n*Branch:* main\\n*Commit:* ${commitMessage}\\n*Author:* ${author}"
                            }"""
                        }
                    } else {
                        echo "Not develop or main branch. Skipping notification."
                    }

                    // Send notification if message is set
                    if (message) {
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
}
