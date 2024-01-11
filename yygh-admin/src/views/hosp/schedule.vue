<template>
  <div class="app-container">
    <div style="margin-bottom: 10px;font-size: 18px;">
      选择：
      {{ baseMap.hosname }} / {{ depname }} / {{ workDate }}
    </div>

    <el-container style="height: 100%">
      <el-aside width="200px" style="border: 1px silver solid">
        <!-- 部门 -->
        <el-tree
          :data="data"
          :props="defaultProps"
          :default-expand-all="true"
          @node-click="handleNodeClick"/>
      </el-aside>

      <el-main style="padding: 0 0 0 20px;">
        <el-row style="width: 100%">
          <!-- 排班日期 -->
          <el-tag
            v-for="(item,index) in bookingScheduleList"
            :key="item.id"
            :type="index === activeIndex? '' : 'info'"
            style="height: 60px;margin-right: 5px;margin-right:15px;cursor:pointer;"
            @click="selectDate(item.workDate, index)">
            {{ item.workDate }} {{ item.dayOfWeek }}
            {{ item.availableNumber }} / {{ item.reservedNumber }}
          </el-tag>

          <!-- 分页 -->
          <el-pagination
            :current-page="page"
            :total="total"
            :page-size="limit"
            class="pagination"
            layout="prev, pager, next"
            @current-change="getPage"/>
        </el-row>

        <el-row style="margin-top: 20px;">
          <!-- 排班医生 -->
          <el-table
            v-loading="listLoading"
            :data="scheduleList"
            border
            fit
            highlight-current-row>
            <el-table-column
              label="序号"
              width="60"
              align="center">
              <template slot-scope="scope">
                {{ scope.$index + 1 }}
              </template>
            </el-table-column>
            <el-table-column label="职称" width="150">
              <template slot-scope="scope">
                {{ scope.row.title }} | {{ scope.row.docname }}
              </template>
            </el-table-column>
            <el-table-column label="号源时间" width="80">
              <template slot-scope="scope">
                {{ scope.row.workTime === 0 ? '上午' : '下午' }}
              </template>
            </el-table-column>
            <el-table-column prop="reservedNum" label="可预约数" width="80"/>
            <el-table-column prop="availableNumber" label="剩余预约数" width="100"/>
            <el-table-column prop="amount" label="挂号费（元）" width="90"/>
            <el-table-column prop="skill" label="擅长技能"/>
          </el-table>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import hosApi from '@/api/hosp'

export default {
  data() {
    return {
      data: [],
      defaultProps: {
        children: 'children',
        label: 'depname'
      },
      hoscode: null,

      activeIndex: 0,
      depcode: null,
      depname: null,
      workDate: null,

      bookingScheduleList: [],
      baseMap: {},

      // 分页
      page: 1,
      limit: 7,
      total: 0,

      // 排班详情
      listLoading: false,
      scheduleList: []
    }
  },

  created() {
    this.hoscode = this.$route.params.hoscode
    this.workDate = this.getCurDate()
    this.fetchData()
  },

  methods: {
    // 获取当前日期
    getCurDate() {
      const datetime = new Date()
      const year = datetime.getFullYear()
      const month = datetime.getMonth() + 1 < 10 ? '0' + (datetime.getMonth() + 1) : datetime.getMonth() + 1
      const date = datetime.getDate() < 10 ? '0' + datetime.getDate() : datetime.getDate()
      return year + '-' + month + '-' + date
    },
    // 获取数据
    fetchData() {
      hosApi.getDeptByHoscode(this.hoscode).then(response => {
        this.data = response.data
        // 默认选中第一个
        if (this.data.length > 0) {
          const child = this.data[0].children[0]
          this.depcode = child.depcode
          this.depname = child.depname
          this.getPage()
        }
      })
    },
    // 切换页码
    getPage(page = 1) {
      this.page = page
      this.workDate = null
      this.activeIndex = 0
      this.getScheduleRule()
    },
    // 获取排班规则
    getScheduleRule() {
      hosApi.getScheduleRule(this.page, this.limit, this.hoscode, this.depcode).then(response => {
        this.bookingScheduleList = response.data.bookingScheduleRuleVoList
        this.total = response.data.total
        this.baseMap = response.data.baseMap

        // 分页后workDate=null，默认选中第一个
        if (this.workDate == null && !!this.bookingScheduleList && this.bookingScheduleList.length > 0) {
          this.workDate = this.bookingScheduleList[0].workDate
        }

        // 查询排班详情
        this.getScheduleDetail()
      })
    },
    // 查询排班详情
    getScheduleDetail() {
      this.listLoading = true
      hosApi.getScheduleDetail(this.hoscode, this.depcode, this.workDate).then(response => {
        this.scheduleList = response.data
        this.listLoading = false
      })
    },
    // 科室点击事件
    handleNodeClick(data) {
      if (data.children != null) {
        return
      }
      this.depname = data.depname
      this.depcode = data.depcode

      this.getPage(1)
    },
    // 切换日期
    selectDate(workDate, index) {
      this.workDate = workDate
      this.activeIndex = index
      // 查询排班详情
      this.getScheduleDetail()
    }
  }
}
</script>

<style>
.el-tree-node.is-current > .el-tree-node__content {
  background-color: #409EFF !important;
  color: white;
}

.el-checkbox__input.is-checked + .el-checkbox__label {
  color: black;
}
</style>
