<template>
  <div class="header-container">
    <div class="wrapper">
      <!-- logo -->
      <div class="left-wrapper v-link selected">
        <img style="width: 50px" width="50" height="50" src="~assets/images/logo.png">
        <span class="text">尚医通 预约挂号统一平台</span>
      </div>
      <!-- 搜索框 -->
      <div class="search-wrapper">
        <div class="hospital-search animation-show">
          <div id="search" style="display: block;">
            <!-- 自动填充 -->
            <el-autocomplete
              v-model="hosname"
              :fetch-suggestions="querySearchAsync"
              :trigger-on-focus="false"
              class="search-input"
              prefix-icon="el-icon-search"
              placeholder="点击输入医院名称"
              @select="handleSelect"
            >
              <span slot="suffix" class="search-btn v-link highlight clickable selected">搜索 </span>
            </el-autocomplete>
          </div>
        </div>
      </div>
      <!-- 右侧 -->
      <div class="right-wrapper">
        <span class="v-link clickable">帮助中心</span>
      </div>
    </div>
  </div>
</template>
<script>
export default {}
</script>
