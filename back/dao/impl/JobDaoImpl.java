package dao.impl;

import dao.BaseDao;
import dao.JobDao;
import pojo.Job;

import java.util.List;

public class JobDaoImpl extends BaseDao implements JobDao {
    @Override
    public int modifyJob(Job job) {
        String sql = "UPDATE job SET job_name=?,salary=? WHERE id=?";
        return executeUpdate(sql,job.getJobName(),job.getSalary(),job.getId());
    }

    @Override
    public List<Job> getJobInfoByDepId(int id) {
        String sql = "SELECT id,job_name jobName,salary FROM job WHERE dep_id=?";
        return executeQuery(Job.class,sql,id);
    }

    @Override
    public int addJobbyDepId(Job job) {
        String sql = "INSERT INTO job VALUES(DEFAULT,?,?,?)";
        return executeUpdate(sql,job.getJobName(),job.getDepId(),job.getSalary());
    }

    @Override
    public List<Job> getJobByDepId(int id) {
        String sql =  """
                SELECT id,job_name jobName
                FROM job
                WHERE dep_id = ?
                """;
        return executeQuery(Job.class,sql,id);
    }

    @Override
    public Job findJobByName(String jobName) {
        String sql = """
                SELECT id,job_name jobName,dep_id depId,salary
                FROM job
                WHERE job_name = ?
                """;
        List<Job> jobs = executeQuery(Job.class, sql, jobName);
        return jobs.isEmpty()?null:jobs.get(0);
    }
}
