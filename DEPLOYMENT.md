# Azure Deployment Guide for uportal-messaging-v2

This guide explains how to deploy the uportal-messaging-v2 Spring Boot microservice to Azure Container Apps using Azure Developer CLI (azd).

## Prerequisites

Before deploying, ensure you have the following installed:

1. **Azure CLI** - [Install instructions](https://learn.microsoft.com/en-us/cli/azure/install-azure-cli)
2. **Azure Developer CLI (azd)** - [Install instructions](https://learn.microsoft.com/en-us/azure/developer/azure-developer-cli/install-azd)
3. **Docker** - [Install instructions](https://docs.docker.com/get-docker/)
4. **Java 17** - Required for building the application
5. **Maven** - For building the Spring Boot application

## Architecture

The deployment includes the following Azure resources:

- **Azure Container Apps**: Hosts the containerized Spring Boot application
- **Azure Container Registry**: Stores and manages Docker images
- **User-Assigned Managed Identity**: Provides secure authentication to ACR
- **Log Analytics Workspace**: Collects logs and diagnostics
- **Application Insights**: Tracks application performance and telemetry

## Quick Start

### 1. Build the Application

```bash
mvn clean package
```

### 2. Login to Azure

```bash
az login
azd auth login
```

### 3. Initialize the Environment

```bash
# Create a new environment
azd env new <your-environment-name>

# Set the subscription (use default or specify one)
azd env set AZURE_SUBSCRIPTION_ID $(az account show --query id -o tsv)

# Set the location (e.g., eastus, westus2, etc.)
azd env set AZURE_LOCATION eastus2
```

### 4. Create Resource Group

```bash
# Create a resource group for the deployment
az group create --name rg-uportal-messaging --location eastus2

# Set the resource group environment variable
azd env set AZURE_RESOURCE_GROUP rg-uportal-messaging
```

### 5. Deploy to Azure

```bash
# Preview the infrastructure
azd provision --preview

# Deploy application and infrastructure
azd up --no-prompt
```

The `azd up` command will:
- Provision all Azure resources defined in `infra/main.bicep`
- Build the Docker image
- Push the image to Azure Container Registry
- Deploy the container to Azure Container Apps

### 6. Access Your Application

After deployment completes, azd will output the application URL:

```
AZURE_CONTAINER_APP_URL: https://<your-app>.azurecontainerapps.io
```

Test the endpoints:
- Health check: `https://<your-app>.azurecontainerapps.io/`
- Messages API: `https://<your-app>.azurecontainerapps.io/messages`
- Admin API: `https://<your-app>.azurecontainerapps.io/admin/allMessages`

## GitHub Actions CI/CD

This repository includes a GitHub Actions workflow (`.github/workflows/azure-deploy.yml`) for automated deployment.

### Setup GitHub Secrets

Configure the following secrets in your GitHub repository:

1. Go to your repository → Settings → Secrets and variables → Actions
2. Add the following secrets:
   - `AZURE_CLIENT_ID`: Azure service principal client ID
   - `AZURE_TENANT_ID`: Azure tenant ID
   - `AZURE_SUBSCRIPTION_ID`: Azure subscription ID
   - `AZURE_ENV_NAME`: Environment name (e.g., "dev", "prod")
   - `AZURE_LOCATION`: Azure region (e.g., "eastus2")

### Create Azure Service Principal

```bash
# Create a service principal with contributor role
az ad sp create-for-rbac --name "github-actions-uportal-messaging" \
  --role contributor \
  --scopes /subscriptions/<subscription-id>/resourceGroups/rg-uportal-messaging \
  --sdk-auth
```

Use the output JSON to populate the GitHub secrets.

## Project Structure

```
.
├── src/                          # Java source code
├── infra/                        # Azure infrastructure as code
│   ├── main.bicep               # Main Bicep template
│   └── main.parameters.json     # Bicep parameters
├── .azure/                       # Azure deployment metadata
│   ├── plan.copilotmd           # Deployment plan
│   └── progress.copilotmd       # Deployment progress tracking
├── azure.yaml                    # Azure Developer CLI configuration
├── Dockerfile                    # Multi-stage Docker build
├── .dockerignore                # Docker build exclusions
└── .github/
    └── workflows/
        └── azure-deploy.yml     # GitHub Actions workflow

```

## Configuration

### Environment Variables

The application uses the following environment variable:
- `message.source`: Location of the messages JSON file (default: `classpath:messages.json`)

Additional environment variables can be added to the Container App via `infra/main.bicep`.

### Scaling

The Container App is configured to:
- Scale to zero when idle (cost-effective)
- Scale up to 10 replicas based on HTTP traffic
- Use 0.5 vCPU and 1.0 GB memory per replica

Adjust scaling in `infra/main.bicep` under `template.scale`.

## Monitoring

### Application Insights

View application telemetry:
1. Go to Azure Portal
2. Navigate to your Application Insights resource
3. Explore metrics, logs, and performance data

### Container App Logs

View real-time logs:
```bash
# Using Azure CLI
az containerapp logs show \
  --name <container-app-name> \
  --resource-group rg-uportal-messaging \
  --follow

# Using azd
azd logs
```

## Cleanup

To remove all deployed resources:

```bash
# Delete all Azure resources
azd down --force --purge

# Optionally, delete the resource group
az group delete --name rg-uportal-messaging --yes
```

## Troubleshooting

### Build Fails

- Ensure Java 17 is installed and configured
- Run `mvn clean package` locally to verify the build works

### Deployment Fails

- Check Azure CLI authentication: `az account show`
- Verify resource group exists: `az group show --name rg-uportal-messaging`
- Review Bicep validation: `az deployment group validate --resource-group rg-uportal-messaging --template-file infra/main.bicep`

### Container App Not Starting

- Check container logs in Azure Portal
- Verify the Docker image was built correctly
- Ensure the health check endpoint (`/`) returns a 200 status

## Additional Resources

- [Azure Container Apps documentation](https://learn.microsoft.com/en-us/azure/container-apps/)
- [Azure Developer CLI documentation](https://learn.microsoft.com/en-us/azure/developer/azure-developer-cli/)
- [Spring Boot on Azure](https://learn.microsoft.com/en-us/azure/developer/java/spring-framework/)

## Support

For issues or questions:
1. Check the [Azure deployment plan](.azure/plan.copilotmd)
2. Review [deployment progress](.azure/progress.copilotmd)
3. Consult Azure Container Apps troubleshooting guides
