# Required
export AWS_ACCESS_KEY_ID="XXXXXXXXXXXXXXXXXXXXXXX"

# Required
export AWS_SECRET_ACCESS_KEY="XXXXXXXXXXXXXXXXXXXXXXX"

# Required
export KOPS_STATE_STORE="s3://cluster.kube.jobbatical.com"

# Required
export KOPS_CLUSTER_NAME="staging.kube.jobbatical.com"

export KOPS_CLUSTER_VPC="vpc-12345"

export KOPS_MASTER_SG="sg-05959c63,sg-c39099a5,sg-40b8ab26,sg-f38e3195"

export KOPS_NODE_SG="sg-05959c63,sg-c39099a5,sg-40b8ab26,sg-f38e3195"

# Required
export KOPS_CLUSTER_ZONES="ap-southeast-1a,ap-southeast-1b"

export KOPS_MASTER_ZONES="ap-southeast-1a"

# Optional
export KUBERNETES_VERSION="1.9.1"

# Optional
export KOPS_MASTER_SIZE="t2.large,m4.large"

# Optional
export KOPS_MASTER_COUNT="3"

# Optional
export KOPS_NODE_SIZE="t2.large,m4.large"

# Optional
export KOPS_NODE_COUNT="3"

# Required
export PROTOKUBE_IMAGE="http://xxxxxxxxxx/kubernetes/kops/v1.8.0-alpha.1/protokube/images/protokube.tar.gz"

# Required
export NODEUP_URL="http://xxxxxxxxxx/integrations/kubernetes/kops/v1.8.0-alpha.1/nodeup/linux/amd64/nodeup"
