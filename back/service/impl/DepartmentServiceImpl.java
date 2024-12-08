package service.impl;

import dao.DepartmentDao;
import dao.EmployeeDao;
import dao.impl.DepartmentDaoImpl;
import dao.impl.EmployeeDaoImpl;
import pojo.Department;
import pojo.Employee;
import service.DepartmentService;

public class DepartmentServiceImpl implements DepartmentService {
    DepartmentDao departmentDao = new DepartmentDaoImpl();
    EmployeeDao employeeDao = new EmployeeDaoImpl();
    @Override
    public boolean CheckDepartmentInfo(Employee employee) {
        // 根据id 判断该用户是否存在
        Employee employee1 = employeeDao.FindEmployeeById(employee.getId());
        Department department = departmentDao.FindDepartmentManagerById(employee.getId());
        boolean exist = false;
        if(employee1!=null && employee1.getPasswd().equals(employee.getPasswd())) exist=true;

        return exist && department!=null;
    }
}
