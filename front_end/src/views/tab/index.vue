<template>
  <el-container>

    <el-main>
      <el-card class="box-card" style="margin: 20px;">

        <div class="table-operations">
          <el-button @click="showAddEmployeeDialog">添加员工</el-button>
          <el-button @click="batchDelete">批量删除</el-button>
          <el-button @click="showFileUploadDialog">批量上传</el-button>
            <el-input v-model="searchQuery" placeholder="搜索员工..." style="width: 200px; margin-right: 10px; margin-left: 700px;"></el-input>
            <el-button @click="filterEmployees">搜索</el-button>
            <!-- 其他按钮 -->

        </div>

        <el-table :data="employeeData" style="width: 100%" ref="multipleTable" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="name" label="姓名" sortable></el-table-column>
          <el-table-column prop="position" label="职务" sortable></el-table-column>
          <el-table-column prop="joinMonth" label="入职月份" sortable></el-table-column>
          <el-table-column prop="leaveMonth" label="离职月份" sortable></el-table-column>
          <el-table-column label="操作">
            <template v-slot:default="scope">
              <el-button @click="editEmployee(scope.row)">编辑</el-button>
              <el-button @click="deleteEmployee(scope.$index, scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-dialog :visible.sync="isDialogVisible" title="员工信息">
        <!-- 对话框内容 -->
      </el-dialog>

      <el-dialog :visible.sync="isUploadDialogVisible" title="批量上传员工数据">
        <!-- 上传对话框内容 -->
      </el-dialog>
    </el-main>
  </el-container>
</template>


<script>
export default {
  data() {
    return {
      employeeData: [
        { id: 1, name: '张三', position: '软件工程师', joinMonth: '2023-01', leaveMonth: '2024-01' },
        { id: 2, name: '李四', position: '产品经理', joinMonth: '2022-05', leaveMonth: '' },
        { id: 3, name: '王五', position: 'UI设计师', joinMonth: '2021-11', leaveMonth: '2023-03' },
        { id: 4, name: '赵六', position: '前端开发', joinMonth: '2023-02', leaveMonth: '' },
        { id: 5, name: '孙七', position: '数据分析师', joinMonth: '2022-08', leaveMonth: '' },
        { id: 6, name: '周八', position: '测试工程师', joinMonth: '2021-07', leaveMonth: '2023-12' },
        { id: 7, name: '吴九', position: '运维工程师', joinMonth: '2020-04', leaveMonth: '2022-11' },
        { id: 8, name: '郑十', position: '人力资源', joinMonth: '2019-09', leaveMonth: '' },
        // 您可以继续添加更多数据
      ],
      currentEmployee: { id: null, name: '', position: '', joinMonth: '', leaveMonth: '' },
      selectedRows: [],
      isDialogVisible: false,
      isUploadDialogVisible: false,
    };
  },
  methods: {
    showAddEmployeeDialog() {
      this.currentEmployee = { id: null, name: '', position: '', joinMonth: '', leaveMonth: '' };
      this.isDialogVisible = true;
    },
    editEmployee(employee) {
      this.currentEmployee = Object.assign({}, employee);
      this.isDialogVisible = true;
    },
    deleteEmployee(index, row) {
      this.employeeData.splice(index, 1);
    },
    batchDelete() {
      this.selectedRows.forEach(row => {
        const index = this.employeeData.findIndex(e => e.id === row.id);
        if (index !== -1) {
          this.employeeData.splice(index, 1);
        }
      });
    },
    saveEmployee() {
      if (this.currentEmployee.id) {
        const index = this.employeeData.findIndex(e => e.id === this.currentEmployee.id);
        if (index !== -1) {
          this.employeeData.splice(index, 1, this.currentEmployee);
        }
      } else {
        const newId = this.employeeData.length > 0 ? Math.max(...this.employeeData.map(e => e.id)) + 1 : 1;
        this.currentEmployee.id = newId;
        this.employeeData.push(this.currentEmployee);
      }
      this.isDialogVisible = false;
    },
    handleSelectionChange(val) {
      this.selectedRows = val;
    }
  }
};
</script>

<style scoped>
.box-card {
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  border-radius: 4px;
}

.el-table .cell {
  padding: 10px 0;
}
</style>
