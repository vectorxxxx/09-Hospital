## 1、Logback和log4j的区别

Logback 和 log4j 都是日志记录框架，用于在 Java 应用程序中记录日志信息。它们的区别如下：

 1. 开发团队不同：Logback 是由 log4j 的创始人 Cekotulin 和 Apache 开发团队共同开发的，而 log4j 是由 Cekotulin 开发。
 2. 功能不同：Logback 提供了更多的功能，例如内联日志记录、异步日志记录、日志过滤器等。log4j 则专注于日志记录的基本功能。
 3. 配置方式不同：Logback 使用 XML 配置文件进行日志配置，而 log4j 使用 Java 代码进行配置。
 4. 性能不同：根据测试结果，Logback 在性能上比 log4j 更快。
 5. 依赖项不同：Logback 是一个独立的库，不需要依赖其他库。而 log4j 需要依赖 Apache Commons Logging 库。

总的来说，Logback 比 log4j 功能更强大，性能更好，推荐使用。但是，如果你已经在项目中使用了 log4j，那么可以考虑逐步迁移到 Logback，以获得更好的性能和功能。



## 2、CMN 是什么？

Cloud Managed Network，云网管



## 3、Docker 拉取 MongoDB

### 3.1、修改镜像源

```shell
vim /etc/docker/daemon.json
```

`/etc/docker/daemon.json`

```json
{
  "registry-mirrors": [
    "https://6kx4zyno.mirror.aliyuncs.com",
    "https://docker.mirrors.ustc.edu.cn/",
    "https://hub-mirror.c.163.com",
    "https://registry.docker-cn.com"
  ]
}
```

重启 Docker

```shell
# systemd 会自动重新加载配置文件，并将新的服务添加到 systemd 的服务管理列表中
systemctl daemon-reload

# 重启 Docker
systemctl restart docker
```

### 3.2、安装运行 MongoDB

```shell
# 拉取 MongoDB 镜像
docker pull mongo:latest

# 创建和启动容器
# `-d` 表示以分离模式运行容器
# `--restart=always` 表示当容器退出时自动重启
# `-p 27017:27017` 表示将容器的 27017 端口映射到宿主机的 27017 端口
# `--name mymongo` 表示为容器设置一个名称
# `-v /data/db:/data/db` 表示将宿主机的 /data/db 目录映射到容器的 /data/db 目录
# 启动一个名为 `mymongo` 的 MongoDB 容器，将容器的 27017 端口映射到宿主机的 27017 端口，将宿主机的 /data/db 目录映射到容器的 /data/db 目录，以分离模式运行容器，并在容器退出时自动重启。
docker run -d --restart=always -p 27017:27017 --name mymongo -v /data/db:/data/db mongo

# 查看容器运行状态
docker ps

# 进入容器
# `-it` 选项表示在执行命令时打开一个交互式终端
# `mymongo` 是容器名称
# `/bin/bash` 是将要执行的命令。
# 当执行 `docker exec -it mymongo /bin/bash` 命令时，它会进入容器中的 Bash  Shell，可以与容器内的系统进行交互。在容器内执行的命令都会在 Bash Shell 中执行，例如，可以输入 `ls` 命令来查看容器内的文件。
# 需要注意的是，在容器内执行命令时，可以使用 `/` 符号来访问宿主机的文件系统，例如，可以使用 `/data/db` 符号来访问宿主机的 /data/db 目录。
docker exec -it mymongo /bin/bash

# 使用MongoDB客户端进行操作 
mongo 

# 查询所有的数据库 
show dbs
```

### 3.3、虚拟机联通主机

```shell
# 修改入站规则
# `/sbin/iptables`：iptables 是一个 Linux 系统上的防火墙工具，用于控制进出系统的网络流量。
# `-I INPUT`：-I 选项表示在 INPUT 链表的末尾添加一条规则。
# `-p tcp`：-p 选项表示该规则仅适用于 TCP 协议。
# `--dport 27017`：--dport 选项表示该规则仅允许从指定端口进入的流量。在这个例子中，允许从端口 27017 进入的流量。
# `-j ACCEPT`：-j 选项表示当匹配到该规则时，将允许该流量进入系统。在这个例子中，允许从端口 27017 进入的流量。
# 该命令的作用是在 Linux 系统的防火墙中添加一条规则，允许从端口 27017 进入的 TCP 流量进入系统。
/sbin/iptables -I INPUT -p tcp --dport 27017 -j ACCEPT

# 保存并重启
service iptables save
service iptables restart

# 查看端口占用情况
yum install -y lsof
lsof -i:27017
# 或者
# -t (tcp) 仅显示tcp相关选项
# -u (udp)仅显示udp相关选项
# -n 拒绝显示别名，能显示数字的全部转化为数字
# -l 仅列出在Listen(监听)的服务状态
# -p 显示建立相关链接的程序名
netstat -tunpl | grep 27017
```



## 4、InetAddress 是什么？

`InetAddress` 是 Java 中的一个类，表示 IP 地址。它是 `java.net` 包下的一个类，用于处理网络通信中的 IP 地址。在 Java 中，IP 地址通常表示为四个十进制数，如 `192.168.1.1`。`InetAddress` 类提供了对 IP 地址的各种操作，如获取地址信息、解析地址等。

 `InetAddress` 类的主要构造方法如下：

 1. `InetAddress(String ipaddress)`：通过 IP 地址字符串构造 `InetAddress` 对象。
 2. `InetAddress(byte[] address)`：通过 IP 地址字节数组构造 `InetAddress` 对象。
 3. `InetAddress(InetAddress addr)`：通过另一个 `InetAddress` 对象构造 `InetAddress` 对象。

 `InetAddress` 类的主要方法如下：

 1. `getHostAddress()`：获取 IP 地址字符串。
 2. `getHostName()`：获取主机名。
 3. `getInetAddress()`：获取 IP 地址字节数组。
 4. `getHostAddress()`：获取 IP 地址字符串。
 5. `getHostName()`：获取主机名。
 6. `getInetAddress()`：获取 IP 地址字节数组。
 7. `getCanonicalHostName()`：获取规范的主机名。
 8. `getLocalHost()`：获取本地主机地址。
 9. `getAllByName(String host)`：根据主机名获取 `InetAddress` 对象列表。
 10. `getAllByNameInetAddress(InetAddress addr)`：根据 `InetAddress` 对象获取 `InetAddress` 对象列表。

 以下是一个简单的示例，展示了如何创建 `InetAddress` 对象并获取其信息：

 ```java
 import java.net.InetAddress;
 import java.net.UnknownHostException;
 
 public class InetAddressExample {
     public static void main(String[] args) {
         try {
             // 创建 InetAddress 对象
             InetAddress inetAddress = InetAddress.getByName("www.baidu.com");
 
             // 获取 IP 地址字符串
             String ipAddress = inetAddress.getHostAddress();
             System.out.println("IP Address: " + ipAddress);
 
             // 获取主机名
             String hostName = inetAddress.getHostName();
             System.out.println("Host Name: " + hostName);
 
             // 获取 IP 地址字节数组
             byte[] address = inetAddress.getInetAddress();
             System.out.println("IP Address Byte Array: " + address);
 
             // 获取规范的主机名
             String canonicalHostName = inetAddress.getCanonicalHostName();
             System.out.println("Canonical Host Name: " + canonicalHostName);
 
             // 获取本地主机地址
             InetAddress localHost = InetAddress.getLocalHost();
             System.out.println("Local Host Address: " + localHost.getHostAddress());
 
         } catch (UnknownHostException e) {
             e.printStackTrace();
         }
     }
 }
 ```

 这个示例将输出：

 ```
 IP Address: 114.114.114.114
 Host Name: www.baidu.com
 IP Address Byte Array: [114, 114, 114, 114]
 Canonical Host Name: www.baidu.com
 Local Host Address: 127.0.0.1
 ```