package controller;

import common.Result;
import common.ResultEnum;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pojo.Employee;
import pojo.Job;
import pojo.vo.EmployeeInfoVo;
import service.DepartmentService;
import service.EmployeeService;
import service.JobService;
import service.impl.DepartmentServiceImpl;
import service.impl.EmployeeServiceImpl;
import service.impl.JobServiceImpl;
import util.JwHelper;
import util.WebUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/department/*")
public class DepartmentController extends BaseController{
    DepartmentService departmentService = new DepartmentServiceImpl();
    EmployeeService employeeService = new EmployeeServiceImpl();
    JobService jobService = new JobServiceImpl();
    /**
     * 处理部门主管的登录请求
     * @param req 客户端发送过来的请求
     * @param resp 返回给客户端的请求
     * @throws ServletException
     * @throws IOException
     */
    protected void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Employee employee = WebUtil.readJson(req, Employee.class);
        boolean exist = departmentService.CheckDepartmentInfo(employee);
        Result<Object> result = Result.build(null, ResultEnum.INFOERROR);
        if(exist){
            String token = JwHelper.createToken(Long.valueOf(employee.getId()));
            Map<String,Object> map = new HashMap<>();
            map.put("token",token);
            result = Result.ok(map);
        }
        WebUtil.writeJson(resp,result);
    }

    /**
     * 实现获取主管信息的接口
     * @param req   请求头中包含了token信息
     * @param resp  响应该主管的id,name,depId即可，没有返回详细信息
     * @throws ServletException
     * @throws IOException
     */
    protected void getLoginInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getHeader("token");
        Result<Object> result = null;
        if(token==null || JwHelper.isExpiration(token)){
            result = Result.build(null,ResultEnum.NOTLOGIN);
        }else {
            int id = JwHelper.getUserId(token).intValue();
            EmployeeInfoVo employeeInfoVo = employeeService.FindEmployeeById(id);
            Map<String,Object> map = new HashMap<>();
            map.put("department",employeeInfoVo);
            result = Result.ok(map);
        }
        WebUtil.writeJson(resp,result);
    }

    /**
     * 获取指定部门的所有员工信息
     * @param req   请求中包含了参数：部门id
     * @param resp  响应该部门的所有人员信息
     * @throws ServletException
     * @throws IOException
     */
    protected void getEmployeeList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int depId = Integer.parseInt(req.getParameter("id"));
        List<EmployeeInfoVo> employeeInfoVoList = employeeService.getEmployeeListByDep(depId);
        Map<String,Object> map = new HashMap<>();
        map.put("employees",employeeInfoVoList);
        Result<Object> result = Result.ok(map);
        WebUtil.writeJson(resp,result);
    }

    /**
     * 实现点击详细信息查看个人详细信息的接口
     * @param req   请求中包含了要查找的用户的id
     * @param resp  响应用户的全部信息给客户端
     * @throws ServletException
     * @throws IOException
     */
    protected void getDetailInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        EmployeeInfoVo employeeInfoVo = employeeService.FindEmployeeById(id);
        Map<String,Object> map = new HashMap<>();
        map.put("employee",employeeInfoVo);
        WebUtil.writeJson(resp,Result.ok(map));
    }

    /**
     *  根据指定id删除员工
     * @param req   请求中包含参数：员工id
     * @param resp  响应包含是否删除成功的状态
     * @throws ServletException
     * @throws IOException
     */
    protected void delEmployee(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        int row = employeeService.delEmployeeById(id);
        Result<Object> result = null;
        if(row > 0) result = Result.ok(null);
        else result = Result.build(null,ResultEnum.NOTLOGIN);
        WebUtil.writeJson(resp,result);
    }

    /**
     * 根据name查询员工
     * @param req   请求中包含了员工的姓名
     * @param resp  name为该员工的名字的集合
     * @throws ServletException
     * @throws IOException
     */
    protected void queryEmployee(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        int id = Integer.parseInt(req.getParameter("depId"));
        List<EmployeeInfoVo> employeeInfoVoList = employeeService.getEmployeeByName(name,id);
        Map<String,Object> map = new HashMap<>();
        map.put("employee",employeeInfoVoList);
        Result result = Result.ok(map);
        WebUtil.writeJson(resp,result);
    }

    /**
     * 指定部门的工作名称
     * @param req   请求中包含了部门id
     * @param resp  响应该部门的工作分类
     * @throws ServletException
     * @throws IOException
     */
    protected void getJob(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        List<Job> job = jobService.getJobByDepId(id);
        Map<String,Object> map = new HashMap<>();
        map.put("jobs",job);
        WebUtil.writeJson(resp,Result.ok(map));
    }

    /**
     * 添加一个员工数据
     * @param req   请求中包含了一个员工的完整信息
     * @param resp  响应添加状态
     * @throws ServletException
     * @throws IOException
     */
    protected void addEmployee(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Employee employee = WebUtil.readJson(req, Employee.class);
        employee.setPasswd("888888");
        int row = employeeService.addEmployee(employee);
        Result<Object> result = null;
        if(row >0) result = Result.ok(null);
        else  result = Result.build(null,ResultEnum.NOTLOGIN);
        WebUtil.writeJson(resp,result);
    }

    /**
     * 添加一个职位
     * @param req   请求中包含了部门id，职位名称，对应薪资
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    protected void addJob(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Job job = WebUtil.readJson(req, Job.class);
        Result<Object> result = null;
        if(job.getJobName().equals("") || job.getJobName().trim().equals("")  ||job.getSalary() == null) result = Result.build(null,ResultEnum.ABNORMALVALUE);
        else {
            int row = jobService.addJobByDepId(job);
            if(row>0) result = Result.ok(null);
            else result = Result.build(null,ResultEnum.NOTLOGIN);
        }
        WebUtil.writeJson(resp,result);
    }

    /**
     * 获取部门的职位和薪资信息
     * @param req   请求中包含了部门id
     * @param resp  以json格式响应Job对象中的所有信息
     * @throws ServletException
     * @throws IOException
     */
    protected void getJobs(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        Map<String,Object> map = new HashMap<>();
        List<Job> job = jobService.getAllJobInfo(id);
        map.put("jobs",job);
        WebUtil.writeJson(resp,Result.ok(map));
    }

    /**
     *  更改工作名称/薪资
     * @param req   请求中包含了job id,jobName,salary
     * @param resp  修改成功响应200，否则返回502/503
     * @throws ServletException
     * @throws IOException
     */
    protected void modifyJobs(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Job job = WebUtil.readJson(req, Job.class);
        String jobName = job.getJobName();
        Integer salary = job.getSalary();
        Result<Object> result = null;
        if(jobName.equals("")|| jobName.trim().equals("") || salary == null){
            result = Result.build(null,ResultEnum.ABNORMALVALUE);
        }else {
            int row = jobService.modifyJob(job);
            if(row >0) result = Result.ok(null);
            else result = Result.build(null,ResultEnum.NOTLOGIN);
        }
        WebUtil.writeJson(resp,result);
    }
}
