<template>
  <div class="app-container">
    <!--  表单查询  -->
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item>
        <el-input v-model="searchObj.hosname" placeholder="医院名称"/>
      </el-form-item>
      <el-form-item>
        <el-input v-model="searchObj.hoscode" placeholder="医院编号"/>
      </el-form-item>
      <el-button type="primary" icon="el-icon-search" @click="getList()">查询</el-button>
    </el-form>

    <!--  工具条  -->
    <div>
      <el-button type="primary" size="mini" @click="addRow()">添加</el-button>
      <el-button type="danger" size="mini" @click="removeRows()">批量删除</el-button>
    </div>

    <!--  表格  -->
    <el-table
      :data="list"
      style="width: 100%"
      @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"/>
      <el-table-column type="index" width="50"/>
      <el-table-column prop="hosname" label="医院名称"/>
      <el-table-column prop="hoscode" label="医院编号"/>
      <el-table-column prop="apiUrl" label="api基础路径" width="200/"/>
      <el-table-column prop="contactsName" label="联系人姓名"/>
      <el-table-column prop="contactsPhone" label="联系人手机"/>
      <el-table-column label="状态" width="80">
        <template slot-scope="scope">
          {{ scope.row.status === 1 ? '可用' : '不可用' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center">
        <template slot-scope="scope">
          <router-link :to="'/hospSet/edit/' + scope.row.id">
            <el-tooltip class="item" effect="dark" content="修改" placement="top-start">
              <el-button type="primary" size="mini" icon="el-icon-edit"/>
            </el-tooltip>
          </router-link>

          <el-tooltip class="item" effect="dark" content="删除" placement="top-start">
            <el-button type="danger" size="mini" icon="el-icon-delete" @click="removeDataById(scope.row.id)"/>
          </el-tooltip>

          <el-tooltip :content="scope.row.status === 1 ? '锁定' : '取消锁定'" class="item" effect="dark" placement="top-start">
            <el-button v-if="scope.row.status === 1" type="warning" size="mini" icon="el-icon-lock" @click="lockHospSet(scope.row.id, 0)"/>
            <el-button v-if="scope.row.status === 0" type="warning" size="mini" icon="el-icon-unlock" @click="lockHospSet(scope.row.id, 1)"/>
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>

    <!--分页-->
    <el-pagination
      :current-page="current"
      :page-size="limit"
      :total="total"
      style="padding: 30px 0; text-align: center;"
      layout="total, prev, pager, next, jumper"
      @current-change="fetchData"
    />
  </div>
</template>
<script>
import hospitalSetApi from '@/api/hospset'

export default {
  data() {
    return {
      current: 1,
      limit: 10,
      total: 0,
      searchObj: {},
      list: [],
      multipleSelection: []
    }
  },

  created() {
    this.getList()
  },

  methods: {
    // 分页查询
    getList(page = 1) {
      this.current = page
      hospitalSetApi.getHospSetList(this.current, this.limit, this.searchObj).then(response => {
        this.list = response.data.records
        this.total = response.data.total
      }).catch(err => {
        console.log(err)
      })
    },
    // 添加
    addRow() {
      this.$router.push({ path: '/hospSet/add' })
    },
    // 删除
    removeDataById(id) {
      this.$confirm('此操作将永久删除医院是设置信息, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        hospitalSetApi.deleteHospSet(id).then(() => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          })
          this.getList()
        })
      })
    },
    // 批量删除
    handleSelectionChange(selection) {
      this.multipleSelection = selection
    },
    removeRows() {
      this.$confirm('此操作将永久删除医院是设置信息, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const idList = []
        for (let i = 0; i < this.multipleSelection.length; i++) {
          const id = this.multipleSelection[i].id
          id && idList.push(id)
        }
        hospitalSetApi.batchRemoveHospSet(idList).then(() => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          })
          this.getList()
        })
      })
    },
    // 锁定/取消锁定
    lockHospSet(id, status) {
      hospitalSetApi.lockHospSet(id, status).then(() => {
        if (status === 0) {
          this.$message({
            type: 'success',
            message: '锁定成功!'
          })
        } else if (status === 1) {
          this.$message({
            type: 'success',
            message: '取消锁定成功!'
          })
        }
        this.getList()
      })
    }
  }
}
</script>
