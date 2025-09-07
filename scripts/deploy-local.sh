#!/bin/bash

# 本地部署脚本 - 用于手动部署或测试
set -e

echo "开始本地构建和部署..."

# 设置变量
IMAGE_NAME="vps-server"
IMAGE_TAG="latest"
NAMESPACE="vps-server"

# 1. 构建Maven项目
echo "1. 构建Maven项目..."
cd vps-server
./mvnw clean package -DskipTests
cd ..

# 2. 构建Docker镜像
echo "2. 构建Docker镜像..."
docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ./vps-server

# 3. 检查PostgreSQL是否运行
echo "3. 检查PostgreSQL状态..."
if ! kubectl get pods -n postgresql | grep -q "Running"; then
    echo "警告: PostgreSQL可能未运行，请先部署PostgreSQL"
    exit 1
fi

# 4. 创建vps数据库（如果不存在）
echo "4. 创建数据库..."
kubectl run postgresql-client --rm --tty -i --restart='Never' \
  --namespace postgresql \
  --image docker.io/bitnami/postgresql:15 \
  --env="PGPASSWORD=mfZiV@!bmr&n" \
  --command -- psql --host postgresql --port 5432 -U root -d postgres -c "CREATE DATABASE vps;" 2>/dev/null || echo "数据库可能已存在"

# 5. 部署到K8s
echo "5. 部署到K8s..."
kubectl apply -f vps-server/k8s-deployment.yaml

# 6. 等待部署完成
echo "6. 等待部署完成..."
kubectl wait --for=condition=available --timeout=300s deployment/vps-server -n ${NAMESPACE}

# 7. 显示部署状态
echo "7. 部署状态："
kubectl get pods -n ${NAMESPACE}
kubectl get svc -n ${NAMESPACE}

# 8. 获取访问信息
echo ""
echo "部署完成！"
echo "===================="
NODE_IP=$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}')
echo "集群内访问: http://vps-server-service.vps-server.svc.cluster.local:8080"
echo "节点IP: ${NODE_IP}"
echo ""
echo "查看日志: kubectl logs -f deployment/vps-server -n ${NAMESPACE}"
echo "健康检查: kubectl exec -it deployment/vps-server -n ${NAMESPACE} -- curl localhost:8080/actuator/health"
