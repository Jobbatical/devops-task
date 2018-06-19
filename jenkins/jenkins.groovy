import groovy.json.JsonSlurperClassic
import groovy.json.JsonOutput
import com.tikal.jenkins.plugins.multijob.*
import hudson.*
import hudson.model.*
import hudson.plugins.git.*
import hudson.slaves.*
import hudson.tasks.*

def version = '1.0'

def lastSuccessfulBuild(passedBuilds, build) {
  if ((build != null) && (build.result != 'SUCCESS')) {
      passedBuilds.add(build)
      lastSuccessfulBuild(passedBuilds, build.getPreviousBuild())
   }
}


@NonCPS
def jsonParse(def json) {
    new groovy.json.JsonSlurperClassic().parseText(json)
}

def start(testCommand, slackUser = "", slackChannel = "", coverageCommand = "") {


	def project = "${env.JOB_NAME}".tokenize("/")[1];
	def messageParams = "of ${project} on branch ${env.BRANCH_NAME} with build #${env.BUILD_NUMBER}"
	def devopsConfig
	def configJson
	def testResource = "test-${project}"
	def stagingResource = "staging-${project}"
	def prodResource = "prod-${project}"

	node {
		echo 'Hello from Pipeline'
		sh("env | sort")
		stage ('Checkout') {
			checkout scm
			def configText
			try {
				configText = readFile('./k8s/config.json')
				devopsConfig = jsonParse(readFile('./k8s/resources.json'))
				sh("echo ${devopsConfig.test.replicas}")
			}
			catch (err) {
				throw err
			}
			echo configText;
			try {
				configJson = jsonParse(configText)
			}
			catch (err) {
				throw err
			}
		}
		def app
		stage ('Build') {

			try {
				app = docker.build(project);
			}
			catch (err) {
				throw err
			}
		}

		milestone 1
		stage ('Deploy to Test Server') {
				// milestone 2
				def imageTag = "${project}:${env.BRANCH_NAME}.${configJson.version}.b${env.BUILD_NUMBER}"
				echo imageTag
				sh("docker tag ${project} ${imageTag}")
				echo "Uploading to DOCKER REPO..."
				try {
					docker.withRegistry("${configJson.dockerRepo}") {
					  docker.image(imageTag).push()
					}
					echo "Uploaded to DOCKER REPO."
				}
				catch(err) {
					throw err
				}
				echo "Templating"

				if(configJson.autoConfig) {
					echo "Autoconfig present"

					try {
						sh("sed -i.bak 's#{{service-name}}#${project}#' ./k8s/*")
						sh("sed -i.bak 's#{{version}}#${configJson.version}#' ./k8s/*")
						sh("sed -i.bak 's#{{port}}#${configJson.port}#' ./k8s/*")

						def environmentText = JsonOutput.toJson(configJson.environment)
						echo environmentText
						if (environmentText == "[]") {
							environmentText = ""
						} else {
							environmentText = ", ${environmentText[1..-2]}"
						}
						sh("sed -i.bak 's#{{environment}}#${environmentText}#' ./k8s/*")

						def labelsText = JsonOutput.toJson(configJson.labels)
						echo labelsText
						if(labelsText == "{}") {
							labelsText = ""
						} else {
							labelsText = ", ${labelsText[1..-2]}"
						}
						sh("sed -i.bak 's#{{labels}}#${labelsText}#' ./k8s/*")

					}
					catch (err) {
						throw err
					}
				}
				else {
					echo "No Autoconfig present"
				}

				echo "Deploying to test server"
				try {
					// Make copies to be used later
					sh("cp ./k8s/deployment.json ./deployment.json")
					sh("cp ./k8s/service.yaml ./service.yaml")
					sh("cp ./k8s/hpa.yaml ./hpa.yaml")

					sh("sed -i.bak 's#{{namespace}}#{{environment}}#' ./k8s/deployment.json")

					sh("sed -i.bak 's#{{replicas}}#${devopsConfig.test.replicas}#' ./k8s/*")
					def maxSurge = devopsConfig.test.replicas + 2;
					sh("sed -i.bak 's#{{maxSurge}}#${maxSurge}#' ./k8s/*")
					def minReplicas = 1;
					try {
						minReplicas = devopsConfig.test.minReplicas;	
					}
					catch(Exception e) {
						
					}
					
					// if (devopsConfig.test.hasProperty("minReplicas")) {
					// 	minReplicas = devopsConfig.test.minReplicas;
					// }
					sh("sed -i.bak 's#{{minReplicas}}#${minReplicas}#' ./k8s/*")
					sh("sed -i.bak 's#{{type}}#${devopsConfig.test.type}#' ./k8s/*")
					def resourcesText = JsonOutput.toJson(devopsConfig.test.resources)
					sh("sed -i.bak 's#{{resources}}#${resourcesText}#' ./k8s/*")
					sh("sed -i.bak 's#{{nodeSelector}}#${testNodeSelector}#' ./k8s/*")
					sh("/usr/local/bin/kubectl --namespace=test --context=aws_kubernetes apply -f k8s/");
					sh("/usr/local/bin/kubectl --namespace=test --context=aws_kubernetes get service/${project} --output=yaml")
					echo "Deployed to test server"
				}
				catch (err) {
					throw err
				}
		}


	}
	}


return this;
