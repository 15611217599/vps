# VPS服务器自动部署指南

## 概述
本项目配置了完整的CI/CD流程，支持代码提交时自动构建和部署到K8s集群。

## 自动部署流程

### GitHub Actions工作流
当你推送代码到`main`或`master`分支时，会自动触发以下流程：

1. **代码检出** - 获取最新代码
2. **Java环境设置** - 配置JDK 21
3. **Maven构建** - 编译和打包应用
4. **Docker镜像构建** - 构建并推送到GitHub Container Registry
5. **K8s部署** - 自动更新K8s集群中的应用

### 配置要求

#### 1. GitHub Secrets配置
在GitHub仓库的Settings > Secrets and variables > Actions中添加：

```
KUBECONFIG - K8s集群的配置文件（base64编码）
```

获取KUBECONFIG的方法：
```bash
# 获取当前kubeconfig并进行base64编码
cat ~/.kube/config | base64 -w 0
```

#### 2. 容器镜像仓库
- 使用GitHub Container Registry (ghcr.io)
- 自动使用GitHub Token进行认证
- 镜像命名格式：`ghcr.io/用户名/vps-server:标签`

## 手动部署

### 本地部署脚本
```bash
# 给脚本执行权限
chmod +x scripts/deploy-local.sh

# 执行部署
./scripts/deploy-local.sh
```

### 手动步骤
```bash
# 1. 构建应用
cd vps-server
./mvnw clean package -DskipTests

# 2. 构建镜像
docker build -t vps-server:latest .

# 3. 部署到K8s
kubectl apply -f k8s-deployment.yaml

# 4. 检查状态
kubectl get pods -n vps-server
```

## 部署架构

### K8s资源
- **Namespace**: `vps-server`
- **Deployment**: 2个副本，配置健康检查
- **Service**: ClusterIP服务用于集群内通信
- **ConfigMap**: 应用配置文件
- **Secret**: 敏感信息（密码、密钥等）
- **Ingress**: 外部访问配置

### 数据库连接
- PostgreSQL服务：`postgresql.postgresql.svc.cluster.local:5432`
- 数据库名：`vps`
- 用户名：`root`
- 密码：通过Secret管理

## 监控和调试

### 查看日志
```bash
kubectl logs -f deployment/vps-server -n vps-server
```

### 健康检查
```bash
kubectl exec -it deployment/vps-server -n vps-server -- curl localhost:8080/actuator/health
```

### 进入容器调试
```bash
kubectl exec -it deployment/vps-server -n vps-server -- /bin/bash
```

## 回滚部署
```bash
# 查看部署历史
kubectl rollout history deployment/vps-server -n vps-server

# 回滚到上一个版本
kubectl rollout undo deployment/vps-server -n vps-server

# 回滚到指定版本
kubectl rollout undo deployment/vps-server -n vps-server --to-revision=2
```

## 故障排除

### 常见问题
1. **镜像拉取失败** - 检查GitHub Token权限和镜像仓库设置
2. **数据库连接失败** - 确认PostgreSQL服务正常运行
3. **健康检查失败** - 检查应用启动日志和配置

### 调试命令
```bash
# 检查Pod状态
kubectl describe pod -l app=vps-server -n vps-server

# 检查Service
kubectl describe svc vps-server-service -n vps-server

# 检查ConfigMap
kubectl describe configmap vps-server-config -n vps-server
```
