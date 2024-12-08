import axios from 'axios'
import { ElMessage } from 'element-plus';
import { useEmployeeInfoStore } from '../stores/empInfo'
import pinia from '../stores/index';
import NProgress from "nprogress";
import "nprogress/nprogress.css";
import router from "../routers/router"
const service = axios.create({
    baseURL:"http://localhost:8080/",
    timeout:50000,
});
// 添加请求拦截器
service.interceptors.request.use(
  (config) => {
    NProgress.start()
    const employeeInfoStore = useEmployeeInfoStore(pinia)
    let token = employeeInfoStore.token
    if(token) {
        (config.headers)['token'] = token
    }
    return config;
  }
);

service.interceptors.response.use(
    (response) => {
    NProgress.done()//关闭进度条
      if(response.data.code !== 200){
      // 判断响应状态码
      if (response.data.code == 501)  return  Promise.reject(ElMessage.error("用户名或密码或者身份有误"))
      else if (response.data.code == 502){
        router.push("/")
        return  Promise.reject(ElMessage.error("登录已过期"))  
      }
      else if (response.data.code == 503) return  Promise.reject(ElMessage.error("请正确填写所有数据"))
      else if(response.data.code == 504){
        window.history.go(-1);
        return Promise.reject(ElMessage.error("身份不正确，你可以重新登录来访问"))
      }
      } else {
        return response.data.data; /* 返回成功响应数据中的data属性数据 */
      }
    },
    (error) => {
    NProgress.done()//关闭进度条
      return Promise.reject(error.message);
    }
  );

export default service;