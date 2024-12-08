import {defineStore} from 'pinia'
import { getToken, removeToken, setToken } from '../utils/token-utils'
import { th } from 'element-plus/es/locale/index.mjs';
import request from '../utils/request'

export const useEmployeeInfoStore = defineStore("employeeInfo",
    {
        state:()=>({
            token:getToken(),
            id:'',
            name:'',
            sex:'',
            rate:'',
            birth:'',
            depId:'',
            depName:'',
            jobId:"",
            jobName:'',
            tel:"",
            cardId:"",
            address:"",
        }),

        actions:{
            initEmployeeInfo(){
                removeToken()
                this.id = ""
                this.name=""
                this.sex=""
                this.depId=""
                this.jobId="",
                this.depName=""
                this.jobName="",
                this.tel=""
                this.birth=""
                this.cardId=""
                this.address=""
                this.rate=""
            },
            async login(path,data){
                const result = await request.post(`${path}/login`,data)
                const token = result.token
                this.token = token
                setToken(token)
            },
        },
    }
);