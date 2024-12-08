import { createRouter, createWebHistory } from "vue-router";

// 路由
const router = createRouter({
    history:createWebHistory(),
    routes:[
        {
            path:'/',
            component:()=>import("../pages/login/index.vue"),
            name:"login",
        },
        {
            path:"/employee",
            component:()=> import("../pages/emplyee/index.vue"),
            name:"employee",
        },
        {
            path:"/department",
            component:()=>import ("../pages/department/index.vue"),
            name:"department",
        },
        {
            path:"/manage",
            component:()=>import("../pages/manager/index.vue"),
            name:"manage"
        }
    ]
})
export default router;