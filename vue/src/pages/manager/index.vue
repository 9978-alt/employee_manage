<script lang="ts" setup>
import { ref,reactive,onMounted,watch } from 'vue'
import request  from "../../utils/request"
import { Loading } from 'element-plus/es/components/loading/src/service.mjs';
import {useEmployeeInfoStore} from '../../stores/empInfo'
import { da } from 'element-plus/es/locale/index.mjs';
import router from "../../routers/router"
import { ElMessage,FormInstance,ElTable  } from 'element-plus'
const singleTableRef = ref<InstanceType<typeof ElTable>>()
let employeeInfoStore = useEmployeeInfoStore()
let tableData = reactive(
  [
    {
      "id":"",
      "depName":"",
      "empId":"",
      "empName":""
    }
  ]
)
let employData = reactive(
  [
    {
      "id":"",
      "name":"",
      "jobName":""
    }
  ]
)
const currentRow = ref()
let passwordForm = reactive({
  id:"",
  originPasswd:"" ,
  newPasswd:"",
  confirmPasswd:""
})
const formRef = ref<FormInstance>()
let password = ref(false)
let change=ref(false)
let changeName=ref("")
onMounted(
  ()=>{
    getDepartmentInfo()
    getManagerId()
  }
)
async function getDepartmentInfo(){
  let result = await request.get("manage/getDefault")
  for (const key in result.departments) {
    tableData[key] = result.departments[key]
  }
}
async function getManagerId(){
  let data = await request.get("manage/getManagerInfo")
  employeeInfoStore.id = data.manager.id
  employeeInfoStore.name = data.manager.name
}
function cancelalterPasswd(){
  password.value=false
  passwordForm.originPasswd = ""
  passwordForm.newPasswd = ""
  passwordForm.confirmPasswd=""
}
const CheckPasswd = (rule:any,value:any,callback:any)=>{
  if(value.length < 4){
    callback(new Error("密码长度至少为4位"))
  }else if(value.length > 12){
    callback(new Error("密码长度最多为12位数"))
  }else{
    callback()
  }
}
const CheckNewAndConPassword = (rule:any,value:any,callback:any)=>{
  if(value != passwordForm.newPasswd){
    callback(new Error("新密码与确认密码一致!"))
  }else {
    callback()
  }
}
let alterRules = {
  originPasswd:[
      {required:true,trigger: 'blur', validator: CheckPasswd }
    ],
  newPasswd:[
    {required:true,trigger: 'blur', validator: CheckPasswd }
  ],
  confirmPasswd:[
      {required:true,trigger: 'blur', validator: CheckNewAndConPassword }
  ],
}
async function alterPassword(){
  await formRef.value?.validate()
  passwordForm.id = employeeInfoStore.id
  // 发送请求更改密码
  // 检查确认密码和新密码是否一致
  request.post("manage/alterPassword",{"id":passwordForm.id,"originPasswd":passwordForm.originPasswd,"newPasswd":passwordForm.newPasswd})
  .then(response => {
    router.push("login")
    ElMessage.success("修改成功请重新登录");
  })
}
async function dbcopy(){
  request.get("manage/backup")
  .then(response=>{
    ElMessage.success("复制成功")
  })
  .catch(error =>{
    ElMessage.error("复制失败，请稍后再试")
  })
}
async function changeDepManager(row){
  change.value = true;
  changeName.value = `更换${row.depName}主管信息`
  let data = await request.get(`manage/allEmployeeOfDep?id=${row.id}`)
  console.log(data.employee)
  for (const key in data.employee) {
    employData[key] = data.employee[key]
  }
}
async function confirmChange(){
  //将选中的人的信息发送给服务端，交换服务端的信息
  console.log(currentRow.value)
  request.post("manage/changeManager",currentRow.value)
  .then (response => {
    change.value = false
    window.location.reload()
    ElMessage.success("更换成功")
  })
  .catch(error=>{
    ElMessage.error("更换失败，请稍后再试")
  })
}
const handleCurrentChange = (val) => {
  currentRow.value = val
}
</script>

<template>
  <div>
    
    <div class="operation">
      <el-button class="title" key="primary" type="primary" text bg @click="dbcopy()">备份数据库</el-button>
      <el-button class="title" key="success" type="success" text bg @click="password = true">修改密码</el-button><br>
    </div>
    <div style="text-align: -webkit-center">
      <el-table :data="tableData" stripe height="250" style="width: 90%">
        <el-table-column prop="id" label="部门编号" width="180" />
        <el-table-column prop="depName" label="部门名称" width="180" />
        <el-table-column prop="empId" label="主管工号" width="180" />
        <el-table-column prop="empName" label="主管姓名" width="180" />
        <el-table-column prop="operation" label="操作">
          <template #default="scoped">
            <el-button link type="primary" size="small" @click="changeDepManager(scoped.row)"> 更改主管 </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-drawer v-model="password" :with-header="false" direction="btt" size="50%">
      <div class="ps-title">修改密码</div>
      <el-form :model="passwordForm" 
                ref="formRef" 
                style="max-width: 600px"  
                :rules="alterRules"
              >
        <el-form-item label="原密码&nbsp;&nbsp;&nbsp;" prop="originPasswd">
          <el-input v-model="passwordForm.originPasswd" autocomplete="off" type="password"></el-input>
        </el-form-item>
        <el-form-item label="新密码&nbsp;&nbsp;&nbsp;" prop="newPasswd">
          <el-input v-model="passwordForm.newPasswd" autocomplete="off" type="password"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPasswd">
          <el-input v-model="passwordForm.confirmPasswd" autocomplete="off" type="password"></el-input>
        </el-form-item>
        <el-button type="info"  @click="cancelalterPasswd()">取 消</el-button>
        <el-button type="primary" @click="alterPassword()">确 认</el-button>
      </el-form>
    </el-drawer>

    <el-drawer
    v-model="change"
    :title="changeName"
    direction="rtl"
    size="40%"
  >
  <el-button type="primary" @click="confirmChange()">确认更换</el-button>
    <el-table
      ref="singleTableRef"
      :data="employData"
      :highlight-current-row="true"
      style="width: 100%"
      @current-change="handleCurrentChange"
    >
      <el-table-column type="index" width="50" />
      <el-table-column property="id" label="id" width="120" />
      <el-table-column property="name" label="name" width="120" />
      <el-table-column property="jobName" label="jobName" />
    </el-table>

  </el-drawer>



  </div>
</template>

<style scoped>
div{
  color: dimgrey;
}
.el-table .warning-row {
  --el-table-tr-bg-color: var(--el-color-warning-light-9);
}
.el-table .success-row {
  --el-table-tr-bg-color: var(--el-color-success-light-9);
}
.operation {
  margin-left: 5%;
}
.ps-title {
  margin-bottom: 10px;
}
</style>
 