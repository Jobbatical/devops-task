# Readme (kubernetes-jenkins-node_service) 
**Node Service**
This directory represents the SCM repo for a microservice on node which is ready deployable for kubernetes via jenkins 
```
- Dockerfile: configuration for the build image
- Jenkinsfile: To trigger jenkins pipeline job for kubernetes deployment
- k8s/*: configs for kubernetes deployment to be used with kubectl 
```

**Kube-up-aws**

SCM repo for automated provisioning of kubernetes cluster in AWS using kops can also be exported as terraform from kops
```
./01-create.sh
./02-validate.sh
```

**Jenkins**
Repo for jenkins job scripts and kubernetes resource deployment templates used with kubectl to create [service,ingress,autoscaler and deployment]
from rendered templates given along the microservice 
```
- k8s/deployment.json
- k8s/hpa.yaml
- k8s/service.yaml
- k8s/service-ingress-template.yaml
- jenkins.groovy [called from Jenkinsfile in root of service GIT repo] 
```