<template>
  <div class="app-container">
    医院设置添加
    <hr>

    <el-form label-width="120px">
      <el-form-item label="医院名称">
        <el-input v-model="hospitalSet.hosname"/>
      </el-form-item>
      <el-form-item label="医院编号">
        <el-input v-model="hospitalSet.hoscode"/>
      </el-form-item>
      <el-form-item label="api基础路径">
        <el-input v-model="hospitalSet.apiUrl"/>
      </el-form-item>
      <el-form-item label="联系人姓名">
        <el-input v-model="hospitalSet.contactsName"/>
      </el-form-item>
      <el-form-item label="联系人手机">
        <el-input v-model="hospitalSet.contactsPhone"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="mini" @click="saveOrUpdate">保存</el-button>
        <el-button size="mini" @click="cancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import hospitalSetApi from '@/api/hospset'

export default {
  data() {
    return {
      hospitalSet: {
        hosname: '',
        hoscode: '',
        apiUrl: '',
        contactsName: '',
        contactsPhone: ''
      }
    }
  },

  created() {
    if (this.$route.params && this.$route.params.id) {
      this.getHospSet(this.$route.params.id)
    } else {
      this.hospitalSet = {}
    }
  },

  methods: {
    getHospSet(id) {
      hospitalSetApi.getHospSet(id).then(response => {
        this.hospitalSet = response.data
      })
    },
    saveOrUpdate() {
      if (this.hospitalSet.id) {
        this.update()
      } else {
        this.save()
      }
    },
    update() {
      hospitalSetApi.updateHospSet(this.hospitalSet).then(() => {
        this.$message({
          type: 'success',
          message: '修改成功!'
        })
        this.$router.push({ path: '/hospSet/list' })
      })
    },
    save() {
      hospitalSetApi.saveHospSet(this.hospitalSet).then(() => {
        this.$message({
          type: 'success',
          message: '添加成功!'
        })
        this.$router.push({ path: '/hospSet/list' })
      })
    },
    cancel() {
      this.$router.push({ path: '/hospSet/list' })
    }
  }
}
</script>

<style scoped>

</style>
