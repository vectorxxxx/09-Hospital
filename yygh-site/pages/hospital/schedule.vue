<template>
  <!-- header -->
  <div class="nav-container page-component">
    <!--左侧导航 #start -->
    <div class="nav left-nav">
      <div class="nav-item selected">
        <span
          :onclick="'javascript:window.location=\'/hospital/'+hoscode+'\''"
          class="v-link selected dark">预约挂号 </span>
      </div>
      <div class="nav-item ">
        <span :onclick="'javascript:window.location=\'/hospital/detail/'+hoscode+'\''" class="v-link clickable dark"> 医院详情 </span>
      </div>
      <div class="nav-item">
        <span :onclick="'javascript:window.location=\'/hospital/notice/'+hoscode+'\''" class="v-link clickable dark"> 预约须知 </span>
      </div>
      <div class="nav-item "><span
        class="v-link clickable dark"> 停诊信息 </span>
      </div>
      <div class="nav-item "><span
        class="v-link clickable dark"> 查询/取消 </span>
      </div>
    </div>
    <!-- 左侧导航 #end -->

    <!-- 右侧内容 #start -->
    <div class="page-container">
      <div class="hospital-source-list">
        <div class="header-wrapper" style="justify-content:normal">
          <span class="v-link clickable" @click="show()">{{ baseMap.hosname }}</span>
          <div class="split"/>
          <div>{{ baseMap.bigname }}</div>
        </div>
        <div class="title mt20"> {{ baseMap.depname }}</div>
        <!-- 号源列表 #start -->
        <div class="mt60">
          <div class="title-wrapper">{{ baseMap.workDateString }}</div>
          <div class="calendar-list-wrapper">

            <!-- item.depNumber == -1 ? 'gray space' : item.depNumber == 0 ? 'gray' : 'small small-space'-->
            <!-- selected , index == activeIndex ? 'selected' : ''-->
            <div
              v-for="(item, index) in bookingScheduleList"
              :class="'calendar-item '+item.curClass"
              :key="item.id"
              style="width: 124px;"
              @click="selectDate(item, index)">
              <div class="date-wrapper"><span>{{ item.workDate }}</span><span class="week">{{ item.dayOfWeek }}</span>
              </div>
              <div v-if="item.status == 0" class="status-wrapper">
                {{ item.availableNumber === -1 ? '无号' : item.availableNumber === 0 ? '约满' : '有号' }}
              </div>
              <div v-if="item.status == 1" class="status-wrapper">即将放号</div>
              <div v-if="item.status == -1" class="status-wrapper">停止挂号</div>
            </div>
          </div>
          <!-- 分页 -->
          <el-pagination
            :current-page="page"
            :total="total"
            :page-size="limit"
            class="pagination"
            layout="prev, pager, next"
            @current-change="getPage"/>
        </div>

        <!-- 即将放号 #start-->
        <div v-if="!tabShow" class="countdown-wrapper mt60">
          <div class="countdonw-title"> {{ time }}<span class="v-link selected">{{ baseMap.releaseTime }} </span>放号
          </div>
          <div class="countdown-text"> 倒 计 时
            <div>
              <span class="number">{{ timeString }}</span>
            </div>
          </div>
        </div>
        <!-- 即将放号 #end-->

        <!-- 号源列表 #end -->
        <!-- 上午号源 #start -->
        <div v-if="tabShow" class="mt60">
          <div class="">
            <div class="list-title">
              <div class="block"/>
              上午号源
            </div>
            <div v-for="item in scheduleList" v-if="item.workTime === 0" :key="item.id">
              <div class="list-item">
                <div class="item-wrapper">
                  <div class="title-wrapper">
                    <div class="title">{{ item.title }}</div>
                    <div class="split"/>
                    <div class="name"> {{ item.docname }}</div>
                  </div>
                  <div class="special-wrapper">{{ item.skill }}</div>
                </div>
                <div class="right-wrapper">
                  <div class="fee"> ￥{{ item.amount }}
                  </div>
                  <div class="button-wrapper">
                    <div
                      :style="item.availableNumber === 0 || pageFirstStatus === -1 ? 'background-color: #7f828b;' : ''"
                      class="v-button"
                      @click="booking(item.id, item.availableNumber)">
                    <span>剩余<span class="number">{{ item.availableNumber }}</span></span></div>
                  </div>
                </div>
              </div>
            </div>

          </div>
        </div>
        <!-- 上午号源 #end -->
        <!-- 下午号源 #start -->
        <div v-if="tabShow" class="mt60">
          <div class="">
            <div class="list-title">
              <div class="block"/>
              下午号源
            </div>
            <div v-for="item in scheduleList" v-if="item.workTime === 1" :key="item.id">
              <div class="list-item">
                <div class="item-wrapper">
                  <div class="title-wrapper">
                    <div class="title">{{ item.title }}</div>
                    <div class="split"/>
                    <div class="name"> {{ item.docname }}</div>
                  </div>
                  <div class="special-wrapper">{{ item.skill }}</div>
                </div>
                <div class="right-wrapper">
                  <div class="fee"> ￥{{ item.amount }}
                  </div>
                  <div class="button-wrapper">
                    <div
                      :style="item.availableNumber === 0 || pageFirstStatus === -1 ? 'background-color: #7f828b;' : ''"
                      class="v-button"
                      @click="booking(item.id, item.availableNumber)">
                    <span>剩余<span class="number">{{ item.availableNumber }}</span></span></div>
                  </div>
                </div>
              </div>
            </div>

          </div>
        </div>
        <!-- 下午号源 #end -->
      </div>
    </div>
    <!-- 右侧内容 #end -->
  </div>
  <!-- footer -->
</template>

<script>
import '~/assets/css/hospital_personal.css'
import '~/assets/css/hospital.css'

import hospitalApi from '@/api/hosp/hospital'

export default {
  data() {
    return {
      hoscode: null,
      depcode: null,
      workDate: null,

      bookingScheduleList: [],
      scheduleList: [],
      baseMap: {},
      nextWorkDate: null, // 下一页第一个日期
      preWorkDate: null, // 上一页第一个日期

      tabShow: true, // 挂号列表与即将挂号切换
      activeIndex: 0,

      page: 1, // 当前页
      limit: 7, // 每页个数
      total: 1, // 总页码

      timeString: null,
      time: '今天',
      timer: null,

      pageFirstStatus: 0 // 第一页第一条数据状态
    }
  },

  created() {
    this.hoscode = this.$route.query.hoscode
    this.depcode = this.$route.query.depcode
    this.workDate = this.getCurDate()

    this.getBookingScheduleRule()
  },

  methods: {
    // 分页查询
    getPage(page = 1) {
      this.page = page
      this.workDate = null
      this.activeIndex = 0

      this.getBookingScheduleRule()
    },

    // 获取预约挂号数据
    getBookingScheduleRule() {
      hospitalApi.getBookingScheduleRule(this.page, this.limit, this.hoscode, this.depcode).then(response => {
        this.bookingScheduleList = response.data.bookingScheduleList
        this.total = response.data.total
        this.baseMap = response.data.baseMap

        // 处理样式
        this.dealClass()

        // 分页后workDate=null，默认选中第一个
        if (!this.workDate) {
          this.workDate = this.bookingScheduleList[0].workDate
        }

        // 判断当天是否停止预约 status == -1 停止预约
        if (this.workDate === this.getCurDate()) {
          this.pageFirstStatus = this.bookingScheduleList[0].status
        } else {
          this.pageFirstStatus = 0
        }
        this.findScheduleList()
      })
    },

    // 获取排班数据
    findScheduleList() {
      hospitalApi.findScheduleList(this.hoscode, this.depcode, this.workDate).then(response => {
        this.scheduleList = response.data
      })
    },

    // 选择日期
    selectDate(item, index) {
      this.workDate = item.workDate
      this.activeIndex = index

      // 清理定时
      if (this.timer) {
        clearInterval(this.timer)
      }

      // 是否即将放号
      if (item.status === 1) {
        this.tabShow = false
        // 放号时间
        const releaseTime = new Date(this.getCurDate() + ' ' + this.baseMap.releaseTime).getTime()
        const nowTime = new Date().getTime()
        this.countDown(releaseTime, nowTime)

        this.dealClass()
      } else {
        this.tabShow = true

        this.getBookingScheduleRule()
      }
    },

    // 处理样式
    dealClass() {
      for (let i = 0; i < this.bookingScheduleList.length; i++) {
        // depNumber -1:无号 0：约满 >0：有号
        let curClass = this.bookingScheduleList[i].availableNumber === -1 ? 'gray space' : this.bookingScheduleList[i].availableNumber === 0 ? 'gray' : 'small small-space'
        curClass += i === this.activeIndex ? ' selected' : ''
        this.bookingScheduleList[i].curClass = curClass
      }
    },

    // 获取当前时间
    getCurDate() {
      const date = new Date()
      const year = date.getFullYear()
      const month = date.getMonth() + 1
      const day = date.getDate()
      return year + '-' + (month < 10 ? '0' + month : month) + '-' + (day < 10 ? '0' + day : day)
    },

    // 倒计时
    countDown(releaseTime, nowTime) {
      if (releaseTime > nowTime) {
        this.time = '今天'
      } else {
        this.time = '明天'
        const releaseDate = new Date(releaseTime)
        releaseTime = new Date(releaseDate.setDate(releaseDate.getDate() + 1)).getTime()
      }
      let seconds = Math.floor((releaseTime - nowTime) / 1000)

      // 定时任务
      this.timer = setInterval(() => {
        seconds = seconds - 1
        if (seconds <= 0) {
          clearInterval(this.timer)
          this.init()
        }
        this.timeString = this.convertTimeString(seconds)
      }, 1000)

      // 通过$once来监听定时器，在beforeDestroy钩子可以被清除。
      //   这是一个 Vue.js 中的生命周期钩子函数 `beforeDestroy` 的钩子函数，它会在组件实例销毁之前执行。在这个例子中，我们使用 `$once` 方法来确保只执行一次。
      //   `this.$once('hook:beforeDestroy', ...)` 中的 `hook:beforeDestroy` 是一个事件名称，它会在组件实例销毁之前触发。当这个事件被触发时，括号中的函数会被执行。
      //   函数体内部执行了 `clearInterval(this.timer)` 这一行代码。`this.timer` 是一个定时器的 ID，它会在组件销毁之前一直执行，直到定时器被清除。这里我们使用 `clearInterval` 方法来清除定时器，从而避免内存泄漏。
      //   总之，这段代码的意思是在组件销毁之前执行一次 `clearInterval` 方法，清除定时器，避免内存泄漏。
      this.$once('hook:beforeDestroy', () => {
        clearInterval(this.timer)
      })
    },

    // 时间转换
    convertTimeString(allseconds) {
      if (allseconds <= 0) {
        return '00:00:00'
      }
      // 天数
      const days = Math.floor(allseconds / (60 * 60 * 24))
      // 小时
      const hour = Math.floor((allseconds - days * 60 * 60 * 24) / (60 * 60))
      // 分钟
      const minute = Math.floor((allseconds - days * 60 * 60 * 24 - hour * 60 * 60) / 60)
      // 秒
      const second = allseconds - days * 60 * 60 * 24 - hour * 60 * 60 - minute * 60

      let timeString = ''
      if (days > 0) {
        timeString = days + '天:'
      }

      return timeString + hour + '时' + minute + '分' + second + '秒'
    },

    // 显示详情
    show() {
      window.location.href = '/hospital/' + this.hoscode
    },

    // 预约
    booking(scheduleId, availableNumber) {
      if (availableNumber === 0 || availableNumber === -1) {
        this.$message.error('不能预约')
      } else {
        window.location.href = '/hospital/booking?scheduleId=' + scheduleId
      }
    }
  }
}
</script>

<style scoped>

</style>
