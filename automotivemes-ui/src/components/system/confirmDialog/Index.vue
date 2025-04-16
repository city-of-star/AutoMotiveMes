<template>
  <el-dialog
      v-model="dialogVisible"
      :title="title"
      :width="width"
      align-center
  >
    <span>{{ message }}</span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">{{ cancelText }}</el-button>
        <el-button type="primary" @click="handleConfirm">{{ confirmText }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
export default {
  name: 'ConfirmDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '系统提示'
    },
    message: {
      type: String,
      required: true
    },
    width: {
      type: [String, Number],
      default: 500
    },
    confirmText: {
      type: String,
      default: '确定'
    },
    cancelText: {
      type: String,
      default: '取消'
    }
  },
  emits: ['confirm', 'cancel', 'update:visible'],
  computed: {
    dialogVisible: {
      get() {
        return this.visible
      },
      set(value) {
        this.$emit('update:visible', value)
      }
    }
  },
  methods: {
    handleConfirm() {
      this.$emit('confirm')
      this.dialogVisible = false
    },
    handleCancel() {
      this.$emit('cancel')
      this.dialogVisible = false
    }
  }
}
</script>