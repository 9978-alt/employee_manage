package service.impl;

import dao.EmployeeDao;
import dao.JobDao;
import dao.ManagerDao;
import dao.impl.EmployeeDaoImpl;
import dao.impl.JobDaoImpl;
import dao.impl.ManagerDaoImpl;
import pojo.*;
import pojo.vo.DepartmentQueryVo;
import pojo.vo.EmployeeInfoVo;
import pojo.vo.Uservo;
import service.ManagerService;

import java.util.List;

public class ManagerServiceImpl implements ManagerService {
    ManagerDao managerDao  = new ManagerDaoImpl();
    EmployeeDao employeeDao = new EmployeeDaoImpl();
    JobDao jobDao = new JobDaoImpl();

    @Override
    public void changeManager(EmployeeInfoVo employeeInfoVo) {
        // 首先找到该部门id和原来的部门主管
        Department department = managerDao.findDepartmentByName(employeeInfoVo.getDepName());
        //根据工作的名字找到对应的id
        Job job = jobDao.findJobByName(employeeInfoVo.getJobName());
        // 根据部门主管的id找到该员工
        Employee employee = employeeDao.FindEmployeeById(department.getManagerId());
        // 将新主管的job更换为主管
        int newM = employeeDao.changeJobById(employeeInfoVo.getId(),employee.getJobId());
        // 将原主观的job进行更换
        int oldM = employeeDao.changeJobById(employee.getId(),job.getId());

        //更新部门信息
        int dep = managerDao.changeManager(department.getId(),employeeInfoVo.getId());
    }

    @Override
    public List<EmployeeInfoVo> getEmployeeOfDepId(int id) {
        return managerDao.findAllemployeeByDepId(id);
    }

    @Override
    public void backup() {
        managerDao.backup();
    }

    @Override
    public int alterManagerPasswd(Uservo uservo) {
        Manager managerById = managerDao.findManagerById(uservo.getId());
        if(!managerById.getPasswd().equals(uservo.getOriginPasswd())) return 0;
        return managerDao.alterPasswd(uservo);
    }

    @Override
    public Manager getManagerInfo(int id) {
        return managerDao.findManagerById(id);
    }

    @Override
    public List<DepartmentQueryVo> getDedault() {
        return managerDao.getDefault();
    }

    @Override
    public boolean CheckManagerInfo(User manager) {
        Manager managerById = managerDao.findManagerById(manager.getId());
        if(managerById!=null && managerById.getPasswd().equals(manager.getPasswd())) return true;
        return false;
    }
}
