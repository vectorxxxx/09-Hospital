<template>
  <!-- header -->
  <div/>
  <!-- footer -->
</template>

<script>
export default {
  layout: 'empty',

  data() {
    return {}
  },

  mounted() {
    const token = this.$route.query.token
    const name = this.$route.query.name
    const openid = this.$route.query.openid
    console.log(window.parent['loginCallback'])
    // 调用父 vue 方法
    window.parent['loginCallback'](name, token, openid)
  }
}
</script>
