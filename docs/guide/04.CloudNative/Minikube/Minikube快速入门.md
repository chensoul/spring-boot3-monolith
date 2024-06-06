## 安装

使用 brew 安装

```bash
brew install minikube
```

## 启动集群

查看当前集群列表

```bash
$ minikube profile list
|----------|-----------|---------|--------------|------|---------|---------|-------|--------|
| Profile  | VM Driver | Runtime |      IP      | Port | Version | Status  | Nodes | Active |
|----------|-----------|---------|--------------|------|---------|---------|-------|--------|
| minikube | docker    | docker  | 192.168.49.2 | 8443 | v1.28.3 | Running |     1 | *      |
|----------|-----------|---------|--------------|------|---------|---------|-------|--------|
```

创建一个集群

```bash
# 启动一个名称为 minikube 集群，命名空间为 default
minikube start
```

>默认情况下，`minikube start `创建一个名为“minikube”的集群。如果您想创建不同的集群或更改其名称，可以使用`--profile`(或`-p`) 标志：
>
>```bash
>minikube start -p test
>```
>
>如果只想使用 docker 而不使用 k8s：
>
>```bash
>minikube start --container-runtime=docker --no-kubernetes
>```

k8s 集群创建成功之后，可以通过 kubectl 查看上下文：

```bash
$ kubectl config get-contexts
CURRENT   NAME       CLUSTER    AUTHINFO   NAMESPACE
*         minikube   minikube   minikube   default
```

> minikube 内置了 kubectl 工具，可以使用下面命令
>
> ```bash
> minikube kubectl --
> ```
>
> 也可以在 shell 中为上面命令设置一个别名：
> ```bash
> alias kubectl="minikube kubectl --"
> ```

查询 pod

```bash
kubectl get po -A
```

启动 dashboard

```bash
minikube dashboard
```

升级集群

```bash
minikube start --kubernetes-version=latest
```

查看集群状态

```bash
minikube status
```

停止本地集群：

```shell
minikube stop
```

暂停集群

```bash
minikube unpause
```

删除本地集群：

```shell
minikube delete
```

删除所有本地集群和配置文件

```shell
minikube delete --all
```

## 部署应用

### 部署一个服务

```bash
#创建一个服务
kubectl create deployment hello-minikube --image=kicbase/echo-server:1.0

#将服务公开为 NodePort
kubectl expose deployment hello-minikube --type=NodePort --port=8080
```

查询服务：

```bash
kubectl get services hello-minikube
```

使用 minikube 打开浏览器访问该服务

```bash
minikube service hello-minikube
```

设置端口转发

```bash
kubectl port-forward service/hello-minikube 7080:8080
```

接下来可以通过 http://localhost:7080/ 访问该服务

### 部署一个负载均衡

```bash
kubectl create deployment balanced --image=kicbase/echo-server:1.0
kubectl expose deployment balanced --type=LoadBalancer --port=8080
```

在另一个窗口中，启动隧道创建可路由 IP：

```bash
minikube tunnel
```

查询该服务

```shell
kubectl get services balanced
```

### 部署一个 ingress

查询插件：

```bash
minikube addons list
```

启用入口插件：

```shell
minikube addons enable ingress
```

如果要禁用插件：

```bash
minikube addons disable ingress
```

以下示例创建简单的 echo-server 服务和一个用于路由到这些服务的 Ingress 对象。

```shell
kind: Pod
apiVersion: v1
metadata:
  name: foo-app
  labels:
    app: foo
spec:
  containers:
    - name: foo-app
      image: 'kicbase/echo-server:1.0'
---
kind: Service
apiVersion: v1
metadata:
  name: foo-service
spec:
  selector:
    app: foo
  ports:
    - port: 8080
---
kind: Pod
apiVersion: v1
metadata:
  name: bar-app
  labels:
    app: bar
spec:
  containers:
    - name: bar-app
      image: 'kicbase/echo-server:1.0'
---
kind: Service
apiVersion: v1
metadata:
  name: bar-service
spec:
  selector:
    app: bar
  ports:
    - port: 8080
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: example-ingress
spec:
  rules:
    - http:
        paths:
          - pathType: Prefix
            path: /foo
            backend:
              service:
                name: foo-service
                port:
                  number: 8080
          - pathType: Prefix
            path: /bar
            backend:
              service:
                name: bar-service
                port:
                  number: 8080
---
```

应用内容

```shell
kubectl apply -f https://storage.googleapis.com/minikube-site-examples/ingress-example.yaml
```

等待入口地址

```shell
kubectl get ingress
NAME              CLASS   HOSTS   ADDRESS          PORTS   AGE
example-ingress   nginx   *       <your_ip_here>   80      5m45s
```

现在验证入口是否正常工作：

```shell
$ curl <ip_from_above>/foo
Request served by foo-app
...

$ curl <ip_from_above>/bar
Request served by bar-app
```

