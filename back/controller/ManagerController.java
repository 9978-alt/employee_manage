package controller;

import common.Result;
import common.ResultEnum;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pojo.Employee;
import pojo.Manager;
import pojo.User;
import pojo.vo.DepartmentQueryVo;
import pojo.vo.EmployeeInfoVo;
import pojo.vo.Uservo;
import service.ManagerService;
import service.impl.ManagerServiceImpl;
import util.JwHelper;
import util.WebUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/manage/*")
public class ManagerController extends BaseController{
    ManagerService managerService = new ManagerServiceImpl();
    /**
     * 接收系统管理员的请求
     * @param req   客户端发送过来的请求
     * @param resp  服务端响应回去的响应
     * @throws ServletException
     * @throws IOException
     */
    protected void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("manager 收到登录请求");
        User manager = WebUtil.readJson(req, User.class);
        boolean exits = managerService.CheckManagerInfo(manager);
        Result result = null;
        if(exits){
            String token = JwHelper.createToken(Long.valueOf(manager.getId()));
            Map<String,Object> data = new HashMap<>();
            data.put("token",token);
            result = Result.ok(data);
        }else result = Result.build(null, ResultEnum.INFOERROR);
        WebUtil.writeJson(resp,result);
    }

    /**
     * 实现登录后自动获取部门信息的请求
     * @param req 请求中没有任何参数
     * @param resp  返回客户端所有部门的信息的列表：id，name,empId,empName
     * @throws ServletException
     * @throws IOException
     */
    protected void getDefault(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<DepartmentQueryVo> departmentQueryVos =  managerService.getDedault();
        Map<String,Object> map = new HashMap();
        map.put("departments",departmentQueryVos);
        Result<Map<String, Object>> result = Result.ok(map);
        WebUtil.writeJson(resp,result);
    }

    /**
     * 管理员用户登陆后自动发起请求获取管理员用户的信息
     * @param req   在请求中包含了token请求头
     * @param resp  返回id，name给客户端
     * @throws ServletException
     * @throws IOException
     */
    protected void getManagerInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 根据token获取用户id查询用户信息
        String token = req.getHeader("token");
        Result<Object> result = null;
        if(token == null || JwHelper.isExpiration(token)){
            result = Result.build(null,ResultEnum.NOTLOGIN);
        }else {
            int id = JwHelper.getUserId(token).intValue();
            Manager manager = managerService.getManagerInfo(id);
            manager.setPasswd("");
            Map<String,Object> map = new HashMap<>();
            map.put("manager",manager);
            result =  Result.ok(map);
        }
        WebUtil.writeJson(resp,result);
    }

        /**
     * 实现管理员用户修改密码的接口
     * @param req   客户端发送过来的请求中包含了参数id，originPasswd，newPasswd
     * @param resp  修改成功响应状态200，登录状态失效响应状态502
     * @throws ServletException
     * @throws IOException
     */
    protected void alterPassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Uservo uservo = WebUtil.readJson(req, Uservo.class);
        Result<Object> result = null;
        int row = managerService.alterManagerPasswd(uservo);
        if(row>0){
            result = Result.ok(null);
        }
        else result = Result.build(null,ResultEnum.INFOERROR);
        WebUtil.writeJson(resp,result);
    }

    /**
     * 实现对数据库进行备份的操作
     * @param req  请求中不包含参数
     * @param resp  响应给客户端的数据
     * @throws ServletException
     * @throws IOException
     */
    protected void backup(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        managerService.backup();
        Result<Object> result = Result.ok(null);
        WebUtil.writeJson(resp,result);
    }

    /**
     * 客户端点击更换主管按钮向服务端发送请求获取该部门的所有员工的数据
     * @param req 请求中包含了要更换的部门的部门id
     * @param resp  响应中包含了该部门的所有人员信息
     * @throws ServletException
     * @throws IOException
     */
    protected void allEmployeeOfDep(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        List<EmployeeInfoVo> employees  = managerService.getEmployeeOfDepId(id);
        Map<String,Object> map = new HashMap<>();
        map.put("employee",employees);
        Result<Map<String, Object>> result = Result.ok(map);
        WebUtil.writeJson(resp,result);
    }

    /**
     * 实现更换部门主管的接口
     * @param req   请求中包含了新主管的信息
     * @param resp  修改成功响应状态200，登录状态失效响应状态502
     * @throws ServletException
     * @throws IOException
     */
    protected void changeManager(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EmployeeInfoVo employeeInfoVo = WebUtil.readJson(req,EmployeeInfoVo.class);
        managerService.changeManager(employeeInfoVo);
        Result<Object> result = Result.ok(null);
        WebUtil.writeJson(resp,result);
    }
}
