package filters;

import common.Result;
import common.ResultEnum;
import dao.EmployeeDao;
import dao.impl.EmployeeDaoImpl;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pojo.vo.EmployeeInfoVo;
import service.EmployeeService;
import service.impl.EmployeeServiceImpl;
import util.JwHelper;
import util.WebUtil;

import java.io.IOException;

public class EmployeeFilter implements Filter {
    EmployeeDao employeeDao = new EmployeeDaoImpl();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request  = (HttpServletRequest) servletRequest;
        String token = request.getHeader("token");
        int id = JwHelper.getUserId(token).intValue();
        EmployeeInfoVo employeeInfoVo = employeeDao.FindEmpolyeeAllInfoById(id);
        if(employeeInfoVo!=null){
            filterChain.doFilter(request,response);
        }else {
            WebUtil.writeJson(response, Result.build(null, ResultEnum.INCORRECTSTATUS));
        }
    }
}
