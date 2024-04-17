<template>
  <div class="content-wrapper">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>订单筛选</span>
      </div>
      <el-row gutter="20">
        <el-col :span="8">
          <el-input v-model="searchText" placeholder="搜索订单名称..." @input="filterOrders"></el-input>
        </el-col>
        <el-col :span="8">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" @change="filterOrdersByDate"></el-date-picker>
        </el-col>
        <el-col :span="8">
          <el-select v-model="selectedStatus" placeholder="选择状态" @change="filterOrders">
            <el-option label="全部" value=""></el-option>
            <el-option label="待处理" value="待处理"></el-option>
            <el-option label="进行中" value="进行中"></el-option>
            <el-option label="已完成" value="已完成"></el-option>
          </el-select>
        </el-col>
      </el-row>
    </el-card>

    <el-row class="box-card-row">
      <el-col :span="24">
        <el-card class="box-card">
          <div slot="header" class="header">
            <span>订单列表</span>

            <div class="header-buttons">
              <el-button class="custom-butt" type="primary" @click="handleAdd">新增订单</el-button>
              <el-upload ref="upload" action="#" :auto-upload="false" :on-change="handleImport" :before-upload="beforeImport" accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
                <el-button class="custom-butt" type="warning">导入订单</el-button>
              </el-upload>
              <el-button class="custom-butt" type="success" @click="exportOrders">导出订单</el-button>
            </div>
          </div>
          <el-table :data="filteredOrders" style="width: 100%;">
            <el-table-column prop="date" label="日期" width="180"></el-table-column>
            <el-table-column prop="name" label="订单名称"></el-table-column>
            <el-table-column label="状态" width="120">
              <template v-slot="{ row }">
                {{ row.status }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template v-slot="{ row }">
                <el-button size="mini" @click="handleEdit(row)">编辑</el-button>
                <el-button size="mini" type="danger" @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible">
      <el-form :model="currentOrder" :rules="rules" ref="orderForm" label-width="120px">
        <el-form-item label="日期" prop="date">
          <el-date-picker v-model="currentOrder.date" type="date" placeholder="选择日期" style="width: 100%;"></el-date-picker>
        </el-form-item>
        <el-form-item label="订单名称" prop="name">
          <el-input v-model="currentOrder.name"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="currentOrder.status" placeholder="选择状态">
            <el-option label="待处理" value="待处理"></el-option>
            <el-option label="进行中" value="进行中"></el-option>
            <el-option label="已完成" value="已完成"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveOrder">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      searchText: '',
      selectedStatus: '',
      dateRange: [],
      orders: [
        { id: 1, date: '2024-01-01', name: '订单1', status: '待处理' },
        { id: 2, date: '2024-01-02', name: '订单2', status: '进行中' },
        // ... 其他订单数据
      ],
      filteredOrders: [],
      currentOrder: { date: '', name: '', status: '' },
      dialogVisible: false,
      dialogTitle: '',
      rules: {
        date: [{ required: true, message: '请选择日期', trigger: 'blur' }],
        name: [{ required: true, message: '请输入订单名称', trigger: 'blur' }],
        status: [{ required: true, message: '请选择状态', trigger: 'blur' }],
      },
    };
  },
  created() {
    this.filteredOrders = this.orders;
  },
  methods: {
    filterOrders() {
      this.filteredOrders = this.orders.filter((order) => {
        return (
          (this.searchText ? order.name.includes(this.searchText) : true) &&
          (this.selectedStatus ? order.status === this.selectedStatus : true)
        );
      });
    },
    filterOrdersByDate() {
      this.filteredOrders = this.orders.filter((order) => {
        const orderDate = new Date(order.date).getTime();
        const startDate = this.dateRange[0] && new Date(this.dateRange[0]).getTime();
        const endDate = this.dateRange[1] && new Date(this.dateRange[1]).getTime();
        return (!startDate || orderDate >= startDate) && (!endDate || orderDate <= endDate);
      });
    },
    handleAdd() {
      this.currentOrder = { date: '', name: '', status: '' };
      this.dialogTitle = '新增订单';
      this.dialogVisible = true;
    },
    handleEdit(order) {
      this.currentOrder = { ...order };
      this.dialogTitle = '编辑订单';
      this.dialogVisible = true;
    },
    handleDelete(order) {
      const index = this.orders.findIndex(o => o.id === order.id);
      if (index !== -1) {
        this.orders.splice(index, 1);
        this.filterOrders();
      }
    },
    saveOrder() {
      this.$refs.orderForm.validate((valid) => {
        if (valid) {
          const index = this.orders.findIndex(o => o.id === this.currentOrder.id);
          if (index !== -1) {
            this.orders.splice(index, 1, this.currentOrder);
          } else {
            this.currentOrder.id = Date.now(); // 简单的方法来生成一个唯一ID
            this.orders.push(this.currentOrder);
          }
          this.filterOrders();
          this.dialogVisible = false;
        }
      });
    },
    beforeImport(file) {
      // 文件校验逻辑（例如文件大小、格式等）
      return true; // 返回 false 则不上传
    },
    handleImport(file) {
      // 文件导入逻辑
      const reader = new FileReader();
      reader.onload = (e) => {
        // 解析 e.target.result 并更新订单列表
      };
      reader.readAsText(file.raw);
    },
    exportOrders() {
      // 导出订单逻辑
    }
  }
};
</script>

<style>
.container {
  padding: 20px; /* 页面主容器内边距 */
}

.box-card {
  margin-bottom: 20px; /* 卡片之间的外边距 */
  margin-left: 20px;
  margin-right: 20px;
  margin-top: 20px;

  box-shadow: 0 2px 4px rgba(0,0,0,0.12), 0 0 6px rgba(0,0,0,0.04);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-buttons {
  display: flex;
}

.custom-button {
  margin-left: 10px;
}
</style>
