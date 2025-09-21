#!/bin/bash

# 前端项目构建和部署脚本

set -e

echo "🚀 开始构建和部署前端项目..."

# 构建 Docker 镜像
echo "📦 构建 Docker 镜像..."
docker build -t ghcr.io/administrator/vps-web:latest .

# 推送镜像到仓库
echo "📤 推送镜像到仓库..."
docker push ghcr.io/administrator/vps-web:latest

# 应用 Kubernetes 配置
echo "🎯 部署到 Kubernetes..."
kubectl apply -f k8s-deployment.yaml

# 等待部署完成
echo "⏳ 等待部署完成..."
kubectl rollout status deployment/vps-web -n vps-web --timeout=300s

# 显示部署状态
echo "✅ 部署完成！"
echo ""
echo "📊 部署状态："
kubectl get pods -n vps-web
echo ""
echo "🌐 服务信息："
kubectl get svc -n vps-web
echo ""
echo "🔗 Ingress 信息："
kubectl get ingress -n vps-web

echo ""
echo "🎉 前端应用已成功部署！"
echo "访问地址: http://vps.local (请确保在 hosts 文件中配置域名解析)"