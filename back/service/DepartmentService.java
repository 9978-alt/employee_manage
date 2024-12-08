package service;

import pojo.Employee;

public interface DepartmentService {
    /**
     * 根据用户信息判断该主管是否存在，如果存在即可以登录，不存在就不能登录
     * @param employee 登录信息
     * @return 如果存在返回true，如果不存在返回false
     */
    boolean CheckDepartmentInfo(Employee employee);
}
