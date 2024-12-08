<script lang="ts" setup>
import { ref,reactive,onMounted,watch } from 'vue'
import {useEmployeeInfoStore} from '../../stores/empInfo'
import request  from "../../utils/request"
import { ElMessage,FormInstance } from 'element-plus'
import router from "../../routers/router"
let employeeInfoStore = useEmployeeInfoStore()
// do not use same name with ref
let form = reactive({
  id:"",
  name:'',
  sex:'',
  rate:'',
  birth:'',
  depName:'',
  jobName:'',
  tel:"",
  cardId:"",
  address:""
})
let passwordForm = reactive({
  id:"",
  originPasswd:"" ,
  newPasswd:"",
  confirmPasswd:""
})
let password = ref(false)
let salary = ref(false)
let salary_num = ref(0)
const formRef = ref<FormInstance>()
const loading = ref(false)
// 页面挂载完毕后向服务端发送请求获取当前用户的基本信息
// 组件挂载的生命周期钩子
onMounted(() => {
  getDetailInfomation()
});
async function getDetailInfomation(){
  // console.log(employeeInfoStore.token);
  // 将从后端获得的数据分别存储在pinia数据和临时响应式数据中
  let result = await request.get("employee/getDefaultInfo")
  for(const key in result.employee){
    employeeInfoStore[key] = result.employee[key]
    form[key] = result.employee[key]
  }
}
const compare = ()=>{
  // 比较数据是否改变
  for (const key in form) {
    if(form[key] !== employeeInfoStore[key]) return true
  }
  return false
}
function alter() {
  // 检查表单是否有修改
  if(!compare()){
    console.log(compare())
    ElMessage.error("没有修改信息");
    return
  }else{
    // 如果有修改数据，发送请求更新数据库
    request.post("employee/amendInfo", form)
    .then(response => {
      ElMessage.success("信息修改成功！");
    })
    .catch(error => {
      ElMessage.error("修改失败，请稍后再试");
    });
  }
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
      {required:true,trigger: 'blur', validator: CheckPasswd}
    ],
  newPasswd:[
    {required:true,trigger: 'blur', validator: CheckPasswd}
  ],
  confirmPasswd:[
      {required:true,trigger: 'blur', validator: CheckNewAndConPassword}
  ],
}
async function alterPassword(){
  await formRef.value?.validate()
  loading.value = true
  passwordForm.id = employeeInfoStore.id
  // 发送请求更改密码
  // 检查确认密码和新密码是否一致
  request.post("employee/alterPassword",{"id":passwordForm.id,"originPasswd":passwordForm.originPasswd,"newPasswd":passwordForm.newPasswd})
  .then(response => {
    router.push("login")
    ElMessage.success("修改成功请重新登录");
  })

}
function cancelalterPasswd(){
  password.value=false
  passwordForm.originPasswd = ""
  passwordForm.newPasswd = ""
  passwordForm.confirmPasswd=""
}
async function querySalary(){
  salary.value = true
  let data = await request.get(`employee/querySalary?id=${employeeInfoStore.id}`)
  salary_num.value = data.salary
}
</script>

<template>
  <div id="content" class="content" style="text-align: -webkit-center"> 
    <el-form  label-width="auto" style="max-width: 600px">
      <el-form-item label=" ">
        <el-button class="title" key="primary" type="primary" text bg @click="password = true" > 修改密码 </el-button>
        <el-button class="title" key="success" type="success" text bg @click="querySalary()"> 查询薪资 </el-button><br>
    </el-form-item>

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

    <el-drawer v-model="salary" :with-header="false" direction="btt" size="50%">
      <!--<div class="ps-title">查看薪资</div>-->
      你的当前薪资为：{{ salary_num }} 元<br>
      再接再厉！
    </el-drawer>

    <el-form-item label="姓名">
      <el-input v-model="form.name"/>
    </el-form-item>

    <el-form-item label="职位">
      <el-button type="primary" plain>{{ form.jobName }}</el-button>
      <el-button type="primary" plain>{{ form.depName }}</el-button>
    </el-form-item>

    <el-form-item label="性别">
      <el-radio-group v-model="form.sex">
      <el-radio value="男" size="large">男</el-radio>
      <el-radio value="女" size="large">女</el-radio>
      </el-radio-group>
    </el-form-item>

    <el-form-item label="联系方式">
      <el-input v-model="form.tel"/>
    </el-form-item>

    <el-form-item label="出生日期">
      <el-col>
        <el-date-picker v-model="form.birth" type="date" placeholder="Pick a date" style="width: 100%"/>
      </el-col>
    </el-form-item>

    <el-form-item label="身份证号">
      <el-input v-model="form.cardId"/>
    </el-form-item>

    <el-form-item label="联系地址">
      <el-input v-model="form.address"/>
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="alter()">修改信息</el-button>
    </el-form-item>
  </el-form>

    
  </div>
</template>

<style scoped>
.content {
  color: rgb(90, 86, 86);
}
.el-input{
  /* border:1px solid salmon */
  background-color: gray
}
/* .content {
  text-align: center;
  width: 700px;
} */
.title {
  margin-bottom: 10px;
}
.ps-title{
  margin-bottom: 10px;
}


</style>
