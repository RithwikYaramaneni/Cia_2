# Attendify

Attendify is an attendance, marks and grades management system built as the Java application for the CIA 2 end-to-end CI/CD exercise. It provides a responsive dashboard, REST endpoints, attendance recording, grade calculation and at-risk student detection.

## Run locally

```bash
mvn spring-boot:run
```

Open `http://localhost:8080`. The API is available at `GET /api/dashboard`, `GET /api/students`, and `POST /api/students/{id}/attendance` with `{"present": true}` or `{"present": false}`.

## Pipeline

`.github/workflows/ci-cd.yml` runs on every push to `main` and has these stages:

1. Checkout source.
2. Set up Java 17, compile, run unit tests and package with Maven.
3. Build and push immutable SHA-tagged and `latest` Docker images to GHCR.
4. Configure the target Kubernetes cluster from the `KUBE_CONFIG` GitHub secret.
5. Apply Kubernetes manifests, update the deployment image, wait for rollout and verify pods/service.

The workflow uses a pull-request-safe build job; image publishing and deployment only occur on a push to `main`.

## Deployment

Replace `OWNER/REPOSITORY` in `k8s/deployment.yaml` with the lowercase GitHub image path. For a cluster, configure `KUBE_CONFIG` as a GitHub Actions environment secret and grant the cluster permission to pull the private GHCR image (or make the image public). Then the `LoadBalancer` service exposes port 80.

The optional `ansible/deploy.yml` applies the same manifests from a configured control-plane host.

## Jenkins pipeline

`Jenkinsfile` provides the equivalent Jenkins pipeline for a local Docker Desktop Kubernetes cluster:

1. Checkout source code.
2. Run Maven compilation and all 15 unit tests.
3. Publish JUnit results in Jenkins.
4. Build the `latest` Docker image for the local Docker Desktop engine.
5. Apply the Kubernetes manifests and update the deployment image.
6. Wait for the Kubernetes rollout and display deployment evidence.

### Jenkins prerequisites

The Jenkins agent must have Java 17, Maven, Docker Desktop, Docker CLI, and `kubectl` installed. It must also be able to access the Docker Desktop Kubernetes context:

```bash
kubectl config use-context docker-desktop
kubectl get nodes
```

Create a **Pipeline** job in Jenkins, select **Pipeline script from SCM**, select Git, and use:

```text
https://github.com/RithwikYaramaneni/Cia_2.git
```

Set the branch to `*/main` and the script path to `Jenkinsfile`. Run the job with **Build Now**. The Jenkins agent must run on the same Mac as Docker Desktop, because a cloud Jenkins agent cannot access the local Kubernetes cluster.

The local dashboard can be exposed after a successful deployment with:

```bash
kubectl -n attendify port-forward service/attendify 8082:80
```

Then open `http://localhost:8082`.

The Jenkins pipeline intentionally uses `attendify:latest` and restarts the deployment instead of using a build-number image tag. Docker Desktop Kubernetes uses the local Docker engine, while a build-number tag may not be visible to the Kubernetes node runtime.
