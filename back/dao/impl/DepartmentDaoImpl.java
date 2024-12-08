package dao.impl;

import dao.BaseDao;
import dao.DepartmentDao;
import pojo.Department;

import java.util.List;

public class DepartmentDaoImpl extends BaseDao implements DepartmentDao {
    @Override
    public Department FindDepartmentManagerById(Integer id) {
        String sql = """
                SELECT id,dep_name depName,manager_id managerId
                FROM department
                WHERE manager_id = ?
                """;
        List<Department> departments = executeQuery(Department.class, sql, id);
        return departments.isEmpty()?null:departments.get(0);
    }
}
