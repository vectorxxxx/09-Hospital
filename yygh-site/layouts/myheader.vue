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

        <span
          v-if="name == ''"
          id="loginDialog"
          class="v-link clickable"
          @click="showLogin()">登录/注册</span>

        <!-- 下拉选项 -->
        <el-dropdown v-if="name != ''" @command="loginMenu">
          <span class="el-dropdown-link">
            {{ name }}<i class="el-icon-arrow-down el-icon--right"/>
          </span>
          <el-dropdown-menu slot="dropdown" class="user-name-wrapper">
            <el-dropdown-item command="/user">实名认证</el-dropdown-item>
            <el-dropdown-item command="/order">挂号订单</el-dropdown-item>
            <el-dropdown-item command="/patient">就诊人管理</el-dropdown-item>
            <el-dropdown-item command="/logout" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>

    <!-- 登录弹出层 -->
    <el-dialog
      :visible.sync="dialogUserFormVisible"
      :append-to-body="true"
      style="text-align: left;"
      top="50px"
      width="960px"
      @close="closeDialog()">
      <div class="container">

        <!-- 手机登录 #start -->
        <div v-if="dialogAttr.showLoginType === 'phone'" class="operate-view">
          <div class="wrapper" style="width: 100%">
            <div class="mobile-wrapper" style="position: static;width: 70%">
              <span class="title">{{ dialogAttr.labelTips }}</span>
              <el-form>
                <el-form-item>
                  <el-input
                    v-model="dialogAttr.inputValue"
                    :placeholder="dialogAttr.placeholder"
                    :maxlength="dialogAttr.maxlength"
                    class="input v-input">
                    <span v-if="dialogAttr.second > 0" slot="suffix" class="sendText v-link">
                      {{ dialogAttr.second }}s </span>
                    <span
                      v-if="dialogAttr.second === 0"
                      slot="suffix"
                      class="sendText v-link highlight clickable selected"
                      @click="getCodeFun()">重新发送 </span>
                  </el-input>
                </el-form-item>
              </el-form>
              <div class="send-button v-button" @click="btnClick()"> {{ dialogAttr.loginBtn }}</div>
            </div>
            <div class="bottom">
              <div class="wechat-wrapper" @click="weixinLogin()"><span
                class="iconfont icon"></span></div>
            <span class="third-text"> 第三方账号登录 </span></div>
          </div>
        </div>
        <!-- 手机登录 #end -->

        <!-- 微信登录 #start -->
        <div v-if="dialogAttr.showLoginType === 'weixin'" class="operate-view">
          <div class="wrapper wechat" style="height: 400px">
            <div>
              <div id="weixinLogin"/>
            </div>
            <div class="bottom wechat" style="margin-top: -80px;">
              <div class="phone-container">
                <div class="phone-wrapper" @click="phoneLogin()"><span
                  class="iconfont icon"></span></div>
              <span class="third-text"> 手机短信验证码登录 </span></div>
            </div>
          </div>
        </div>
        <!-- 微信登录 #end -->

        <div class="info-wrapper">
          <div class="code-wrapper">
            <div><img src="//img.114yygh.com/static/web/code_login_wechat.png" class="code-img">
              <div class="code-text"><span class="iconfont icon"></span>微信扫一扫关注
              </div>
              <div class="code-text"> “快速预约挂号”</div>
            </div>
            <div class="wechat-code-wrapper"><img
              src="//img.114yygh.com/static/web/code_app.png"
              class="code-img">
              <div class="code-text"> 扫一扫下载</div>
              <div class="code-text"> “预约挂号”APP</div>
            </div>
          </div>
          <div class="slogan">
            <div>xxxxxx官方指定平台</div>
            <div>快速挂号 安全放心</div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import cookie from 'js-cookie'
import Vue from 'vue'

import userInfoApi from '@/api/user/userInfo'
import smsApi from '@/api/sms/sms'
import hospitalApi from '@/api/hosp/hospital'
import weixinApi from '@/api/user/weixin'

const defaultDialogAtrr = {
  showLoginType: 'phone', // 控制手机登录与微信登录切换

  labelTips: '手机号码', // 输入框提示

  inputValue: '', // 输入框绑定对象
  placeholder: '请输入您的手机号', // 输入框placeholder
  maxlength: 11, // 输入框长度控制

  loginBtn: '获取验证码', // 登录按钮或获取验证码按钮文本

  sending: true, // 是否可以发送验证码
  second: -1, // 倒计时间  second>0 : 显示倒计时 second=0 ：重新发送 second=-1 ：什么都不显示
  clearSmsTime: null // 倒计时定时任务引用 关闭登录层清除定时任务
}

export default {
  data() {
    return {
      userInfo: {
        phone: '',
        code: '',
        openid: ''
      },

      dialogUserFormVisible: false,
      // 弹出层相关属性
      dialogAttr: defaultDialogAtrr,

      name: '' // 用户登录显示的名称
    }
  },

  created() {
    this.showInfo()
  },

  // 注册与监听事件
  mounted() {
    // 注册全局登录事件对象
    window.loginEvent = new Vue()
    // 监听登录事件
    // eslint-disable-next-line no-undef
    loginEvent.$on('loginDialogEvent', function() {
      document.getElementById('loginDialog').click()
    })
    // 触发事件，显示登录层：loginEvent.$emit('loginDialogEvent')

    // 初始化微信js
    const script = document.createElement('script')
    script.type = 'text/javascript'
    script.src = 'https://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js'
    document.body.appendChild(script)

    // 微信登录回调粗粒
    const self = this
    window['loginCallback'] = (name, token, openid) => {
      debugger
      self.loginCallback(name, token, openid)
    }
  },

  methods: {
    loginCallback(name, token, openid) {
      // 打开手机登录层，绑定手机号，改逻辑与手机登录一致
      if (openid !== '' && openid !== null) {
        this.userInfo.openid = openid
        this.dialogAttr.labelTips = '绑定手机号'
        this.showLogin()
      } else {
        this.setCookies(name, token)
      }
    },

    // 获取验证码 / 登录
    btnClick() {
      // 判断是获取验证码还是登录
      if (this.dialogAttr.loginBtn === '获取验证码') {
        this.userInfo.phone = this.dialogAttr.inputValue

        // 获取验证码
        this.getCodeFun()
      } else {
        // 登录
        this.login()
      }
    },

    // 绑定登录，点击显示登录层
    showLogin() {
      this.dialogUserFormVisible = true

      // 初始化登录层相关参数
      this.dialogAttr = { ...defaultDialogAtrr }
    },

    // 登录
    login() {
      this.userInfo.code = this.dialogAttr.inputValue

      if (this.dialogAttr.loginBtn === '正在提交...') {
        this.$message.error('请勿重复提交')
        return
      }

      if (this.userInfo.code === '') {
        this.$message.error('验证码不能为空')
        return
      }

      if (this.userInfo.code.length !== 6) {
        this.$message.error('验证码长度为6位数')
        return
      }

      this.dialogAttr.loginBtn = '正在提交...'
      userInfoApi.login(this.userInfo).then(response => {
        const data = response.data
        this.setCookies(data.name, data.token)
      }).catch(() => {
        this.dialogAttr.loginBtn = '马上登录'
      })
    },

    setCookies(name, token) {
      cookie.set('name', name, { domain: 'localhost' })
      cookie.set('token', token, { domain: 'localhost' })
      window.location.reload()
    },

    // 获取验证码
    getCodeFun() {
      if (!(/^1[345789]\d{9}$/.test(this.userInfo.phone))) {
        this.$message.error('手机号码不正确')
        return
      }

      // 初始化验证码相关属性
      this.dialogAttr.inputValue = ''
      this.dialogAttr.placeholder = '请输入验证码'
      this.dialogAttr.maxlength = 6
      this.dialogAttr.loginBtn = '马上登录'

      // 控制重复发送
      if (!this.dialogAttr.sending) {
        return
      }

      // 发送验证码
      this.timeDown()
      this.dialogAttr.sending = false
      smsApi.sendCode(this.userInfo.phone).then(response => {
        this.timeDown()
      }).catch(() => {
        // this.$message.error('验证码发送失败，请稍后再试')
        this.showLogin()
      })
    },

    // 倒计时
    timeDown() {
      if (this.clearSmsTime) {
        clearInterval(this.clearSmsTime)
      }

      this.dialogAttr.second = 60
      this.dialogAttr.labelTips = '验证码已发送至' + this.userInfo.phone

      this.clearSmsTime = setInterval(() => {
        --this.dialogAttr.second
        if (this.dialogAttr.second < 1) {
          clearInterval(this.clearSmsTime)
          this.dialogAttr.sending = true
          this.dialogAttr.second = 0
        }
      }, 1000)
    },

    // 关闭登录层
    closeDialog() {
      if (this.clearSmsTime) {
        clearInterval(this.clearSmsTime)
      }
    },

    // 展示用户信息
    showInfo() {
      const token = cookie.get('token')
      if (token) {
        this.name = cookie.get('name')
        console.log(this.name)
      }
    },

    // 登录菜单
    loginMenu(command) {
      if (command === '/logout') {
        cookie.set('token', '', { domain: 'localhost' })
        cookie.set('name', '', { domain: 'localhost' })

        window.location.href = '/'
      } else {
        window.location.href = command
      }
    },

    // 搜索
    querySearchAsync(queryString, cb) {
      if (queryString.trim() === '') {
        return
      }
      hospitalApi.getByHosname(queryString).then(response => {
        const hospital = response.data
        for (let i = 0; i < hospital.length; i++) {
          hospital.value = hospital.hosname
        }
        cb(hospital)
      })
    },

    handleSelect(item) {
      window.location.href = '/hospital/' + item.hoscode
    },

    // 微信登录
    weixinLogin() {
      this.dialogAttr.showLoginType = 'weixin'

      weixinApi.getLoginParam().then(response => {
        // eslint-disable-next-line no-undef
        new WxLogin({
          self_redirect: true,
          id: 'weixinLogin', // 需要显示的容器id
          appid: response.data.appid, // 公众号appid wx*******
          scope: response.data.scope, // 网页默认即可
          redirect_uri: response.data.redirect_uri, // 授权成功后回调的url
          state: response.data.state, // 可设置为简单的随机数加session用来校验
          style: 'black', // 提供"black"、"white"可选。二维码的样式
          href: '' // 外部css文件url，需要https
        })
      })
    },

    // 手机号登录
    phoneLogin() {
      this.dialogAttr.showLoginType = 'phone'
      this.showLogin()
    }
  }
}
</script>
