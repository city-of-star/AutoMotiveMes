<!-- views/auth/Register.vue -->
<template>
  <auth-layout>
    <el-steps :active="step" finish-status="success">
      <el-step title="账户信息" />
      <el-step title="企业验证" />
      <el-step title="完成注册" />
    </el-steps>

    <transition name="el-fade-in">
      <div v-show="step === 1" class="step-container">
        <!-- 第一步：基础信息表单 -->
      </div>

      <div v-show="step === 2" class="step-container">
        <el-form>
          <el-form-item label="企业验证码">
            <el-input
                v-model="companyCode"
                placeholder="请输入企业授权码"
            />
            <div class="tips">请联系系统管理员获取企业授权码</div>
          </el-form-item>
        </el-form>
      </div>

      <div v-show="step === 3" class="success-container">
        <el-result
            icon="success"
            title="注册成功"
            sub-title="正在跳转到登录页面..."
        />
      </div>
    </transition>
  </auth-layout>
</template>