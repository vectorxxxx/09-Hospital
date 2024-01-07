## 1、前端中的面包屑是什么？

面包屑(Breadcrumb)是前端中一种导航设计模式，用于在网站或应用中创建导航路径，帮助用户了解他们在当前页面中的位置，以便更好地进行页面导航。面包屑通常包括一系列链接，这些链接可以带领用户返回到他们访问过的 earlier 页面。

在实际应用中，面包屑可以出现在以下几种位置：

1. 页面顶部：通常位于页面顶部，作为整个导航的起点。
2. 页面底部：通常位于页面底部，作为页面结束的标志。
3. 页面左侧：通常位于页面左侧，作为导航菜单。
4. 页面右侧：通常位于页面右侧，作为一些辅助信息，如操作提示、帮助信息等。

前端中的面包屑可以由HTML和CSS组成，也可以通过JavaScript进行交互操作。例如，可以使用JavaScript监听用户在页面上的操作，如滚动、点击等，动态更新面包屑导航路径。

下面是一个简单的HTML和CSS实现的前端面包屑示例：

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Breadcrumb Example</title>
    <style>
        .breadcrumb {
            list-style: none;
            margin: 0;
            padding: 0;
            overflow: hidden;
            background-color: #333;
        }
        .breadcrumb li {
            float: left;
        }
        .breadcrumb a {
            display: block;
            color: white;
            text-align: center;
            padding: 14px 16px;
            text-decoration: none;
        }
        .breadcrumb a:hover {
            background-color: #111;
        }
        .breadcrumb .active {
            background-color: #4CAF50;
            color: white;
        }
    </style>
</head>
<body>
    <ul class="breadcrumb">
        <li><a href="#">Home</a></li>
        <li><a href="#">About</a></li>
        <li><a href="#">Services</a></li>
        <li><a href="#">Contact</a></li>
        <li class="active"><a href="#">Current Page</a></li>
    </ul>
</body>
</html>
```

这个示例中，我们创建了一个简单的面包屑导航，包括Home、About、Services、Contact和当前页面。用户可以通过点击面包屑链接返回到相应的页面。



## 2、`<template slot-scope="scope">` 是什么？

`<template slot-scope="scope">` 是 Vue.js 中的一个语法，用于创建一个插槽（slot）。插槽允许你在组件模板中定义一个作用域，该作用域可以传递数据和事件到子组件。`slot-scope` 属性用于定义插槽的作用域的名称，`scope` 是该属性的值，它是一个对象，包含了插槽的数据和事件。



## 3、`<router-link>` 与 `this.$router.push()` 有何区别和相同点？

`<router-link>` 和 `this.$router.push()` 都是 Vue.js 中用于导航到路由的方法，但它们之间有一些关键区别：

 1. 功能不同：`<router-link>` 是一个 Vue.js 组件，主要用于生成导航链接，而 `this.$router.push()` 方法主要用于导航到路由。
 2. 用法不同：`<router-link>` 需要与 Vue.js 路由配置一起使用，它会自动将用户导航到指定的路由。而 `this.$router.push()` 方法需要手动调用，需要传入要导航到的路由对象。
 3. 传递参数不同：`<router-link>` 可以通过 `to` 属性传递参数，这些参数会被自动解析为路由的参数。而 `this.$router.push()` 方法需要手动传递参数，这些参数需要作为对象传递。
 4. 路由配置不同：`<router-link>` 需要与 Vue.js 路由配置一起使用，而 `this.$router.push()` 方法不需要。

相同点：`<router-link>` 和 `this.$router.push()` 都是用于导航到路由的方法，它们都可以将用户导航到指定的路由。

总的来说，`<router-link>` 和 `this.$router.push()` 都是用于导航到路由的方法，但它们的使用方式和功能不同。在实际开发中，可以根据需要选择使用 `<router-link>` 组件还是 `this.$router.push()` 方法。



## 4、yarn 是什么，跟 npm 有何区别？

yarn 和 npm 都是用于管理 Node.js 项目的包管理器。它们的目的是为了简化项目依赖管理，并提供一个统一的接口来处理依赖。

yarn 和 npm 之间有许多相似之处，例如它们都使用 lock 文件来存储项目依赖关系。但是，它们也有一些关键区别：

 1. 安装速度：yarn 通常比 npm 更快地安装项目依赖。这是因为 yarn 使用了并行下载和缓存来提高安装速度。
 2. 版本控制：yarn 支持 semantic versioning（语义版本），这意味着它可以自动处理版本冲突和依赖关系。而 npm 主要使用非语义版本，这意味着它可能需要手动解决版本冲突。
 3. 缓存：yarn 提供了缓存功能，可以避免重复下载相同的依赖。而 npm 没有这个功能。
 4. 发布包：yarn 支持发布包，可以创建、管理和分发自己的包。而 npm 主要关注的是公共包仓库。

总的来说，yarn 和 npm 都是优秀的包管理器，但它们在某些方面有所不同。选择哪个包管理器取决于你的具体需求和偏好。如果你需要更高的安装速度和版本控制，可以考虑使用 yarn。如果你需要发布包或管理自己的依赖，可以考虑使用 npm。
