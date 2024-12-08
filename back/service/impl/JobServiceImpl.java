package service.impl;

import dao.JobDao;
import dao.impl.JobDaoImpl;
import pojo.Job;
import service.JobService;

import java.util.List;

public class JobServiceImpl implements JobService {
    JobDao jobDao = new JobDaoImpl();

    @Override
    public int modifyJob(Job job) {
        return jobDao.modifyJob(job);
    }

    @Override
    public List<Job> getAllJobInfo(int id) {
        return jobDao.getJobInfoByDepId(id);
    }

    @Override
    public int addJobByDepId(Job job) {
        return jobDao.addJobbyDepId(job);
    }

    @Override
    public List<Job> getJobByDepId(int id) {
        return jobDao.getJobByDepId(id);
    }
}
