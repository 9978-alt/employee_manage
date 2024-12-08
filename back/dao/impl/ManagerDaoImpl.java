package dao.impl;

import dao.BaseDao;
import dao.ManagerDao;
import pojo.Department;
import pojo.Employee;
import pojo.Manager;
import pojo.vo.DepartmentQueryVo;
import pojo.vo.EmployeeInfoVo;
import pojo.vo.Uservo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ManagerDaoImpl extends BaseDao implements ManagerDao {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    @Override
    public int changeManager(Integer id, Integer managerId) {
        String sql = "UPDATE department SET manager_id = ? WHERE id=?";
        return executeUpdate(sql,managerId,id);
    }

    @Override
    public Department findDepartmentByName(String depName) {
        String sql= """
                SELECT id,dep_name depName ,manager_id managerId
                FROM department
                WHERE dep_name = ?
                """;
        List<Department> departments = executeQuery(Department.class, sql, depName);
        return departments.isEmpty()?null:departments.get(0);
    }

    @Override
    public List<EmployeeInfoVo> findAllemployeeByDepId(int id) {
        String sql = """
                SELECT employee.id,employee.name,passwd,sex,rate,birth,address,cardId,tel,dep_name depName,job_name jobName
                FROM employee,department,job
                WHERE employee.dep_id=? AND employee.dep_id = department.id AND employee.job_id = job.id
                """;
        return executeQuery(EmployeeInfoVo.class,sql,id);
    }

    @Override
    public void backup() {
        String date = sdf.format(new Date());
        String pathSql = "D:/ex/" + date + ".sql";
        File empFile = new File(pathSql);
        if (!empFile.exists()) {
            try {
                empFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String root = "root", pwd = "123456", dbName = "emp_ms";
        String sql = "mysqldump -u" + root + " -p" + pwd + " " + dbName + " > " + pathSql;
        Runtime runtime = Runtime.getRuntime();
        try {
            Process exec = runtime.exec("cmd /c" + sql);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int alterPasswd(Uservo uservo) {
        String sql = "UPDATE manager SET passwd=? WHERE id=?";
        return executeUpdate(sql, uservo.getNewPasswd(), uservo.getId());
    }

    @Override
    public Manager findManagerById(int id) {
        String sql = "SELECT id,name,passwd FROM manager WHERE id = ?";
        List<Manager> managers = executeQuery(Manager.class, sql, id);
        return managers.isEmpty() ? null : managers.get(0);
    }

    @Override
    public List<DepartmentQueryVo> getDefault() {
        String sql = """
                SELECT department.id,department.dep_name depName,employee.id empId,employee.`name` empName
                FROM employee,department
                WHERE department.manager_id = employee.id
                """;
        return executeQuery(DepartmentQueryVo.class, sql);
    }

    @Override
    public Manager FindManagerById(Integer id) {
        String sql = "select id,name,passwd from manager where id=?";
        List<Manager> managers = executeQuery(Manager.class, sql, id);
        return managers.isEmpty() ? null : managers.get(0);
    }
}
