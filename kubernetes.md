[Back to Table of Contents](README.md)

`minikube start`

`minikube dashboard &`: startet im background das dashboard-server und öffnet zugehörige URL im Browser

`kubectl create deployment hello-minikube --image=k8s.gcr.io/echoserver:1.10`: create a deplyment

`kubectl expose deployment hello-minikube --type=NodePort --port=8080`: expose the deployment
oder
`kubectl expose deployment hello-minikube --type=LoadBalancer --port=8080`: expose outside of cluster

`kubectl get pod`: show status

`kubectl get pod,svc -n kube-system`: get pods and services in the "kube-system" namespace

`kubectl get pod,svc -n default`: get pods and services in the "default" namespace


`minikube service hello-minikube --url`: get URL of an already deployed and exposed service

`kubectl delete services hello-minikube`: delete the serice but not the deployment 

`kubectl delete deployment hello-minikube` delete deployment

`minikube stop`

`minikube delete` : delete the cluster

# About container runtime
* docker (uses internally containerd)
* containerd without docker
* kata containers (VMs on OpenStack) using Dockerfiles (hypervisor: QEMU), better isolation for containers
* firecracker (Amazon) also better isolation, uses KVM (Linux Kernel-based Virtual Machine)Firecracker is a Virtual Machine Manager like QEMU.
* gVisor from Google with "Sentry" as a user-space OS Kernel , also Docker-compatible
* CRI-O: lightweight container runtime for Kubernetes Fascade for any OCI runtime compliant software * Trend: eliminate Docker from Kubernetes installations, but still using Dockerfile and docker compatible images
* Conclusion: **kata** might be more secure

# add-ons
`minikube addons list`: dashboard, storage-provisioner etc.
