## 1、问题参考腾讯文档

[尚医通项目疑难问题解决方法 (qq.com)](https://docs.qq.com/doc/DT2JPQUVvb015RHVB)



## 2、scrollIntoView() 是什么？

```javascript
document.getElementById(depcode).scrollIntoView();
```

`scrollIntoView()`：这个方法用于将元素滚动到视口可见区域。它接受一个可选的参数`alignToTop`，表示元素是否应该与视口顶部对齐。如果该参数设置为`true`，则元素将从顶部开始滚动；如果设置为`false`（默认值），则元素将从底部开始滚动。



## 3、$emit 有啥用？

`$emit `是 Vue.js 中的一个实例方法，用于触发组件的事件。它接受两个参数：第一个参数是事件名称，第二个参数是事件对象。当组件调用 `$emit` 方法时，它会将事件名称和事件对象传递给父组件，从而触发父组件中的事件处理函数。

在Vue组件中，我们可以通过 `$emit` 方法来触发自定义事件，从而实现父子组件之间的通信。例如，子组件可以通过 `$emit` 方法向父组件发送数据，或者在子组件中触发一个事件，从而在父组件中执行某些操作。

例如，假设我们有一个子组件，它有一个名为 `update-data` 的自定义事件，当用户点击按钮时，子组件会触发这个事件，并传递一个数据对象给父组件。父组件可以通过监听这个事件，并在事件处理函数中处理接收到的数据。

在子组件中，我们可以这样触发事件：
 ```javascript
 this.$emit('update-data', {
   name: 'John',
   age: 30
 });
 ```
在父组件中，我们可以这样监听事件：
 ```javascript
 <template>
   <div>
     <child-component @update-data="handleUpdateData"></child-component>
   </div>
 </template>
 
 <script>
 import ChildComponent from './ChildComponent.vue';
 
 export default {
   components: {
     ChildComponent
   },
   methods: {
     handleUpdateData(data) {
       console.log('Received data:', data);
       // 在这里处理接收到的数据
     }
   }
 };
 </script>
 ```
总之，`$emit `是 Vue.js 中实现父子组件之间通信的一个关键方法，通过它可以触发自定义事件，并将事件对象传递给父组件。
