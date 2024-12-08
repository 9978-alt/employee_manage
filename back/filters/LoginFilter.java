package filters;

import common.Result;
import common.ResultEnum;
import dao.DepartmentDao;
import dao.EmployeeDao;
import dao.ManagerDao;
import dao.impl.DepartmentDaoImpl;
import dao.impl.EmployeeDaoImpl;
import dao.impl.ManagerDaoImpl;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pojo.Department;
import pojo.Employee;
import pojo.Manager;
import util.JwHelper;
import util.WebUtil;

import java.io.IOException;

@WebFilter(urlPatterns = {"/employee/*","/department/*","/manage/*"})
public class LoginFilter implements Filter {
    EmployeeDao employeeDao = new EmployeeDaoImpl();
    DepartmentDao departmentDao = new DepartmentDaoImpl();
    ManagerDao managerDao = new ManagerDaoImpl();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //在访问时获取token判断是否可以登录
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request  = (HttpServletRequest) servletRequest;
        String token = request.getHeader("token");
        if(token==null && JwHelper.isExpiration(token)){
            WebUtil.writeJson(response, Result.build(null, ResultEnum.NOTLOGIN));
        }else{
            // 根据身份判断
            int id = JwHelper.getUserId(token).intValue();
            String[] split = request.getRequestURI().split("/");
            String status = split[split.length-2];
            System.out.println(status);
            if(status.equals("employee")){
                Employee employee = employeeDao.FindEmployeeById(id);
                if(employee == null) WebUtil.writeJson(response,Result.build(null,ResultEnum.INCORRECTSTATUS));
                else filterChain.doFilter(request,response);
            }else if(status.equals("department")){
                Department department = departmentDao.FindDepartmentManagerById(id);
                if(department == null) WebUtil.writeJson(response,Result.build(null,ResultEnum.INCORRECTSTATUS));
                else filterChain.doFilter(request,response);
            } else {
                Manager managerById = managerDao.findManagerById(id);
                if(managerById==null) WebUtil.writeJson(response,Result.build(null,ResultEnum.INCORRECTSTATUS));
                else filterChain.doFilter(request,response);
            }

        }
    }
}
