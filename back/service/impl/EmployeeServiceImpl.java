package service.impl;

import dao.EmployeeDao;
import dao.impl.EmployeeDaoImpl;
import pojo.Employee;
import pojo.User;
import pojo.vo.EmployeeInfoVo;
import pojo.vo.Uservo;
import service.EmployeeService;

import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {
    EmployeeDao employeeDao = new EmployeeDaoImpl();

    @Override
    public int addEmployee(Employee employee) {
        return  employeeDao.addEmployee(employee);
    }

    @Override
    public List<EmployeeInfoVo> getEmployeeByName(String name,int id) {
        return  employeeDao.getEmployeeListByName(name,id);
    }

    @Override
    public int delEmployeeById(int id) {
        return  employeeDao.delEmployeeById(id);
    }

    @Override
    public List<EmployeeInfoVo> getEmployeeListByDep(int depId) {
        return employeeDao.getEmployeeList(depId);
    }

    @Override
    public int FindSalaryById(int id) {
        return employeeDao.findSalaryById(id);
    }

    @Override
    public int alterPassword(Uservo uservo) {
        return employeeDao.alterPasswordById(uservo);

    }

    @Override
    public int AlterInfoOfEmplpoyee(EmployeeInfoVo employeeInfoVo) {
        return employeeDao.AlterInfoAll(employeeInfoVo);
    }

    @Override
    public EmployeeInfoVo FindEmployeeById(int id) {
        return employeeDao.FindEmpolyeeAllInfoById(id);
    }

    @Override
    public boolean CheckEmployeeInfo(User employee) {
        // 根据用户名查询用户是否存在
        Employee loginEmployee = employeeDao.FindEmployeeById(employee.getId());
        if(loginEmployee != null && loginEmployee.getPasswd().equals(employee.getPasswd())) return true;
        return false;
    }
}
