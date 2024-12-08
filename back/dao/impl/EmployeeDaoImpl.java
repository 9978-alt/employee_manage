package dao.impl;

import dao.BaseDao;
import dao.EmployeeDao;
import pojo.Employee;
import pojo.vo.EmployeeInfoVo;
import pojo.vo.Uservo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class EmployeeDaoImpl extends BaseDao implements EmployeeDao {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public int addEmployee(Employee employee) {
        String sql = "INSERT INTO employee VALUES (DEFAULT,?,?,?,0,?,?,?,?,?,?)";
        return executeUpdate(sql,employee.getName(),employee.getPasswd(),employee.getSex(),employee.getBirth(),employee.getAddress(),
                employee.getCardId(),employee.getTel(),employee.getDepId(),employee.getJobId());
    }

    @Override
    public List<EmployeeInfoVo> getEmployeeListByName(String name,int id) {
        String sql = "SELECT employee.id,employee.name,sex,tel,birth,address,job_name jobName FROM employee,job WHERE employee.job_id = job.id and employee.dep_id=?";
        if(!name.equals("") && !name.equals(" ")){
            sql = sql.concat(" and employee.name = ?");
            return executeQuery(EmployeeInfoVo.class,sql,id,name);
        }
        return executeQuery(EmployeeInfoVo.class,sql,id);

    }

    @Override
    public int delEmployeeById(int id) {
        String sql = "DELETE FROM employee WHERE id=?";
        return executeUpdate(sql,id);
    }

    @Override
    public List<EmployeeInfoVo> getEmployeeList(int depId) {
        String sql = """
                SELECT employee.id,employee.name,sex,tel,birth,address,job_name jobName
                FROM employee,job
                WHERE employee.dep_id=? AND employee.job_id = job.id
                """;
        return executeQuery(EmployeeInfoVo.class,sql,depId);
    }

    @Override
    public int changeJobById(Integer id, Integer jobId) {
        String sql = "UPDATE employee SET job_id = ? WHERE id = ?";
        return executeUpdate(sql,jobId,id);
    }

    @Override
    public int findSalaryById(int id) {
        String sql = """
                SELECT salary
                from employee,job
                WHERE employee.job_id = job.id and employee.id=?
                """;
        return executeQueryObject(Integer.class,sql,id);
    }

    @Override
    public int alterPasswordById(Uservo uservo) {
        String sql = "UPDATE employee set passwd=? WHERE id=?";
        return executeUpdate(sql,uservo.getNewPasswd(),uservo.getId());
    }

    @Override
    public int AlterInfoAll(EmployeeInfoVo employeeInfoVo) {
        String sql = """
                UPDATE employee
                set name=?,sex=?,address=?,cardId=?,tel=?,birth=?
                WHERE id=?
                """;
        return executeUpdate(sql,employeeInfoVo.getName(),employeeInfoVo.getSex(),
                employeeInfoVo.getAddress(), employeeInfoVo.getCardId(),
                employeeInfoVo.getTel(),employeeInfoVo.getBirth(),
                employeeInfoVo.getId());
    }

    @Override
    public EmployeeInfoVo FindEmpolyeeAllInfoById(int id) {
        String sql= """
                SELECT
                  employee.id,name,passwd,rate,sex,birth,address,address,cardId,tel,dep_name depName,job_name jobName,employee.dep_id depId,employee.job_id jobId
                FROM
                  employee,department,job
                WHERE
                  employee.dep_id = department.id AND employee.job_id = job.id AND employee.id=?
                """;
        List<EmployeeInfoVo> employeeInfoVos = executeQuery(EmployeeInfoVo.class, sql, id);
        return employeeInfoVos.isEmpty()?null:employeeInfoVos.get(0);
    }

    @Override
    public Employee FindEmployeeById(Integer id) {
        String sql = """
                select
                    id,name,passwd,sex,rate,birth,address,address,cardId,tel,dep_id depId,job_id jobId
                from
                    employee
                where
                    id = ?;
                """;
        List<Employee> employees = executeQuery(Employee.class, sql, id);
        if(!employees.isEmpty()) return employees.get(0);
        return null;
    }
}
