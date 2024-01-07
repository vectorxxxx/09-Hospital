<template>
  <div class="app-container">
    <!-- 导出按钮 -->
    <div class="el-toolbar">
      <div class="el-toolbar-body" style="justify-content: flex-start;">
        <a :href="baseURL + '/admin/cmn/dict/exportData'" target="_blank">
          <el-button type="text"><i class="fa fa-download">导出</i></el-button>
        </a>
        <el-button type="text" @click="importData()"><i class="fa fa-upload">导入</i></el-button>
      </div>
    </div>

    <!-- 树形表格 -->
    <el-table
      :data="list"
      :load="getChildren"
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
      style="width: 100%;"
      row-key="id"
      border
      lazy>
      <el-table-column label="名称" width="230" align="left">
        <template slot-scope="scope">
          <span> {{ scope.row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="编码" width="230">
        <template slot-scope="scope">
          <span> {{ scope.row.dictCode }}</span>
        </template>
      </el-table-column>
      <el-table-column label="值" width="230" align="left">
        <template slot-scope="scope">
          <span> {{ scope.row.value }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="left">
        <template slot-scope="scope">
          <span> {{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
    </el-table>

    <!-- 弹出层 -->
    <el-dialog
      :visible.sync="dialogImportVisible"
      title="导入"
      width="480px">
      <el-form
        label-position="right"
        label-width="170px">
        <el-form-item label="文件">
          <el-upload
            ref="upload"
            :on-success="onUploadSuccess"
            :action="baseURL +'/admin/cmn/dict/importData'"
            :multiple="false"
            class="upload-demo">
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">只能上传excel文件，且不超过500kb</div>
          </el-upload>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>
<script>
import dictApi from '@/api/dict'

export default {
  data() {
    return {
      id: 0,
      baseURL: process.env.BASE_API,
      list: [],
      dialogImportVisible: false
    }
  },

  created() {
    this.getDictList(this.id)
  },

  methods: {
    getDictList(id) {
      dictApi.dictList(id).then(response => {
        this.list = response.data
      })
    },
    getChildren(tree, treeNode, resolve) {
      dictApi.dictList(tree.id).then(response => {
        resolve(response.data)
      })
    },
    // 导入数据
    importData() {
      this.dialogImportVisible = true
    },
    // 上传成功
    onUploadSuccess() {
      this.dialogImportVisible = false
      this.$message.success('导入数据成功')
      this.getDictList(this.id)
      this.$refs.upload.clearFiles()
    }
  }
}
</script>

<style scoped>

</style>
