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
