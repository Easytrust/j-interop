#!groovy

def selectNode() {
        ( env.BRANCH_NAME.contains("master") ) ? 'easytrustdev1' : 'easytrustdev2'
}

// Only keep the 10 most recent builds.
properties([[$class: 'jenkins.model.BuildDiscarderProperty', strategy: [$class: 'LogRotator', daysToKeepStr: '5', numToKeepStr: '5']]])

try
{
	// Build a Preproduction branch in order to prepare a production deployment.
	def selectedNode = selectNode()
	timestampedNode(selectedNode)
	{
		withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'f80975de-35c6-41e5-a506-9e46684a0147', passwordVariable: 'REPO_SNAPSHOT_PASSWORD', usernameVariable: 'REPO_SNAPSHOT_USERNAME']])
		{
			// See below for what this method does - we're passing an arbitrary environment
			// variable to it so that JAVA_OPTS and MAVEN_OPTS are set correctly.
			withMavenEnv(["JAVA_OPTS=-Xmx1536m -Xms512m -XX:MaxPermSize=1024m -Djavax.net.ssl.trustStore=/release/java/security/cacerts -Djavax.net.ssl.trustStorePassword=changeit",
                  "MAVEN_OPTS=-Xmx1536m -Xms512m -XX:MaxPermSize=1024m -Djavax.net.ssl.trustStore=/release/java/security/cacerts -Djavax.net.ssl.trustStorePassword=changeit",
				          "REPO_SNAPSHOT_USERNAME=${env.REPO_SNAPSHOT_USERNAME}",
				          "REPO_SNAPSHOT_PASSWORD=${env.REPO_SNAPSHOT_PASSWORD}"])
			{
				// Checkout
				stage 'Checking-out j-interop'
				checkout scm

				// Build J-Interop
				stage "Clean Maven local repository"
				echo "PATH= ${PWD}/.repository"
				sh 'rm -Rf ${PWD}/.repository'

				stage "Building J-Interop"

				/*
				    Feature branches treatment
				    Maven goal(s): clean install
				    Artefact(s) deployment on Artifactory: No
				    Artefacts deployment on WildFly: No
				*/
				if (env.BRANCH_NAME.contains("feature/"))
				{
				    stage ('Build Branch')
				    sh 'mvn -Pdev2 -V -B -s settings.xml clean install -Dmaven.repo.local=${PWD}/.repository'
				}

				/*
				    PR branches treatment
				    Maven goal(s): clean deploy
				    Artefact(s) deployment on Artifactory: yes
				    Artefacts deployment on WildFly: No
				*/
				if (env.BRANCH_NAME.contains("PR-"))
				{
				    stage ('Build PR + Test')
				    sh 'mvn -Pdev2 -V -B -s settings.xml clean deploy -Dmaven.repo.local=${PWD}/.repository'
				}

				/*
				    Develop branch treatment
				    Maven goal(s): clean deploy
				    Artefact(s) deployment on Artifactory: yes
				    Artefacts deployment on WildFly: No
				*/
				if (env.BRANCH_NAME == "develop" )
				{
				    stage ('Deploy Artefacts')
				    sh 'mvn -Pdev2 -V -B -s settings.xml clean deploy -Dmaven.repo.local=${PWD}/.repository'
				}
			}
		}
	}
}

catch (err)
{
	stage 'Send Error Notification'
	mail (  to: 'stephane.ricci@aspera.com, v-af-dev@aspera.com',
            subject: "Pipeline '${env.JOB_NAME}' - Build #${env.BUILD_NUMBER} - FAILED!",
            body: "<b>Pipeline '${env.JOB_NAME}' - #${env.BUILD_NUMBER} - FAILED!</b><p>To view the results, please check console output at ${env.BUILD_URL}console</p><p>Have a nice day!:-(</p>",
            mimeType:'text/html');
        currentBuild.result = 'FAILURE'
}

// This method sets up the Maven and JDK tools, puts them in the environment along
// with whatever other arbitrary environment variables we passed in, and runs the
// body we passed in within that environment.
void withMavenEnv(List envVars = [], def body) {
        // The names here are currently hardcoded for my test environment. This needs to be made more flexible.
        // Using the "tool" Workflow call automatically installs those tools on the node.
        String mvntool = tool name: "MAVEN 3.3", type: 'hudson.tasks.Maven$MavenInstallation'
        String jdktool = tool name: "JDK1.8", type: 'hudson.model.JDK'

        // Set JAVA_HOME, MAVEN_HOME and special PATH variables for the tools we're using.
        List mvnEnv = ["PATH+MVN=${mvntool}/bin", "PATH+JDK=${jdktool}/bin", "JAVA_HOME=${jdktool}", "MAVEN_HOME=${mvntool}"]

        // Add any additional environment variables.
        for (String item : envVars)
		{
            mvnEnv.add(item)
        }

        // Invoke the body closure we're passed within the environment we've created.
        withEnv(mvnEnv)
		{
            body.call()
        }
}

// Runs the given body within a Timestamper wrapper on the given label.
def timestampedNode(String label, Closure body) {
	node(label)
	{
		wrap([$class: 'TimestamperBuildWrapper'])
		{
			body.call()
		}
	}
}
