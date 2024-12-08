<template>
  <el-page-header :icon="null" @back="onBack">
    <template #content>
      <div class="flex items-center">
        <el-avatar
          :size="32"
          class="mr-3"
          src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
        />
        <span class="text-large font-600 mr-3"> 欢迎您 </span>
        <span class="text-sm mr-2" style="color: var(--el-text-color-regular)">
          {{ employeeInfoStore.name }}
        </span>
        <!-- <el-tag>Default</el-tag> -->
      </div>
    </template>
    <template #extra>
      <div class="flex items-center">
        <el-button type="danger" v-if="employeeInfoStore.id!=''" class="ml-2" @click="loginout()">退出登录</el-button>
      </div>
    </template>
  </el-page-header>
</template>

<style scoped>
</style>

<script setup>
import {useEmployeeInfoStore} from '../stores/empInfo'
import router from "../routers/router"
import {ref} from 'vue'
const employeeInfoStore = useEmployeeInfoStore()
function loginout(){
  router.push("/")
  employeeInfoStore.initEmployeeInfo()
  window.location.reload()
}
const onBack = () => {
  
  window.history.go(-1);
}
</script>