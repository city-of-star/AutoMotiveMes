<template>
  <div style="display: flex; gap: 20px;">

    <div>
      <el-input
          v-model="input4"
          placeholder="请输入部门名称"
          class="input-dept"
      >
        <template #prefix>
          <el-icon class="el-input__icon"><search /></el-icon>
        </template>
      </el-input>
      <el-tree
          :data="data"
          :props="defaultProps"
          @node-click="handleNodeClick"
          class="el-tree"
          clearable
      />
    </div>

    <div class="line"></div>

    <div>
      <div class="input-container">
        <div class="block">
          <span class="demonstration">用户名称</span>
          <el-input
              v-model="input4"
              placeholder="请输入用户名称"
              class="input"
              clearable
          >
            <template #prefix>
              <el-icon class="el-input__icon"><search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="block">
          <span class="demonstration">手机号码</span>
          <el-input
              v-model="input4"
              placeholder="请输入手机号码"
              class="input"
              clearable
          >
            <template #prefix>
              <el-icon class="el-input__icon"><search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="block">
          <span class="demonstration">用户状态</span>
          <el-input
              v-model="input4"
              placeholder="用户状态"
              class="input"
              clearable
          >
            <template #prefix>
              <el-icon class="el-input__icon"><search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="block">
          <span class="demonstration">创建时间</span>
          <el-date-picker
              v-model="value1"
              type="daterange"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              size="default"
              style="width: 220px;"
              clearable
          />
        </div>
        <el-button :icon="Search" type="primary">搜索</el-button>
        <el-button :icon="Refresh">重置</el-button>
      </div>

      <div class="btn-container">
        <el-button :icon="Plus" :color=theme_color plain>新增</el-button>
        <el-button :icon="Edit" :color=btn_update_color plain>修改</el-button>
        <el-button :icon="Delete" :color=btn_delete_color plain>删除</el-button>
        <el-button :icon="Download" :color=btn_import_color plain>导入</el-button>
        <el-button :icon="Upload" :color=btn_export_color plain>导出</el-button>
      </div>

      <el-table class="table-container" :data="tableData" style="width: 100%">
        <el-table-column prop="date" label="用户编号" />
        <el-table-column prop="name" label="用户名称" />
        <el-table-column prop="address" label="真实姓名" />
        <el-table-column prop="name" label="部门" />
        <el-table-column prop="name" label="手机号码" width="180" />
        <el-table-column prop="name" label="状态" />
        <el-table-column prop="name" label="创建时间" width="180" />
        <el-table-column prop="name" label="操作" width="180" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { Search, Refresh, Plus, Edit, Delete, Download, Upload  } from '@element-plus/icons-vue'
import { ref } from 'vue'
import { useStore } from 'vuex'

const store = useStore()

// 主题和按钮颜色
const theme_color = store.state.user.theme_color;
const btn_update_color = store.state.app.btn_update_color;
const btn_delete_color = store.state.app.btn_delete_color;
const btn_import_color = store.state.app.btn_import_color;
const btn_export_color = store.state.app.btn_export_color;

// 查询的条件值
const value1 = ref('')
const input4 = ref('')

const handleNodeClick = (data) => {
  console.log(data);
};

const data = [
  {
    label: '总部',
    children: [
      {
        label: '生产管理部门',
        children: [
          {
            label: '生产计划科',
          },
          {
            label: '车间执行组',
          },
        ],
      },
      {
        label: '质量管控中心',
        children: [
          {
            label: '质检科',
          },
        ],
      },
      {
        label: '设备运维部',
        children: [
          {
            label: '设备状态监控',
          },
        ],
      },
    ],
  },
];

const defaultProps = {
  children: 'children',
  label: 'label',
};

</script>

<style scoped>
.el-tree {
  width: 13vw;
  min-width: 170px;
  margin-top: 30px;
}

.input-dept {
  width: 13vw;
  min-width: 170px;
}

.input {
  width: 220px;
}

.line {
  background-color: #d7d7d7;
  width: 1px;
  height: 300px;
}

.input-container {
  display: flex;
  width: 67vw;
  gap: 16px; /* 统一控制元素间距 */
  flex-wrap: wrap; /* 允许换行 */
  align-items: center; /* 垂直居中 */
}

.block {
  display: flex;
  align-items: center;
  gap: 8px; /* 标签和输入框之间的间距 */
}

.demonstration {
  min-width: 70px;  /* 统一标签宽度 */
  text-align: right;  /* 标签右对齐 */
  color: #606266;  /* Element Plus 默认文字颜色 */
  font-size: 14px;
}

.btn-container {
  display: flex;
  margin-top: 20px;
}

.table-container {
  margin-top: 10px;
}

.table-container :deep() th {
  background-color: #e8ebec !important;
}
</style>