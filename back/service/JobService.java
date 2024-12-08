package service;

import pojo.Job;

import java.util.List;

public interface JobService {
    /**
     * 根据部门id查找该部门的工作
     * @param id    部门id
     * @return  返回一个job对象组成的列表集合
     */
    List<Job> getJobByDepId(int id);

    /**
     * 添加一个职位
     * @param job   要添加的职位的信息
     * @return  返回收影响的行数
     */
    int addJobByDepId(Job job);

    /**
     * 获得指定部门的所有职位和薪资
     *
     * @param id 部门id
     * @return 返回job对象组成的列表集合
     */
    List<Job> getAllJobInfo(int id);

    /**
     *  更改工作名称/薪资
     * @param job   job id,jobName,salary
     * @return 返回受影响的行数
     */
    int modifyJob(Job job);
}
