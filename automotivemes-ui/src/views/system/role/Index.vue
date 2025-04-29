<template>
  <div class="main-container">
    <div class="input-container">
      <div class="block">
        <span class="input-title">角色名称</span>
        <el-input v-model="searchData.username" placeholder="请输入角色名称" class="input" @clear="search" clearable>
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      <div class="block">
        <span class="input-title">权限字符</span>
        <el-input v-model="searchData.phone" placeholder="请输入权限字符" class="input" @clear="search" clearable>
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      <div class="block">
        <span class="input-title">角色状态</span>
        <el-select v-model="searchData.status" placeholder="角色状态" class="input" @clear="search" clearable>
          <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </div>
      <div class="block">
        <span class="input-title">创建时间</span>
        <el-date-picker
            v-model="searchData.time"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            size="default"
            style="width: 220px;"
            @clear="search"
            clearable
        />
      </div>
      <el-button @click="search()" :icon="Search" type="primary">搜索</el-button>
      <el-button @click="refresh()" :icon="Refresh">重置</el-button>
    </div>
  </div>
</template>

<script setup>
import { Refresh, Search } from "@element-plus/icons-vue";
import { ref } from "vue";

// 搜索条件
const searchData = ref({
  deptName: '',
  username: '',
  phone: '',
  status: null,
  time: [],
})
</script>

<style scoped>
.main-container {

}

.input {
  width: 220px;
}

.input-container {
  display: flex;
  gap: 16px; /* 统一控制元素间距 */
  flex-wrap: wrap; /* 允许换行 */
  align-items: center; /* 垂直居中 */
}

.block {
  display: flex;
  align-items: center;
  gap: 8px; /* 标签和输入框之间的间距 */
}

.input-title {
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
</style>