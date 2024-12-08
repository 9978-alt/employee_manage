<template>
  <div class="login-container">
    <el-form
      :model="loginEmpolyee"
      label-width="80px"
      class="login-form"
      ref="formRef"
      :rules="loginRules"
      >
      <h2>用户登录</h2>

      <el-form-item prop="status">
        <el-radio-group v-model="loginEmpolyee.status" >
          <el-radio value="employee">员工</el-radio>
          <el-radio value="department">部门主管</el-radio>
          <el-radio value="manager">系统管理员</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="工 号" prop="id">
        <el-input
          type="text"
          autocomplete="off"
          placeholder="请输入用户名"
          v-model="loginEmpolyee.id"
        ></el-input>
      </el-form-item>
      <el-form-item label="密 码" prop="passwd">
        <el-input
          type="password"
          autocomplete="off"
          placeholder="请输入密码"
          v-model="loginEmpolyee.passwd"
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click.native.prevent="login()">登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import {ref,reactive} from 'vue'
import request from '../../utils/request'
import { da, el, pa, tr } from 'element-plus/es/locale/index.mjs';
import { FormInstance } from 'element-plus';
import { useEmployeeInfoStore } from '../../stores/empInfo';
import router from "../../routers/router"

let loginEmpolyee = reactive(
  {
    id:"",
    passwd:"",
    status:""
  }
)
const employeeInfoStore = useEmployeeInfoStore()
let formRef = ref<FormInstance>()
let loading = ref(false)
const CheckUsername = (rule:any,value:any,callback:any)=>{
    if(value.length < 4){
      callback(new Error("工号长度不能小于2位"))
    }else{
      callback()
    }
}
const CheckPasswd = (rule:any,value:any,callback:any)=>{
  if(value.length < 4){
    callback(new Error("密码长度至少为4位"))
  }else if(value.length >12){
    callback(new Error("密码长度最多为12位数"))
  }else{
    callback()
  }
}
const CheckStatus = (rule:any,value:any,callback:any)=>{
    if(value == "") callback(new Error("请选择你要登录的账号"))
    else callback()
}
let loginRules = {
    id:[
      {required:true,trigger:"blur",validator: CheckUsername }
    ],
    passwd:[
      {required:true,trigger: 'blur', validator: CheckPasswd}
    ],
    status:[
      {required:true, validator: CheckStatus}
    ]
}

// 发送登录请求
const login = async function(){
  //  检查所有信息都已经填写完毕
    await formRef.value?.validate()
    loading.value = true;

    // 根据身份进行验证登录
    var path = ""
    // console.log(loginEmpolyee)
    if(loginEmpolyee.status == "employee")  path = "employee"
    else if(loginEmpolyee.status == "manager") path = "manage"
    else path = "department"
    await employeeInfoStore.login(path,{"id":loginEmpolyee.id,"passwd":loginEmpolyee.passwd})
    // 登录成功，给当前用户的pinia数据赋值
    router.push(path)
  
  }

</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: top;
  height: 100vh;
}
.login-form {
  width: 400px;
  text-align: center;
}
</style>