package controller;

import common.Result;
import common.ResultEnum;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pojo.Employee;
import pojo.User;
import pojo.vo.EmployeeInfoVo;
import pojo.vo.Uservo;
import service.EmployeeService;
import service.impl.EmployeeServiceImpl;
import util.JwHelper;
import util.WebUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

@WebServlet("/employee/*")
public class EmployeeController extends BaseController{
    EmployeeService employeeService = new EmployeeServiceImpl();
    /**
     * 处理普通员工的登录请求
     * 如果存在该用户且密码正确，就返回ok并且生成token，否则返回用户名或者密码错误
     * @param req   客户端发送过来的请求
     * @param resp  返回给客户端的请求
     * @throws ServletException
     * @throws IOException
     */
    protected void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 通过请求得到传递过来的用户信息
        User employee = WebUtil.readJson(req, User.class);
        // 判断用户名和密码是否正确,如果正确就生成用户id的token
        boolean exist = employeeService.CheckEmployeeInfo(employee);
        Result result = Result.build(null, ResultEnum.INFOERROR);
        if(exist) {
            // 将员工的id(工号)写入token中
            String token = JwHelper.createToken(Long.valueOf(employee.getId()));
            Map<String,Object> map = new HashMap<>();
            map.put("token",token);
            result = Result.ok(map);
        }
        WebUtil.writeJson(resp,result);
    }

    /**
     * 处理获取员工基本信息的请求：id,name,department,jobName,tel,birth,carId,address
     * @param req   客户端发送的请求
     * @param resp  服务端返回的请求
     * @throws ServletException
     * @throws IOException
     */
    protected void getDefaultInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getHeader("token");
        Result result = null;
        // 如果token为空或者token已经过期，返回没有登录
        // 否则的话从token中获取信息
        if (token==null || JwHelper.isExpiration(token)) {
            result = Result.build(null,ResultEnum.NOTLOGIN);
        }else{
            int id = JwHelper.getUserId(token).intValue();
            Map<String,Object> map = new HashMap<>();
            EmployeeInfoVo employee = employeeService.FindEmployeeById(id);
            employee.setPasswd("");
            map.put("employee",employee);
            if(employee!=null) result = Result.ok(map);
        }
        WebUtil.writeJson(resp,result);
    }

    /**
     * 处理员工修改基本信息的需求
     * @param req 客户端传递的数据，包含了所有基本信息
     * @param resp 响应给客户端的数据
     * @throws ServletException
     * @throws IOException
     */
    protected void amendInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EmployeeInfoVo employeeInfoVo = WebUtil.readJson(req, EmployeeInfoVo.class);// 传递过来的要修改的员工的数据
        int row = employeeService.AlterInfoOfEmplpoyee(employeeInfoVo);
        Result<Object> result = null;
        if(row <= 0) {
            result = Result.build(null,ResultEnum.NOTLOGIN);
        }else {
            result = Result.ok(null);
        }
        WebUtil.writeJson(resp,result);
    }

    /**
     * 完成员工修改密码的业务接口
     * @param req   传递的请求，包含了id，原始密码和新密码
     * @param resp  响应给客户端的请求
     * @throws ServletException
     * @throws IOException
     */
    protected void alterPassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Uservo uservo = WebUtil.readJson(req, Uservo.class);
        EmployeeInfoVo employeeInfoVo = employeeService.FindEmployeeById(uservo.getId());
        Result<Object> result = null;
        if (employeeInfoVo.getPasswd().equals(uservo.getOriginPasswd())) {
            int row = employeeService.alterPassword(uservo);
            if(row > 0) result = Result.ok(null);
        }else {
            result = Result.build(null,ResultEnum.INFOERROR);
        }
        WebUtil.writeJson(resp,result);
    }

    /**
     * 实现普通员工查询薪资的业务接口
     * @param req   参数中包含了该员工的id
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    protected void querySalary(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        int salary = employeeService.FindSalaryById(id);
        Map<String,Object> map = new HashMap<>();
        map.put("salary",salary);
        Result<Object> result = Result.ok(map);
        WebUtil.writeJson(resp,result);
    }
}
