<script lang="ts" setup>
import { ref,reactive,onMounted,watch } from 'vue'
import request  from "../../utils/request"
import { Loading } from 'element-plus/es/components/loading/src/service.mjs';
import {useEmployeeInfoStore} from '../../stores/empInfo'
import { da, de, ro } from 'element-plus/es/locale/index.mjs';
import router from "../../routers/router"
import { ElMessage,FormInstance,ElTable,ElMessageBox, timeUnits } from 'element-plus'
const employeeInfoStore = useEmployeeInfoStore()
let tableData = ref([])
const detailForm = reactive(
  {
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
  }
)
const addForm = reactive(
  {
    name:'',
    sex:'',
    rate:0,
    birth:'',
    jobId:'',
    tel:"",
    cardId:"",
    address:"",
    depId:employeeInfoStore.depId
  }
)
onMounted(async ()=>{
  await getLoginInfo()
  getEmployeeList()
})
async function getLoginInfo(){
  let data = await request.get("department/getLoginInfo")
  for (const key in data.department) {
    employeeInfoStore[key] = data.department[key]
  }
}
async function getEmployeeList() {
  let data = await request.get(`department/getEmployeeList?id=${employeeInfoStore.depId}`)
  tableData.value = data.employees
  // for (const key in data.employees) {
  //     tableData[key] = data.employees[key]
  // }
}
let detail = ref(false)
async function getDetailInfo(row){
  detail.value = true
  let data = await request.get(`department/getDetailInfo?id=${row.id}`)
  for (const key in data.employee) {
      detailForm[key] = data.employee[key]
  }
}
async function delEmployee(row){
  ElMessageBox.confirm(
    '确定删除该员工？',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
  .then(()=>{
      request.get(`department/delEmployee?id=${row.id}`)
      .then( response=>{
        window.location.reload()
        ElMessage.success("删除成功")
      })
      .catch( error=>{
        ElMessage.error("删除失败请稍后再试")
      })
  })
}
let input= ref("")
async function queryEmployee(){
  let data = await request.get(`department/queryEmployee?name=${input.value}&depId=${employeeInfoStore.depId}`)
  tableData.value = data.employee
}
let add=ref(false)
let jobSelect = ref([])
async function getAddInfo(){
  add.value=true
  // 发送请求获取该部门的工作分类
  let data = await request.get(`department/getJob?id=${employeeInfoStore.depId}`)
  jobSelect.value = data.jobs
}
async function addEmployee(){
  request.post(`department/addEmployee`,addForm)
  .then( response=>{
    ElMessage.success("添加成功")
    window.location.reload()
  })
  .catch(error=>{
    ElMessage.error("添加失败，稍后再试")
  })
}
let addJob = ref(false)
let addJobInfo = reactive({
  depId:employeeInfoStore.depId,
  jobName:"",
  salary:""
})
let addJobTitle = `${employeeInfoStore.depName}添加职位`
async function addJobRequest() {
  request.post("department/addJob",addJobInfo)
  .then(response=>{
    ElMessage.success("添加成功")
    window.location.reload()
  })
}
let salaryManageFlag = ref(false)
let salaryData = ref([])
let salaryModify = ref()
async function getSalaryInfo(){
  salaryManageFlag.value = true
  let data = await request.get(`department/getJobs?id=${employeeInfoStore.depId}`)
  salaryData.value = data.jobs
}
function modifySalary(row) {
  request.post("department/modifyJobs",row)
  .then(response=>{
    ElMessage.success("修改成功")
  })
}
</script>

<template>
  <div>
    <div class="operations">
      <el-button class="title" key="primary" type="primary" text bg @click="getAddInfo()">添加员工</el-button>
      <el-button class="title" key="primary" type="primary" text bg @click="getSalaryInfo()">薪资管理</el-button>
      <el-button class="title" key="primary" type="primary" text bg @click="addJob = true">添加职位</el-button>
      <label >
        <el-input v-model="input" style="width: 120px" placeholder="请输入员工姓名"/>
        <el-button class="title" key="primary" type="primary" size="default" @click="queryEmployee()">查询</el-button>
      </label>
    </div>
    
    <div style="text-align: -webkit-center">
      <el-table :data="tableData" highlight-current-row stripe style="width: 90%" height="400">
        <el-table-column type="index" width="50"/>
        <el-table-column prop="name" label="姓名" width="70" />
        <el-table-column prop="id" label="工号" width="70" />
        <el-table-column prop="sex" label="性别" width="70" />
        <el-table-column prop="tel" label="电话号码" width="130" />
        <el-table-column prop="birth" label="出生日期" width="130" />
        <el-table-column prop="address" label="地址" width="130" />
        <el-table-column prop="jobName" label="职位" width="130" />
        <el-table-column prop="operation" label="操作">
          <template #default="scoped">
            <el-button  type="success" size="small" @click="getDetailInfo(scoped.row)"> 详情 </el-button>
            <el-button  type="danger" size="small" @click="delEmployee(scoped.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-drawer
    v-model="detail"
    title="个人详细信息"
    direction="rtl"
    >
      <el-form  label-width="auto" style="max-width: 600px">
        <el-form-item label="姓名" >
        <el-input disabled v-model="detailForm.name"/>
      </el-form-item>

      <el-form-item label="职位">
        <el-button type="primary" plain>{{ detailForm.jobName }}</el-button>
        <el-button type="primary" plain>{{ detailForm.depName }}</el-button>
      </el-form-item>

      <el-form-item  label="性别">
        <el-radio-group v-model="detailForm.sex">
        <el-radio value="男"  size="large">男</el-radio>
        <el-radio value="女" size="large">女</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="联系方式">
        <el-input disabled v-model="detailForm.tel"/>
      </el-form-item>

      <el-form-item label="出生日期">
        <el-col>
          <el-date-picker disabled v-model="detailForm.birth" type="date" placeholder="Pick a date" style="width: 100%"/>
        </el-col>
      </el-form-item>

      <el-form-item label="身份证号">
        <el-input disabled v-model="detailForm.cardId"/>
      </el-form-item>

      <el-form-item label="联系地址">
        <el-input disabled v-model="detailForm.address"/>
      </el-form-item>
      </el-form>

    </el-drawer>

    <el-drawer
    v-model="add"
    title="添加员工"
    direction="ltr"
    >
      <el-form  label-width="auto" style="max-width: 600px">
          <el-form-item label="姓名" >
          <el-input v-model="addForm.name" placeholder="输入姓名"/>
        </el-form-item>

        <el-form-item label="职位">
          <el-select v-model="addForm.jobId" placeholder="选择职位">
          <el-option v-for="(select,index) in jobSelect" :label=select.jobName  :value=select.id />
        </el-select>
        </el-form-item>

        <el-form-item  label="性别">
          <el-radio-group v-model="addForm.sex">
          <el-radio value="男" size="large">男</el-radio>
          <el-radio value="女" size="large">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="联系方式">
          <el-input v-model="addForm.tel" placeholder="输入联系方式"/>
        </el-form-item>

        <el-form-item label="出生日期">
          <el-col>
            <el-date-picker v-model="addForm.birth" type="date" placeholder="选择出生日期" style="width: 100%"/>
          </el-col>
        </el-form-item>

        <el-form-item label="身份证号">
          <el-input  v-model="addForm.cardId" placeholder="输入身份证号"/>
        </el-form-item>

        <el-form-item label="联系地址">
          <el-input v-model="addForm.address" placeholder="输入联系地址"/>
        </el-form-item>
        <el-button  type="primary" size="small" @click="addEmployee()">添加</el-button>
      </el-form>

    </el-drawer>

    <el-drawer
    v-model="addJob"
    :title=addJobTitle
    direction="ltr"
    >
      <el-form  label-width="auto" style="max-width: 600px">
        <el-form-item label="工作名称" >
          <el-input v-model="addJobInfo.jobName" placeholder="输入工作名称"/>
        </el-form-item>

        <el-form-item label="薪资" >
          <el-input v-model="addJobInfo.salary" placeholder="输入薪资(必须是整数)"/>
        </el-form-item>
        <el-button  type="primary" size="small" @click="addJobRequest()">添加</el-button>
      </el-form>

    </el-drawer>

    <el-drawer
    v-model="salaryManageFlag"
    title="薪资管理"
    direction="ltr"
    size="40%"
    >
      <el-table :data="salaryData" stripe style="width: 100%">
        <el-table-column type="index" width="50" />
        <el-table-column label="工作名称">
          <template #default="scoped">
            <el-input v-model="scoped.row.jobName" placeholder="输入工作名称"/>
          </template>
        </el-table-column>
        <el-table-column label="薪资">
          <template #default="scoped">
            <el-input v-model="scoped.row.salary" placeholder="输入薪资(必须是整数)"/>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scoped">
            <el-button type="primary" size="small" @click="modifySalary(scoped.row)"> 更改 </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>
  </div>
</template>

<style scoped>
div {
  color: gray;
}
.operations {
  margin-left: 5%;
}
.title {
  margin-right: 10px;
}
.operations {
  margin-bottom: 10px;
}

</style>
