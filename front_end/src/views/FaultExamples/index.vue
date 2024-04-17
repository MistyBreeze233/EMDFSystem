<template>
  <div>
    <el-button type="primary" @click="showEditDialog = true">新增订单</el-button>
    <el-table :data="orders" style="width: 100%">
      <el-table-column prop="date" label="日期" width="180"></el-table-column>
      <el-table-column prop="name" label="订单名称"></el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button size="mini" @click="editOrder(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="订单编辑" :visible.sync="showEditDialog">
      <el-form :model="editForm">
        <el-form-item label="日期">
          <el-date-picker v-model="editForm.date" type="date" placeholder="选择日期"></el-date-picker>
        </el-form-item>
        <el-form-item label="订单名称">
          <el-input v-model="editForm.name"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showEditDialog = false">取 消</el-button>
        <el-button type="primary" @click="saveOrder">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      orders: [
        { date: '2024-01-01', name: '2024-01-01-订单1' },
        { date: '2024-01-02', name: '2024-01-02-订单2' }
        // 更多订单数据...
      ],
      showEditDialog: false,
      editForm: {
        date: '',
        name: ''
      }
    };
  },
  methods: {
    editOrder(order) {
      this.editForm = { ...order };
      this.showEditDialog = true;
    },
    saveOrder() {
      // 保存订单逻辑
      this.showEditDialog = false;
    }
  }
};
</script>

<style>
/* 可以在这里添加一些样式 */
</style>
