# 一个javaweb项目-人事工资管理

## 工具类

### 数据库相关

1.连接数据库的配置
在项目Module下创建resources文件并设置为resource文件夹

```java
driverClassName=com.mysql.cj.jdbc.Driver
url=jdbc:mysql:///emp_ms
username=root
password=123456
initialSize=5
maxActive=10
maxWait=1000
```

2.数据库工具类-JDBCUtil.java

```java
import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCUtil {

    /*
     * 创建连接池引用，当前连接池供全局使用
     * threadLocal 用来保存线程中的共享变量，每一个线程对象中都有一个ThreadLocalMap<ThreadLocal,Object>d对象,
     * Object就是共享变量，而ThreadLocal作为key存储
     */
    private static DataSource dataSource;
    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    static {
        try {
            // 读取配置文件
            Properties properties = new Properties();
            // 获取一个读取信息的流
            InputStream inputStream = JDBCUtil.class.getClassLoader().getResourceAsStream("db.properties");
            // 加载配置信息
            properties.load(inputStream);
            // 创建连接池
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取连接池对象
     * @return 返回一个连接池对象
     */
    public static DataSource getDataSource(){
        return dataSource;
    }

    public static Connection getConnection(){
        Connection connection = null;
        try {
            connection = threadLocal.get();
            if(connection == null) {
                connection = dataSource.getConnection();
                threadLocal.set(connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void releaseConnection(){
        try {
            Connection connection = threadLocal.get();
            if(connection!=null){
                threadLocal.remove();
                connection.setAutoCommit(true);
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

```

### 读写请求中的json串

WebUtil.java

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class WebUtil {
    static ObjectMapper objectMapper;
    static {
        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public static <T> T readJson(HttpServletRequest request, Class<T> clazz){
        T t = null;
        try {
            BufferedReader br = request.getReader();
            StringBuilder buffer = new StringBuilder();
            String line=null;
            while ((line = br.readLine()) != null){
                buffer.append(line);
            }
            t = objectMapper.readValue(buffer.toString(),clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static void writeJson(HttpServletResponse response, Result result){
        response.setContentType("application/json;charset=utf-8");
        try {
            String info = objectMapper.writeValueAsString(result);
            response.getWriter().write(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### token 创建

为用户创建token，用户每次请求都携带该头，服务端根据token获得用户登录信息-JWHelper.java

```java
import com.alibaba.druid.util.StringUtils;
import io.jsonwebtoken.*;
import java.util.Date;
public class JwHelper {
    private static long tokenExpiration = 24*60*60*1000;
    private static String tokenSignKey = "123456";

    public static String createToken(Long userId){
        String token = Jwts.builder()
                .setSubject("YYGH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("userId", userId)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    public  static Long getUserId(String token){
        if(StringUtils.isEmpty(token)) return null;
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        Integer userId = (Integer) claims.get("userId");
        return userId.longValue();
    }

    public static boolean isExpiration(String token){
        try {
            boolean isExpire = Jwts.parser()
                    .setSigningKey(tokenSignKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration().before(new Date());
            return isExpire;
        }catch (Exception e){
            // 过期出现异常 返回true
            return true;
        }
    }
}

```

### 密码加密工具类

加密方式：MD5

```java
package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    public static String encrypt(String strSrc){
        try {
            char[] hexChars ={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
            byte[] bytes = strSrc.getBytes();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(bytes);
            bytes = md5.digest();
            int j = bytes.length;
            char[] chars = new char[j*2];
            int k =0;
            for (int i = 0; i < bytes.length; i++) {
                byte b = bytes[i];
                chars[k++] = hexChars[b >>> 4 & 0xf];
                chars[k++] = hexChars[b & 0xf];
            }
            return new String(chars);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("MD5 occur error!");
        }
    }
}

```

## 结果处理类

### 结果枚举类

通过服务层业务处理状态，响应不同的代码给客户端，处理状态可以预先写为枚举类
ResultEnum

```java
/**
 * 定义返回结果的code和message的枚举类
 */
public enum ResultEnum {
    // 在枚举类的开头声明多个对象，对象之间使用逗号隔开
    SUCCESS(200,"success"),
    INFOERROR(501,"usernameOrPasswordError"),
    NOTLOGIN(502,"notLogin"),
    USERNAMEUSED(503,"usernameUsed"),
    ;
    // 声明当前类的实例变量，因为是枚举类，值不会改变，用private final 修饰
    private final Integer code;
    private final String message;

    // 私有化构造器
    private ResultEnum(Integer code,String message){
        this.code = code;
        this.message = message;
    }
    // 在枚举类中不提供setter方法
    public Integer getCode(){
        return code;
    }
    public String getMessage(){
        return message;
    }
    @Override
    public String toString() {
        return "ResultEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}

```

### 结果类

定义一个结果类，定义返回给客户端的数据格式
code：业务处理状态码，message：状态码描述，data：返回给客户端的数据

```java
import lombok.Data;

@Data
public class Result<T> {
    // 泛型类/接口的定义格式 class classname/interfacename<T,...E>{}
    // 泛型方法的定义格式 权限修饰符 <T> 返回值类型 方法名(形参列表){}
    private Integer code;
    private String message;
    private T data;

    // 空参构造器
    public Result(){}

    /**
     * 根据传进来的数据构造一个result，没有提供code和message，主要用于在包装data
     * @param data  需要相应给客户端的数据
     * @param <T>   一般以map的形式传入
     * @return  返回一个只包含了data的结果
     */
    protected static <T> Result<T> build(T data){
        Result<T> result = new Result<>();
        if(data!=null){
            result.setData(data);
        }
        return result;
    }

    /**
     * 调用包装好的result根据传进来的code和message进行赋值
     * @param data  需要响应给客户端的数据
     * @param code  响应给客户端的编码
     * @param message   响应给客户端的信息
     * @param <T> 一般以map的形式传入
     * @return 返回一个完整的结果集
     */
    public static <T> Result<T> build(T data,Integer code, String message){
        Result<T> result = build(data);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 根据ResultEnum设置结果的code和message
     * @param data  需要响应给客户端的数据
     * @param resultEnum 提前准备好的枚举类
     * @param <T> 一般以map的形式传入
     * @return  返回一个完整的结果集
     */
    public static  <T> Result<T> build(T data,ResultEnum resultEnum){
        Result<T> result = build(data);
        result.setCode(resultEnum.getCode());
        result.setMessage(resultEnum.getMessage());
        return result;
    }

    /**
     * 返回一个包含数据的成功状态的结果集
     * @param data  需要响应给客户端的数据
     * @param <T> 一般以map的形式传入
     * @return 返回一个完整的结果集
     */
    public static <T> Result<T> ok(T data){
        return build(data,ResultEnum.SUCCESS);
    }

    /**
     * 设置结果集的message
     * @param message message
     * @return  一个结果集
     */
    public Result<T> message(String message){
        this.setMessage(message);
        return this;
    }

    /**
     * 设置结果集的code
     * @param code code
     * @return 一个结果集
     */
    public Result<T> code(Integer code){
        this.setCode(code);
        return this;
    }
}

```

## 跨域过滤器

参考“浏览器同源禁止策略”，对该问题进行解决，在进行前后端分离项目时防止出现数据不安全的情况
解决方式：客户端发送预检请求，当服务端接收到预检请求时在响应头中设置允许访问的信息，如果不是预检请求就直接放行

```java
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebFilter("/*")
public class CrossFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        resp.setHeader("Access-Control-Allow-Origin","*");  // 允许跨域
        resp.setHeader("Access-Control-Allow-Methods","POST,GET,PUT,OPTIONS,DELETE,HEAD");// 允许跨域的方法
        resp.setHeader("Access-Control-Max-Age","3600");    // 间隔时间
        resp.setHeader("Access-Control-Allow-Headers","access-control-allow-origin,authority,content-type,version-info,X-Requested-With");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        if(!req.getMethod().equalsIgnoreCase("OPTIONS")){
            filterChain.doFilter(req,resp);
        }
    }
}
```

## 实体层-pojo（Entity层）

为数据库中的表创建相应的实体类，表的字段就是类的成员变量，在本次项目中为![image-20241120215506142](D:\study\java\javaWeb学习记录.assets\image-20241120215506142.png)

例如：department.java

```java
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class department implements Serializable {
    private Integer id;
    private String depName;
    private Integer managerId;
}
```

## Dao层

Data Access Object  ——  数据访问层
作用：封装对数据库的访问，对表进行增删改查的操作，不涉及业务逻辑，只按照某个条件获得指定数据的要求

### BASEDAO.java 

对数据库的基本操作

```java
import util.JDBCUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaseDao {

    /**
     * 基础的数据库增删改的方法
     * @param sql 需要查询的sql语句
     * @param params    传入的参数-> 可变数组
     * @return  返回受影响的行数
     */
    public static int executeUpdate(String sql,Object... params){
        int row = 0;
        PreparedStatement preparedStatement = null;
        Connection connection = JDBCUtil.getConnection();
        try {
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i+1,params[i]);
            }
            row = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if(preparedStatement != null)preparedStatement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                if(connection != null && connection.getAutoCommit()) JDBCUtil.releaseConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return row;
    }

    /**
     * 返回查询的结果集合
     * @param tClass 返回的数据类型
     * @param sql 查询语句
     * @param params 可变参数列表
     * @return  返回结果集合
     * @param <T>   返回的数据类型
     */
    public static <T> List<T> executeQuery(Class<T> tClass,String sql,Object... params){
        List<T> res = new ArrayList<>();
        Connection connection = JDBCUtil.getConnection();
        // 获得sql语句的执行对象
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i+1,params[i]);
            }
            // 获得sql语句的执行结果集
            resultSet = preparedStatement.executeQuery();
            // 得到结果集的结构信息
            ResultSetMetaData metaData = resultSet.getMetaData();
            // 遍历结果集并将其存入集合中
            while (resultSet.next()){
                T t = tClass.getDeclaredConstructor().newInstance();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    // 得到当前行的第i列的属性名和属性值
                    String nameFiled = metaData.getColumnLabel(i+1);
                    Object obj = resultSet.getObject(i+1);
                    // 通过反射找到该属性名,然后将其设置为可访问的，然后在将该值赋值给该属性
                    Field declaredField = tClass.getDeclaredField(nameFiled);
                    declaredField.setAccessible(true);
                    declaredField.set(t,obj);
                }
                res.add(t);
            }
        } catch (SQLException | NoSuchFieldException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }finally {
            // 关闭资源
            try {
                if(resultSet != null)resultSet.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                if(preparedStatement!=null)preparedStatement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                if(connection!=null && connection.getAutoCommit()) JDBCUtil.releaseConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return res;
    }

    /**
     * 查询结果为单行单列的情况的基础查询
     * @param tClass    返回结果的数据类型
     * @param sql   查询语句
     * @param params 查询参数
     * @return  返回一个对象
     * @param <T>   泛型
     */
    public static <T> T executeQueryObject(Class tClass,String sql,Object... params){
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        T t = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i+1,params[i]);
            }
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                t = (T) resultSet.getObject(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(resultSet!=null)resultSet.close();
                if(preparedStatement!=null)preparedStatement.close();
                if(connection.getAutoCommit())JDBCUtil.releaseConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return t;
    }
}

```

### Dao初始化

```java
public interface DepartmentDao {
}
public class DepartmentDaoImpl extends BaseDao implements DepartmentDao {
}

```

## Service层

业务逻辑层，处理逻辑上的业务，不考虑具体的实现。是对一个或多个DAO进行的再次封装，封装成一个服务，所以这里也就不会是一个原子操作了，需要事物控制。

### 初始化

```java
public interface DepartmentService {
}
public class DepartmentServiceImpl implements DepartmentService {
}
```

## Controller 层

Controler负责请求转发，接受页面过来的参数，传给Service处理，接到返回值，再传给页面。

### BaseControl.java

用于接收请求，并根据请求的路径找到对应的service方法

```java
public class BaseController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 根据uri找到方法名称
        String requestURI = req.getRequestURI();
        String[] split = requestURI.split("/");
        String methodName =  split[split.length-1];
        // 根据反射找到对应的方法
        Class<? extends BaseController> aClass = this.getClass();
        try {
            Method declaredMethod = aClass.getDeclaredMethod(methodName,req.getClass(),resp.getClass());
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(this,req,resp);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
```

### 其他实体类的controller

```java
public class DepartmentController extends BaseController{
}
```

## 业务实现

### 登录功能

客户端：选择身份类型，输入用户名和密码，点击登录向服务端发送请求
服务端处理请求，并根据工号查询该用户，如果信息无误，那么就返回用户的token，否则就返回信息错误的信息
前端代码：

```vue
<template>
  <div class="login-container">
    <el-form
      :model="loginEmpolyee"
      label-width="80px"
      class="login-form"
      ref="formRef"
      :rules="loginRules"
      >
      <h2>用户登录</h2>

      <el-form-item prop="status">
        <el-radio-group v-model="loginEmpolyee.status" >
          <el-radio value="employee">员工</el-radio>
          <el-radio value="department">部门主管</el-radio>
          <el-radio value="manager">系统管理员</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="工 号" prop="id">
        <el-input
          type="text"
          autocomplete="off"
          placeholder="请输入用户名"
          v-model="loginEmpolyee.id"
        ></el-input>
      </el-form-item>
      <el-form-item label="密 码" prop="passwd">
        <el-input
          type="password"
          autocomplete="off"
          placeholder="请输入密码"
          v-model="loginEmpolyee.passwd"
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click.native.prevent="login()">登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import {ref,reactive} from 'vue'
import request from '../../utils/request'
import { da, el, pa, tr } from 'element-plus/es/locale/index.mjs';
import { FormInstance } from 'element-plus';
import { useEmployeeInfoStore } from '../../stores/empInfo';
import router from "../../routers/router"

let loginEmpolyee = reactive(
  {
    id:"",
    passwd:"",
    status:""
  }
)
const employeeInfoStore = useEmployeeInfoStore()
let formRef = ref<FormInstance>()
let loading = ref(false)
const CheckUsername = (rule:any,value:any,callback:any)=>{
    if(value.length < 4){
      callback(new Error("工号长度不能小于2位"))
    }else{
      callback()
    }
}
const CheckPasswd = (rule:any,value:any,callback:any)=>{
  if(value.length < 4){
    callback(new Error("密码长度至少为4位"))
  }else if(value.length >12){
    callback(new Error("密码长度最多为12位数"))
  }else{
    callback()
  }
}
const CheckStatus = (rule:any,value:any,callback:any)=>{
    if(value == "") callback(new Error("请选择你要登录的账号"))
    else callback()
}

let loginRules = {
    id:[
      {required:true,trigger:"blur",validator: CheckUsername }
    ],
    passwd:[
      {required:true,trigger: 'blur', validator: CheckPasswd}
    ],
    status:[
      {required:true, validator: CheckStatus}
    ]
}

// 发送登录请求
const login = async function(){
  //  检查所有信息都已经填写完毕
    await formRef.value?.validate()
    loading.value = true;
    // 根据身份进行验证登录
    var path = ""
    // console.log(loginEmpolyee)
    if(loginEmpolyee.status == "employee")  path = "employee"
    else if(loginEmpolyee.status == "manager") path = "manage"
    else path = "department"
    employeeInfoStore.initEmployeeInfo
    console.log(employeeInfoStore.token)
    employeeInfoStore.initEmployeeInfo()
    console.log(employeeInfoStore.token)
    let data = await request.post(`${path}/login`,{"id":loginEmpolyee.id,"passwd":loginEmpolyee.passwd})
    // 登录成功，给当前用户的pinia数据赋值
    employeeInfoStore.setToken(data)
    router.push(path) // 路由跳转
  }
</script>
<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: top;
  height: 100vh;
}
.login-form {
  width: 400px;
  text-align: center;
}
</style>
```

服务端代码：
在本次项目为三类用户都实现了login接口
employee-普通用户

```java
Controller
 protected void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("employee 收到登录请求");
        // 通过请求得到传递过来的用户信息
        User employee = WebUtil.readJson(req, User.class);
        // 判断用户名和密码是否正确,如果正确就生成用户id的token
        boolean exist = employeeService.CheckEmployeeInfo(employee);
        Result result = Result.build(null, ResultEnum.INFOERROR);
        if(exist) {
            String token = JwHelper.createToken(Long.valueOf(employee.getId()));
            Map<String,Object> map = new HashMap<>();
            map.put("token",token);
            result = Result.ok(map);
        }
        WebUtil.writeJson(resp,result);
    }
EmployeeServiceImpl
    
    EmployeeDao employeeDao = new EmployeeDaoImpl();
    @Override
    public boolean CheckEmployeeInfo(User employee) {
        // 根据用户名查询用户是否存在
        Employee loginEmployee = employeeDao.FindEmployeeById(employee.getId());
        if(loginEmployee != null && loginEmployee.getPasswd().equals(employee.getPasswd())) return true;
        return false;
    }
EmployeeDaoImpl
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
```

department用户-部门主管

```java
DepartmentController
protected void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("department 收到登录请求");
        Employee employee = WebUtil.readJson(req, Employee.class);
        boolean exist = departmentService.CheckDepartmentInfo(employee);
        Result result = Result.build(null, ResultEnum.INFOERROR);
        if(exist) {
            String token = JwHelper.createToken(Long.valueOf(employee.getId()));
            Map<String,Object> map = new HashMap<>();
            map.put("token",token);
            result = Result.ok(map);
        }
        WebUtil.writeJson(resp,result);
    }

 DepartmentServiceImpl
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

DepartmentDaoImpl
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
```

Manager用户-系统用户

```java
ManagerController
protected void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("manager 收到登录请求");
        User manager = WebUtil.readJson(req, User.class);
        boolean exits = managerService.CheckManagerInfo(manager);
        Result result = null;
        if(exits){
            String token = JwHelper.createToken(Long.valueOf(manager.getId()));
            Map<String,Object> data = new HashMap<>();
            data.put("token",token);
            result = Result.ok(data);
        }else result = Result.build(null, ResultEnum.INFOERROR);
        WebUtil.writeJson(resp,result);
    }
   
ManagerServiceImpl
ManagerDao managerDao  = new ManagerDaoImpl();
@Override
public boolean CheckManagerInfo(User manager) {
    Manager managerById = managerDao.FindManagerById(manager.getId());
    if(managerById.getPasswd().equals(manager.getPasswd())) return true;
    return false;
}

ManagerDaoImpl
	@Override
    public Manager FindManagerById(Integer id) {
        String sql = "select id,name,passwd from manager where id=?";
        List<Manager> managers = executeQuery(Manager.class, sql, id);
        return managers.isEmpty()?null:managers.get(0);
    }
```



#### 跨域问题

1. 没有配置跨域过滤器：由于请求的ip和响应的ip不一致，服务器会拒绝响应回来的数据
2. 配置了跨域过滤器，第一次请求时能够拿到响应结果，第二次之后不行，这是因为在第一次登陆过后再次发起请求会携带token信息，而跨域的请求头中没有指明可以有该请求头会出现报错。【解决办法1：每次发出登录请求时清空当前localStorage的token信息；解决办法2：在服务端允许该请求头的存在】
   ![image-20241122195922966](D:\study\java\javaWeb学习记录.assets\image-20241122195922966.png)![image-20241122195946095](D:\study\java\javaWeb学习记录.assets\image-20241122195946095.png)

#### 错误

```javascript
util/request.js中
import axios from 'axios'
import { ElMessage } from 'element-plus';
import { useEmployeeInfoStore } from '../stores/empInfo'
import pinia from '../stores/index';
import NProgress from "nprogress";
import "nprogress/nprogress.css";

const service = axios.create({
    baseURL:"http://localhost:8080:/",
    timeout:50000,
});
// 添加请求拦截器
service.interceptors.request.use(
  (config) => {
    NProgress.start()
    const useEmployeeInfoStore = useEmployeeInfoStore(pinia)
    let token = employeeInfoStore.token
    console.log(token)
    if(token) {
        (config.headers)['token'] = token
    }
    return config;
  }
);
service.interceptors.response.use(
    (response) => {
    NProgress.done()//关闭进度条
      if(response.data.code !== 200){
      // 判断响应状态码
      if (response.data.code == 501)  return  Promise.reject(ElMessage.error("用户名或密码或者身份有误"))
      else if (response.data.code == 502) return  Promise.reject(ElMessage.error("登录已过期"))
      else if (response.data.code == 503) return  Promise.reject(ElMessage.error("用户名占用"))
      } else {
        return response.data.data; /* 返回成功响应数据中的data属性数据 */
      }
    },
    (error) => {
    NProgress.done()//关闭进度条
      return Promise.reject(error.message);
    }
  );
```

发送请求出现错误：

![image-20241122163318932](D:\study\java\javaWeb学习记录.assets\image-20241122163318932.png)

**错误原因**：
`useEmployeeInfoStore` 的定义和初始化顺序有问题。你在定义 `useEmployeeInfoStore` 的同时又调用了它，导致了循环引用问题。
在文件顶部引入了 useEmployeeInfoStore ，然后又试图重新定义一个变量与它同名，这导致名称冲突和初始化问题。

#### 乱码问题

**tomcat 乱码**：tomcat日志中的字符编码默认使用的是UTF-8，但是window系统一般使用的是GBK编码，输出日志时会出现乱码
**sout乱码**：在idea设置编码格式为UTF-8，会才采用UTF-8格式进行编码，但是在运行字节码时，jdk加载类时用的可能不是UTF-8字符集，可以在tomcat启动设置中设置vm option = -Dfile.encoding=UTF-8来解决

### 普通用户功能实现

#### 获取默认信息

客服端向服务端发送get请求，token携带在请求头中，客户端收到请求从请求头中获取token，并进行解析获得用户id，获得用户的基本信息返回给客户端
URI

```http
employee/getDefaultInfo
```

请求方式

```http
get
```

响应示例

- 响应成功

  ```json
  {
      "code":"200",         // 成功状态码 
   	"message":"success"   // 成功状态描述
   	"data":{
  		"id":"",
  		"name":"",
  		......
  	}
  }
  ```

- 响应失败

  ```json
  {
      "code":"502",         // 成功状态码 
   	"message":"NotLogin"   // 成功状态描述
   	"data":null
  }
  ```

前端代码

```vue
<script>
onMounted(() => {
getDetailInfomation()
})

async function getDetailInfomation(){
  // console.log(employeeInfoStore.token);
  // 将从后端获得的数据分别存储在pinia数据和临时响应式数据中
  let result = await request.get("employee/getDefaultInfo")
  employeeInfoStore = result.employee
  form = result.employee
}
</script>
```

后端代码

```java
Controller
protected void getDefaultInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getHeader("token");
        Result result = null;
        // 如果token为空或者token已经过期，返回没有登录
        // 否则的话从token中获取信息
        if (token.isEmpty() ||JwHelper.isExpiration(token)) {
            result = Result.build(null,ResultEnum.NOTLOGIN);
        }else{
            int id = JwHelper.getUserId(token).intValue();
            EmployeeInfoVo employee = employeeService.FindEmployeeById(id);
            if(employee!=null) result = Result.ok(employee);
        }
        WebUtil.writeJson(resp,result);
    }
    
Service
    public EmployeeInfoVo FindEmployeeById(int id) {
        EmployeeInfoVo employeeInfoVo = employeeDao.FindEmpolyeeAllInfoById(id);
        if(employeeInfoVo!=null) employeeInfoVo.setPasswd("");
        return employeeInfoVo;
    }
    
Dao
@Override
    public EmployeeInfoVo FindEmpolyeeAllInfoById(int id) {
        String sql= """
                SELECT
                  employee.id,name,passwd,sex,birth,address,address,cardId,tel,dep_name depName,job_name jobName
                FROM
                  employee,department,job
                WHERE
                  employee.dep_id = department.id AND employee.job_id = job.id AND employee.id=?
                """;
        List<EmployeeInfoVo> employeeInfoVos = executeQuery(EmployeeInfoVo.class, sql, id);
        return employeeInfoVos.isEmpty()?null:employeeInfoVos.get(0);
    }

```

##### 错误提示

- 在token不为空的情况下，一直提示登录已过期（token）不正确。
  错误原因：在使用localstorage存储token时使用JSON.stringfy(token)会在最外层多一对引号，服务端在解析时一直不正确。
  修改：不适用该方法，**由于插件原因一直提示不能写result.token**，更换插件即可

  ```ts
  const TokenKey = 'emp_token'
  
  export function getToken() {
    return localStorage.getItem(TokenKey)
  }
  
  export function setToken(token: string) {
    localStorage.setItem(TokenKey, token)
  }
  
  export function removeToken() {
    localStorage.removeItem(TokenKey)
  }
  
  ```

  ```js
  const login = async function(){
    //  检查所有信息都已经填写完毕
      await formRef.value?.validate()
      loading.value = true;
  
      // 根据身份进行验证登录
      var path = ""
      // console.log(loginEmpolyee)
      if(loginEmpolyee.status == "employee")  path = "employee"
      else if(loginEmpolyee.status == "manager") path = "manage"
      else path = "department"
      employeeInfoStore.initEmployeeInfo
      console.log(employeeInfoStore.token)
      employeeInfoStore.initEmployeeInfo()
      console.log(employeeInfoStore.token)
      let result = await request.post(`${path}/login`,{"id":loginEmpolyee.id,"passwd":loginEmpolyee.passwd})
      // 登录成功，给当前用户的pinia数据赋值
      // console.log(result)
      employeeInfoStore.initEmployeeInfo()
      employeeInfoStore.setToken(result.token)
      // console.log(employeeInfoStore.token)
      // 路由跳转
      router.push(path)
    
    }
  ```

#### 修改信息

客户端点击修改信息向服务端发起请求，服务端修改信息后存入数据库，客户端刷新页面
URI

```http
employee/amendInfo
```

请求方式

```http
POST
```

响应示例

- 响应成功

  ```json
  {
      "code":"200",         // 成功状态码 
   	"message":"success"   // 成功状态描述
   	"data":{
     	"employee":{"id",.......}
  }
  }
  ```

- 响应失败

  ```json
  {
      "code":"502",         // 成功状态码 
   	"message":"NotLogin"   // 成功状态描述
   	"data":null
  }
  ```

前端代码

```vue
<script>
const compare = ()=>{
  // 比较数据是否改变
  for (const key in form) {
    if(form[key] !== employeeInfoStore[key]) return true
  }
  return false
}
function alter() {
  // 检查表单是否有修改
  if(!compare()){
    console.log(compare())
    alert("没有修改信息");
    return
  }else{
    // 如果有修改数据，发送请求更新数据库
    request.post("employee/amendInfo", form)
    .then(response => {
      alert("信息修改成功！");
    })
    .catch(error => {
      alert("修改失败，请稍后再试");
    });
  }
}
</script>
```

后端代码

```java
Controller
protected void amendInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    EmployeeInfoVo employeeInfoVo = WebUtil.readJson(req, EmployeeInfoVo.class);// 传递过来的要修改的员工的数据
    int row = employeeService.AlterInfoOfEmplpoyee(employeeInfoVo);
    Result<Object> result = null;
    if(row <= 0) {
        result = Result.build(null,ResultEnum.NOTLOGIN);
    }else {
        result = Result.ok(null);
    }
    WebUtil.writeJson(resp,result);
}

Service
public int AlterInfoOfEmplpoyee(EmployeeInfoVo employeeInfoVo) {
    return employeeDao.AlterInfoAll(employeeInfoVo);
}

Dao
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
```

##### 问题1：日期格式问题

**问题描述**：从客户端传来的数据中含有日期格式（"2002-05-08T16:00:00.000Z"），导致服务端在对该日期进行解析时无法对应日期类导致java.text.ParseException: Unparseable date: "2002-05-08T16:00:00.000Z"错误。

**解决方法**：

- 在服务端对该类型进行处理

  ```java
  for (int i = 0; i < split.length; i++) {
      if(split[i].contains("birth")){
          // 为了能够使用ObjectMapper进行存储对象，我们需要保留键值对的格式，所以首先将键和值分别存储起来
          String str = split[i].substring("birth".length()+3);
          String prefix = split[i].substring(0,"birth".length()+3);
          // "Z" 通常表示 UTC 时间（协调世界时），替换为 " UTC" 让其更具可读性，便于后续的时间处理
          str = str.replace("Z"," UTC");
          // 进行日期格式的解析
          Date parse = sdf.parse(str.substring(1,str.length()-1));
          // 将键和值连接起来
          split[i] = prefix.concat("\"".concat(sdf_new.format(parse))).concat("\"");
      }
      // 将所有对象连接起来
      buffer.append(split[i]).append(",");
  }
  ```

##### 前后端对象属性名不一致导致找不到该属性

#### 修改密码

客户端点击修改按钮，向服务端发送请求，如果原密码正确，修改成功，如果原密码不正确，修改失败

URI

```http
employee/alterPassword
```

请求方式

```http
POST
```

响应示例

- 响应成功

```json
{
    "code":"200",         // 成功状态码 
    "message":"success"   // 成功状态描述
    "data":null
}
```

- 响应失败

```json
{
    "code":"502",         // 成功状态码 
 	"message":"NotLogin"   // 成功状态描述
 	"data":null
}
```

前端代码

```vue
<template>
  <div id="content" class="content" style="text-align: -webkit-center"> 
    <el-form  label-width="auto" style="max-width: 600px">
      <el-form-item label=" ">
        <el-button class="title" key="primary" type="primary" text bg @click="password = true" > 修改密码 </el-button>
        <el-button class="title" key="success" type="success" text bg @click="salary = true"> 查询薪资 </el-button><br>
    </el-form-item>

    <el-drawer v-model="password" :with-header="false" direction="btt" size="50%">
      <div class="ps-title">修改密码</div>
      <el-form :model="passwordForm" 
                ref="formRef" 
                style="max-width: 600px"  
                :rules="alterRules"
              >
        <el-form-item label="原密码&nbsp;&nbsp;&nbsp;" prop="originPasswd">
          <el-input v-model="passwordForm.originPasswd" autocomplete="off" type="password"></el-input>
        </el-form-item>
        <el-form-item label="新密码&nbsp;&nbsp;&nbsp;" prop="newPasswd">
          <el-input v-model="passwordForm.newPasswd" autocomplete="off" type="password"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPasswd">
          <el-input v-model="passwordForm.confirmPasswd" autocomplete="off" type="password"></el-input>
        </el-form-item>
        <el-button type="info"  @click="cancelalterPasswd()">取 消</el-button>
        <el-button type="primary" @click="alterPassword()">确 认</el-button>
      </el-form>
    </el-drawer>

    <el-drawer v-model="salary" :with-header="false" direction="btt" size="50%">
      <div class="ps-title">查看薪资</div>
      你的当前薪资为：{{  }}
    </el-drawer>

    <el-form-item label="姓名">
      <el-input v-model="form.name"/>
    </el-form-item>

    <el-form-item label="职位">
      <el-button type="primary" plain>{{ form.jobName }}</el-button>
      <el-button type="primary" plain>{{ form.depName }}</el-button>
    </el-form-item>

    <el-form-item label="性别">
      <el-radio-group v-model="form.sex">
      <el-radio value="男" size="large">男</el-radio>
      <el-radio value="女" size="large">女</el-radio>
      </el-radio-group>
    </el-form-item>

    <el-form-item label="联系方式">
      <el-input v-model="form.tel"/>
    </el-form-item>

    <el-form-item label="出生日期">
      <el-col>
        <el-date-picker v-model="form.birth" type="date" placeholder="Pick a date" style="width: 100%"/>
      </el-col>
    </el-form-item>

    <el-form-item label="身份证号">
      <el-input v-model="form.cardId"/>
    </el-form-item>

    <el-form-item label="联系地址">
      <el-input v-model="form.address"/>
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="alter()">修改信息</el-button>
    </el-form-item>
  </el-form>
  </div>
</template>

<style scoped>
.content {
  color: rgb(90, 86, 86);
}
.el-input{
  /* border:1px solid salmon */
  background-color: gray
}
/* .content {
  text-align: center;
  width: 700px;
} */
.title {
  margin-bottom: 10px;
}
.ps-title{
  margin-bottom: 10px;
}
</style>
<script lang="ts" setup>
import { ref,reactive,onMounted,watch } from 'vue'
import {useEmployeeInfoStore} from '../../stores/empInfo'
import request  from "../../utils/request"
import { ElMessage,FormInstance } from 'element-plus'
import router from "../../routers/router"
let employeeInfoStore = useEmployeeInfoStore()
// do not use same name with ref
let form = reactive({
  id:"",
  name:'',
  sex:'',
  rate:'',
  birth:'',
  depName:'',
  jobName:'',
  tel:"",
  cardId:"",
  address:""
})
let passwordForm = reactive({
  id:"",
  originPasswd:"" ,
  newPasswd:"",
  confirmPasswd:""
})
let password = ref(false)
let salary = ref(false)
const formRef = ref<FormInstance>()
const loading = ref(false)
// 页面挂载完毕后向服务端发送请求获取当前用户的基本信息
// 组件挂载的生命周期钩子
onMounted(() => {
  getDetailInfomation()
});
async function getDetailInfomation(){
  // console.log(employeeInfoStore.token);
  // 将从后端获得的数据分别存储在pinia数据和临时响应式数据中
  let result = await request.get("employee/getDefaultInfo")
  for(const key in result.employee){
    employeeInfoStore[key] = result.employee[key]
    form[key] = result.employee[key]
  }
}
const compare = ()=>{
  // 比较数据是否改变
  for (const key in form) {
    if(form[key] !== employeeInfoStore[key]) return true
  }
  return false
}
function alter() {
  // 检查表单是否有修改
  if(!compare()){
    console.log(compare())
    ElMessage.error("没有修改信息");
    return
  }else{
    // 如果有修改数据，发送请求更新数据库
    request.post("employee/amendInfo", form)
    .then(response => {
      ElMessage.success("信息修改成功！");
    })
    .catch(error => {
      ElMessage.error("修改失败，请稍后再试");
    });
  }
}
const CheckPasswd = (rule:any,value:any,callback:any)=>{
  if(value.length < 4){
    callback(new Error("密码长度至少为4位"))
  }else if(value.length > 12){
    callback(new Error("密码长度最多为12位数"))
  }else{
    callback()
  }
}
const CheckNewAndConPassword = (rule:any,value:any,callback:any)=>{
  if(value != passwordForm.newPasswd){
    callback(new Error("新密码与确认密码一致!"))
  }else {
    callback()
  }
}
let alterRules = {
  originPasswd:[
      {required:true,trigger: 'blur', validator: CheckPasswd}
    ],
  newPasswd:[
    {required:true,trigger: 'blur', validator: CheckPasswd}
  ],
  confirmPasswd:[
      {required:true,trigger: 'blur', validator: CheckNewAndConPassword}
  ],
}
async function alterPassword(){
  await formRef.value?.validate()
  loading.value = true
  passwordForm.id = employeeInfoStore.id
  // 发送请求更改密码
  // 检查确认密码和新密码是否一致
  request.post("employee/alterPassword",{"id":passwordForm.id,"originPasswd":passwordForm.originPasswd,"newPasswd":passwordForm.newPasswd})
  .then(response => {
    router.push("login")
    ElMessage.success("修改成功请重新登录");
  })
}
function cancelalterPasswd(){
  password.value=false
  passwordForm.originPasswd = ""
  passwordForm.newPasswd = ""
  passwordForm.confirmPasswd=""
}
</script>
```

后端代码

```java
Controller
protected void alterPassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Uservo uservo = WebUtil.readJson(req, Uservo.class);
    EmployeeInfoVo employeeInfoVo = employeeService.FindEmployeeById(uservo.getId());
    Result<Object> result = null;
    if (employeeInfoVo.getPasswd().equals(uservo.getOriginPasswd())) {
        int row = employeeService.alterPassword(uservo);
        if(row > 0) result = Result.ok(null);
    }else {
        result = Result.build(null,ResultEnum.INFOERROR);
    }
    WebUtil.writeJson(resp,result);
}
Service
@Override
public int alterPassword(Uservo uservo) {
    return employeeDao.alterPasswordById(uservo);
}
Dao
@Override
public int alterPasswordById(Uservo uservo) {
    String sql = "UPDATE employee set passwd=? WHERE id=?";
    return executeUpdate(sql,uservo.getNewPasswd(),uservo.getId());
}
```

##### 问题1：表单绑定问题

**问题描述：**在使用element-plus组件中的表单的校验规则时，出现值无法绑定输入框的现象，无论在输入框中输入什么值，数据中都是undefined。<img src="D:\study\java\javaWeb学习记录.assets\74d0532853ffd9b1acbdf6ff2a8277f.png" alt="74d0532853ffd9b1acbdf6ff2a8277f" style="zoom:50%;" />
**解决方法：**在使用表单的校验规则时需要在el-form的标签中添加:model和:rule两个属性，其中model是绑定的响应式数据，rule是校验规则。绑定的响应式数据需要用一个对象来存储，对象中的属性名必须与el-input中的prop相同，否则会一直undefined。

##### **问题2：跳转问题**

**问题描述：**在处理完数据后对页面进行跳转，使用router.push()，但是在跳转时需要手动刷新页面才能完成跳转
**解决方法**：对login进行了重构【观察到token的存储存在问题】

```vue
pinia数据
export const useEmployeeInfoStore = defineStore("employeeInfo",
    {
        state:()=>({
            token:getToken(),
            id:'',
            name:'',
            sex:'',
            rate:'',
            birth:'',
            depName:'',
            jobName:'',
            tel:"",
            cardId:"",
            address:""
        }),

        actions:{
            initEmployeeInfo(){
                removeToken()
                this.id = ""
                this.name=""
                this.sex=""
                this.depName=""
                this.jobName="",
                this.tel=""
                this.birth=""
                this.cardId=""
                this.address=""
                this.rate=""
            },
            async login(path,data){
                const result = await request.post(`${path}/login`,data)
                const token = result.token
                this.token = token
                setToken(token)
            },
        },
    }
);
login
<script setup lang="ts">
import {ref,reactive} from 'vue'
import request from '../../utils/request'
import { da, el, pa, tr } from 'element-plus/es/locale/index.mjs';
import { FormInstance } from 'element-plus';
import { useEmployeeInfoStore } from '../../stores/empInfo';
import router from "../../routers/router"

let loginEmpolyee = reactive(
  {
    id:"",
    passwd:"",
    status:""
  }
)
const employeeInfoStore = useEmployeeInfoStore()
let formRef = ref<FormInstance>()
let loading = ref(false)
const CheckUsername = (rule:any,value:any,callback:any)=>{
    if(value.length < 4){
      callback(new Error("工号长度不能小于2位"))
    }else{
      callback()
    }
}
const CheckPasswd = (rule:any,value:any,callback:any)=>{
  if(value.length < 4){
    callback(new Error("密码长度至少为4位"))
  }else if(value.length >12){
    callback(new Error("密码长度最多为12位数"))
  }else{
    callback()
  }
}
const CheckStatus = (rule:any,value:any,callback:any)=>{
    if(value == "") callback(new Error("请选择你要登录的账号"))
    else callback()
}
let loginRules = {
    id:[
      {required:true,trigger:"blur",validator: CheckUsername }
    ],
    passwd:[
      {required:true,trigger: 'blur', validator: CheckPasswd}
    ],
    status:[
      {required:true, validator: CheckStatus}
    ]
}

// 发送登录请求
const login = async function(){
  //  检查所有信息都已经填写完毕
    await formRef.value?.validate()
    loading.value = true;

    // 根据身份进行验证登录
    var path = ""
    // console.log(loginEmpolyee)
    if(loginEmpolyee.status == "employee")  path = "employee"
    else if(loginEmpolyee.status == "manager") path = "manage"
    else path = "department"
    await employeeInfoStore.login(path,{"id":loginEmpolyee.id,"passwd":loginEmpolyee.passwd})
    // 登录成功，给当前用户的pinia数据赋值
    router.push(path)
  
  }
</script>
```

#### 查看薪资

URI

```http
employee/querySalary?id=${employeeInfoStore.id}
```

请求方式

```http
GET
```

响应示例

- 响应成功

  ```json
  {
      "code":"200",         // 成功状态码 
   	"message":"success"   // 成功状态描述
   	"data":{
  		"salary":"",
  	}
  }
  ```

- 响应失败

  ```json
  {
      "code":"502",         // 成功状态码 
   	"message":"NotLogin"   // 成功状态描述
   	"data":null
  }
  ```

前端代码

```vue
<script>
let salary_num = ref(0)
async function querySalary(){
  salary.value = true
  let data = await request.get(`employee/querySalary?id=${employeeInfoStore.id}`)
  salary_num.value = data.salary
}
</script>
```

后端代码

```java
Controller
protected void querySalary(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    int id = Integer.parseInt(req.getParameter("id"));
    int salary = employeeService.FindSalaryById(id);
    Map<String,Object> map = new HashMap<>();
    map.put("salary",salary);
    Result<Object> result = Result.ok(map);
    WebUtil.writeJson(resp,result);
}
Service
@Override
public int FindSalaryById(int id) {
	return employeeDao.findSalaryById(id);
}
Dao
@Override
public int findSalaryById(int id) {
    String sql = """
            SELECT salary
            from employee,job
            WHERE employee.job_id = job.id and employee.id=?
            """;
    return executeQueryObject(Integer.class,sql,id);
}
```

### 系统管理员页面功能实现

#### 加载页面后自动发送请求获得列表数据

URI

```http
manage/getDefault
```

请求方式

```http
Get
```

响应示例

- 响应成功

  ```json
  {
  "code":"200",         // 成功状态码 
  "message":"success"   // 成功状态描述
  "data":{
      "department":[{
      "id":"",
      "name":"",
      ......
      },{
      "id":"",
      "name":"",
      ......
      },
      ]
  }
  }
  ```

- 响应失败

  ```json
  {
  "code":"502",         // 成功状态码 
  "message":"NotLogin"   // 成功状态描述
  "data":null
  }
  ```

前端代码

```Vue
<script>
import { ref,reactive,onMounted,watch } from 'vue'
import request  from "../../utils/request"
import { Loading } from 'element-plus/es/components/loading/src/service.mjs';
let tableData = reactive(
  [
    {
      "id":"",
      "depName":"",
      "empId":"",
      "empName":""
    }
  ]
)
onMounted(
  ()=>{
    getDepartmentInfo()
  }
)
async function getDepartmentInfo(){
  let result = await request.get("manage/getDefault")
  for (const key in result.departments) {
    console.log(result.departments[key])
    tableData[key] = result.departments[key]
  }
}
</script>
```

后端代码

```java
Controller
protected void getDefault(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    List<DepartmentQueryVo> departmentQueryVos =  managerService.getDedault();
    Map<String,Object> map = new HashMap();
    map.put("departments",departmentQueryVos);
    Result<Map<String, Object>> result = Result.ok(map);
    WebUtil.writeJson(resp,result);
}

Service
@Override
public List<DepartmentQueryVo> getDedault() {
    return managerDao.getDefault();
}

Dao
@Override
public List<DepartmentQueryVo> getDefault() {
String sql = """
        SELECT department.id,department.dep_name depName,employee.id empId,employee.`name` empName
        FROM employee,department
        WHERE department.manager_id = employee.id
        """;
return executeQuery(DepartmentQueryVo.class,sql);
}
```

#### 获取管理员信息进行存储

URI

```http
manage/getManagerInfo
```

请求方式

```http
get
```

响应示例

- 响应成功

  ```json
  {
  "code":"200",         // 成功状态码 
  "message":"success"   // 成功状态描述
  "data":{
      "manager":{
          "id":,
          "name":"",
      }
  }
  }
  ```

- 响应失败

  ```json
  {
      "code":"502",         // 成功状态码 
   	"message":"NotLogin"   // 成功状态描述
   	"data":null
  }
  ```

前端代码

```vue
<script>
onMounted(
()=>{
getDepartmentInfo()
getManagerId()
}
)
async function getManagerId(){
  let data = await request.get("manage/getManagerInfo")
  employeeInfoStore.id = data.manager.id
  employeeInfoStorse.name = data.manager.name
  console.log(employeeInfoStore.id)
}
</script>
```

后端代码

```java
Controller
protected void getManagerInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // 根据token获取用户id查询用户信息
    String token = req.getHeader("token");
    Result<Object> result = null;
    if(token == null || JwHelper.isExpiration(token)){
        result = Result.build(null,ResultEnum.NOTLOGIN);
    }else {
        int id = JwHelper.getUserId(token).intValue();
        Manager manager = managerService.getManagerInfo(id);
        Map<String,Object> map = new HashMap<>();
        map.put("manager",manager);
        result =  Result.ok(map);
    }
    WebUtil.writeJson(resp,result);
}
Service
@Override
public Manager getManagerInfo(int id) {
    Manager manager = managerDao.findManagerById(id);
    manager.setPasswd("");
    return manager;
}
Dao
@Override
public Manager findManagerById(int id) {
    String sql = "SELECT id,name,passwd FROM manager WHERE id = ?";
    List<Manager> managers = executeQuery(Manager.class, sql, id);
    return managers.isEmpty()?null:managers.get(0);
}
```

#### 修改密码

URI

```http
manage/alterPassword
```

请求方式

```http
POST
```

响应示例

- 响应成功

  ```json
  {
      "code":"200",         // 成功状态码 
      "message":"success"   // 成功状态描述
      "data":null
  }
  ```

- 响应失败

  ```json
  {
      "code":"502",         // 成功状态码 
   	"message":"NotLogin"   // 成功状态描述
   	"data":null
  }
  ```

前端代码

```vue
<script>
let passwordForm = reactive({
id:"",
originPasswd:"" ,	
newPasswd:"",
confirmPasswd:""
})
function cancelalterPasswd(){
  password.value=false
  passwordForm.originPasswd = ""
  passwordForm.newPasswd = ""
  passwordForm.confirmPasswd=""
}
const CheckPasswd = (rule:any,value:any,callback:any)=>{
  if(value.length < 4){
    callback(new Error("密码长度至少为4位"))
  }else if(value.length > 12){
    callback(new Error("密码长度最多为12位数"))
  }else{
    callback()
  }
}
const CheckNewAndConPassword = (rule:any,value:any,callback:any)=>{
  if(value != passwordForm.newPasswd){
    callback(new Error("新密码与确认密码一致!"))
  }else {
    callback()
  }
}
let alterRules = {
  originPasswd:[
      {required:true,trigger: 'blur', validator: CheckPasswd }
    ],
  newPasswd:[
    {required:true,trigger: 'blur', validator: CheckPasswd }
  ],
  confirmPasswd:[
      {required:true,trigger: 'blur', validator: CheckNewAndConPassword }
  ],
}
async function alterPassword(){
  await formRef.value?.validate()
  passwordForm.id = employeeInfoStore.id
  // 发送请求更改密码
  // 检查确认密码和新密码是否一致
  request.post("manage/alterPassword",{"id":passwordForm.id,"originPasswd":passwordForm.originPasswd,"newPasswd":passwordForm.newPasswd})
  .then(response => {
    router.push("login")
    ElMessage.success("修改成功请重新登录");
  })
}
</script>
```

后端代码

```java
Controller
protected void alterPassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Uservo uservo = WebUtil.readJson(req, Uservo.class);
    Result<Object> result = null;
    int row = managerService.alterManagerPasswd(uservo);
    if(row>0){
        result = Result.ok(null);
    }
    else result = Result.build(null,ResultEnum.INFOERROR);
    WebUtil.writeJson(resp,result);
}
Service
@Override
public int alterManagerPasswd(Uservo uservo) {
    Manager managerById = managerDao.findManagerById(uservo.getId());
    if(!managerById.getPasswd().equals(uservo.getOriginPasswd())) return 0;
    return managerDao.alterPasswd(uservo);
}
Dao
@Override
public int alterPasswd(Uservo uservo) {
    String sql = "UPDATE manager SET passwd=? WHERE id=?";
    return executeUpdate(sql,uservo.getNewPasswd(),uservo.getId());
}
```

#### 备份数据库

客户端向服务端发送请求复制数据库
URI

```http
manage/backup
```

请求方式

```http
GET
```

响应示例

- 响应成功

  ```json
  {
      "code":"200",         // 成功状态码 
      "message":"success"   // 成功状态描述
      "data":null
  }
  ```

- 响应失败

  ```json
  {
      "code":"502",         // 成功状态码 
   	"message":"NotLogin"   // 成功状态描述
   	"data":null
  }
  ```

前端代码

```vue
<script>
async function dbcopy(){
  request.get("manage/backup")
  .then(response=>{
    ElMessage.success("复制成功")
  })
  .catch(error =>{
    ElMessage.error("复制失败，请稍后再试")
  })
}
</script>
```

后端代码

```java
Controller
 /**
 * 实现对数据库进行备份的操作
 * @param req  请求中不包含参数
 * @param resp  响应给客户端的数据
 * @throws ServletException
 * @throws IOException
 */
protected void backup(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    managerService.backup();
    Result<Object> result = Result.ok(null);
    WebUtil.writeJson(resp,result);
}
Service
@Override
public void backup() {
    managerDao.backup();
}
Dao
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
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
```

##### 知识点-数据库备份

1. mysql导出数据

   ```mysql
   SELECT id, name, email		
   INTO OUTFILE '/tmp/user_data.csv'	
   FIELDS TERMINATED BY ','	
   LINES TERMINATED BY '\n'	
   FROM users;
   ```

   into outfile – 指定导出的目录和文件名
   fields terminated by – 指定字段间分隔符，即定义字段间的分隔符
   optionally enclosed by – 指定字段包围符，即定义包围字段的字符，而参数optionally表示数值型字段无效，即数值类型的值不要加包围符
   lines terminated by – 指定行间分隔符，即定义每行的分隔符

   

2. mysqldump导出表作为原始数据

   ```cmd
   // 导出整个数据库
   mysqldump -u username -p password -h hostname database_name > output_file.sql
   // 只导出数据库结构不导出数据
   mysqldump -u username -p password -h hostname --no-data database_name > output_file.sql
   // 导出为压缩文件
   mysqldump -u username -p password -h hostname database_name | gzip > output_file.sql.gz
   ```

   **mysqldump** 是 mysql 用于转存储数据库的实用程序。

#### 当前部门人员列表数据获取

客户端点击更换主管按钮向服务端发送请求获取该部门的所有员工的数据
URI

```http
manage/allEmployeeOfDep?id=部门编号
```

请求方式

```http
GET
```

响应示例

- 响应成功

  ```json
  {
      "code":"200",         // 成功状态码 
      "message":"success"   // 成功状态描述
      "data":{
      "employee":[
      {"id":...},
  	{"id":...}
      ]
  }
  }
  ```

- 响应失败

  ```json
  {
      "code":"502",         // 成功状态码 
   	"message":"NotLogin"   // 成功状态描述
   	"data":null
  }
  ```

前端代码

```vue
<script>
import { ElMessage,FormInstance,ElTable  } from 'element-plus'
const singleTableRef = ref<InstanceType<typeof ElTable>>()
let employData = reactive(
[
{
  "id":"",
  "name":"",
  "jobName":""
}
]
)
async function changeDepManager(row){
  change.value = true;
  changeName.value = `更换${row.depName}主管信息`
  let data = await request.get(`manage/allEmployeeOfDep?id=${row.id}`)
  console.log(data.employee)
  for (const key in data.employee) {
    employData[key] = data.employee[key]
  }
}
const handleCurrentChange = (val) => {
    currentRow.value = val
    console.log(currentRow.value)
}
</script>
<template>
    <el-drawer
    v-model="change"
    :title="changeName"
    direction="rtl"
    size="40%"
  >
  <el-button type="primary" @click="confirmChange()">确认更换</el-button>
    <el-table
      ref="singleTableRef"
      :data="employData"
      :highlight-current-row="true"
      style="width: 100%"
      @current-change="handleCurrentChange"
    >
      <el-table-column type="index" width="50" />
      <el-table-column property="id" label="id" width="120" />
      <el-table-column property="name" label="name" width="120" />
      <el-table-column property="jobName" label="jobName" />
    </el-table>
</template>
```

后端代码

```java
Controller
/**
 * 客户端点击更换主管按钮向服务端发送请求获取该部门的所有员工的数据
 * @param req 请求中包含了要更换的部门的部门id
 * @param resp  响应中包含了该部门的所有人员信息
 * @throws ServletException
 * @throws IOException
 */
protected void allEmployeeOfDep(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    int id = Integer.parseInt(req.getParameter("id"));
    List<EmployeeInfoVo> employees  = managerService.getEmployeeOfDepId(id);
    Map<String,Object> map = new HashMap<>();
    map.put("employee",employees);
    Result<Map<String, Object>> result = Result.ok(map);
    WebUtil.writeJson(resp,result);
}
Service
@Override
public List<EmployeeInfoVo> getEmployeeOfDepId(int id) {
    return managerDao.findAllemployeeByDepId(id);
}
Dao
@Override
public List<EmployeeInfoVo> findAllemployeeByDepId(int id) {
    String sql = """
            SELECT employee.id,employee.name,passwd,sex,rate,birth,address,cardId,tel,dep_name depName,job_name jobName
            FROM employee,department,job
            WHERE employee.dep_id=? AND employee.dep_id = department.id AND employee.job_id = job.id
            """;
    return executeQuery(EmployeeInfoVo.class,sql,id);
}
```

#### 更换主管功能

客户端点击确认更换按钮向后端发起请求对换两个人的职位，工资

URI

```http
manage/changeManager
```

请求方式

```http
POST
```

响应示例

- 响应成功

  ```json
  {
      "code":"200",         // 成功状态码 
      "message":"success"   // 成功状态描述
      "data":{
      "employee":[
      {"id":...},
  	{"id":...}
      ]
  }
  }
  ```

- 响应失败

  ```json
  {
      "code":"502",         // 成功状态码 
   	"message":"NotLogin"   // 成功状态描述
   	"data":null
  }
  ```

前端代码

```vue
<script>
async function confirmChange(){
  //将选中的人的信息发送给服务端，交换服务端的信息
  console.log(currentRow.value)
  request.post("manage/changeManager",currentRow.value)
  .then (response => {
    change.value = false
    window.location.reload()
    ElMessage.success("更换成功")
  })
  .catch(error=>{
    ElMessage.error("更换失败，请稍后再试")
  })
}
</script>
```

后端代码

```java
Controller
/**
 * 实现更换部门主管的接口
 * @param req   请求中包含了新主管的信息
 * @param resp  修改成功响应状态200，登录状态失效响应状态502
 * @throws ServletException
 * @throws IOException
 */
protected void changeManager(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    EmployeeInfoVo employeeInfoVo = WebUtil.readJson(req,EmployeeInfoVo.class);
    managerService.changeManager(employeeInfoVo);
    Result<Object> result = Result.ok(null);
    WebUtil.writeJson(resp,result);
}
Service
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
Dao
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
public Job findJobByName(String jobName) {
    String sql = """
            SELECT id,job_name jobName,dep_id depId,salary
            FROM job
            WHERE job_name = ?
            """;
    List<Job> jobs = executeQuery(Job.class, sql, jobName);
    return jobs.isEmpty()?null:jobs.get(0);
}
@Override
public int changeJobById(Integer id, Integer jobId) {
    String sql = "UPDATE employee SET job_id = ? WHERE id = ?";
    return executeUpdate(sql,jobId,id);
}
@Override
public int changeManager(Integer id, Integer managerId) {
    String sql = "UPDATE department SET manager_id = ? WHERE id=?";
    return executeUpdate(sql,managerId,id);
}
```

### 部门主管功能实现

#### 登录后获取主管信息

URI

```http
department/getLogininfo
```

请求方式

```http
Get
```

响应示例

- 响应成功

  ```json
  {
  "code":"200",         // 成功状态码 
  "message":"success"   // 成功状态描述
  "data":{
      "department":
      {
      "id":"",
      "name":"",
      ......
      },
  	}
  }
  ```

- 响应失败

  ```json
  {
  "code":"502",         // 成功状态码 
  "message":"NotLogin"   // 成功状态描述
  "data":null
  }
  ```

前端代码

```vue
<script>
import { ref,reactive,onMounted,watch } from 'vue'
import request  from "../../utils/request"
import { Loading } from 'element-plus/es/components/loading/src/service.mjs';
import {useEmployeeInfoStore} from '../../stores/empInfo'
import { da, de } from 'element-plus/es/locale/index.mjs';
import router from "../../routers/router"
import { ElMessage,FormInstance,ElTable  } from 'element-plus'
const employeeInfoStore = useEmployeeInfoStore()
async function getLoginInfo(){
  let data = await request.get("department/getLoginInfo")
  for (const key in data.department) {
    employeeInfoStore[key] = data.department[key]
  }
}
</script>
```

后端代码

```java
Controller
protected void getLoginInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String token = req.getHeader("token");
    Result<Object> result = null;
    if(token==null || JwHelper.isExpiration(token)){
        result = Result.build(null,ResultEnum.NOTLOGIN);
    }else {
        int id = JwHelper.getUserId(token).intValue();
        EmployeeInfoVo employeeInfoVo = employeeService.FindEmployeeById(id);
        Map<String,Object> map = new HashMap<>();
        map.put("department",employeeInfoVo);
        result = Result.ok(map);
    }
    WebUtil.writeJson(resp,result);
}
```

#### 获取该部门的员工信息

URI

```http
department/getEmployeeList?id=xxx
```

请求方式

```http
Get
```

响应示例

- 响应成功

  ```json
  {
  "code":"200",         // 成功状态码 
  "message":"success"   // 成功状态描述
  "data":{
      "employees":[
          {
          "id":"",
          "name":"",
          ......
          },
      ]
  	}
  }
  ```

- 响应失败

  ```json
  {
  "code":"502",         // 成功状态码 
  "message":"NotLogin"   // 成功状态描述
  "data":null
  }
  ```

前端代码

```vue
<script>
const tableData = reactive(
[{
    "id":"",
    "name":"",
    "sex":"",
    "tel":"",
    "birth":"",
    "address":"",
    "jobName":"",
  }])
async function getEmployeeList() {
  let data = await request.get(`department/getEmployeeList?id=${employeeInfoStore.depId}`)
  for (const key in data.employees) {
      tableData[key] = data.employees[key]
  }
}
</script>
```

后端代码

```java
Controller
    /**
     * 获取指定部门的所有员工信息
     * @param req   请求中包含了参数：部门id
     * @param resp  响应该部门的所有人员信息
     * @throws ServletException
     * @throws IOException
     */
    protected void getEmployeeList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int depId = Integer.parseInt(req.getParameter("id"));
        List<EmployeeInfoVo> employeeInfoVoList = employeeService.getEmployeeListByDep(depId);
        Map<String,Object> map = new HashMap<>();
        map.put("employees",employeeInfoVoList);
        Result<Object> result = Result.ok(map);
        WebUtil.writeJson(resp,result);
    }
}
Service
@Override
public List<EmployeeInfoVo> getEmployeeListByDep(int depId) {
    return employeeDao.getEmployeeList(depId);
}
Dao
@Override
public List<EmployeeInfoVo> getEmployeeList(int depId) {
    String sql = """
            SELECT employee.id,employee.name,sex,tel,birth,address,job_name jobName
            FROM employee,job
            WHERE employee.dep_id=? AND employee.job_id = job.id
            """;
    return executeQuery(EmployeeInfoVo.class,sql,depId);
}
```

#### 查看个人的详细信息

URI

```http
department/getDetailInfo?id=
```

请求方式

```http
Get
```

响应示例

- 响应成功

  ```json
  {
  "code":"200",         // 成功状态码 
  "message":"success"   // 成功状态描述
  "data":{
      "employee":[
          {
          "id":"",
          "name":"",
          ......
          },
      ]
  	}
  }
  ```

- 响应失败

  ```json
  {
  "code":"502",         // 成功状态码 
  "message":"NotLogin"   // 成功状态描述
  "data":null
  }
  ```

前端代码

```vue
<tmplate>
<el-drawer
    v-model="detail"
    title="个人详细信息"
    direction="rtl"
    >
      <el-form  label-width="auto" style="max-width: 600px">
        <el-form-item label="姓名" >
        <el-input disabled v-model="detailForm.name"/>
      </el-form-item>

      <el-form-item label="职位">
        <el-button type="primary" plain>{{ detailForm.jobName }}</el-button>
        <el-button type="primary" plain>{{ detailForm.depName }}</el-button>
      </el-form-item>

      <el-form-item  label="性别">
        <el-radio-group v-model="detailForm.sex">
        <el-radio value="男"  size="large">男</el-radio>
        <el-radio value="女" size="large">女</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="联系方式">
        <el-input disabled v-model="detailForm.tel"/>
      </el-form-item>

      <el-form-item label="出生日期">
        <el-col>
          <el-date-picker disabled v-model="detailForm.birth" type="date" placeholder="Pick a date" style="width: 100%"/>
        </el-col>
      </el-form-item>

      <el-form-item label="身份证号">
        <el-input disabled v-model="detailForm.cardId"/>
      </el-form-item>

      <el-form-item label="联系地址">
        <el-input disabled v-model="detailForm.address"/>
      </el-form-item>
      </el-form>

    </el-drawer>
</tmplate>
<script>
let detail = ref(false)
async function getDetailInfo(row){
  detail.value = true
  let data = await request.get(`department/getDetailInfo?id=${row.id}`)
  for (const key in data.employee) {
      detailForm[key] = data.employee[key]
  }
}
</script>
```

后端代码

```java
protected void getDetailInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    int id = Integer.parseInt(req.getParameter("id"));
    EmployeeInfoVo employeeInfoVo = employeeService.FindEmployeeById(id);
    Map<String,Object> map = new HashMap<>();
    map.put("employee",employeeInfoVo);
    WebUtil.writeJson(resp,Result.ok(map));
}
```

#### 删除员工信息

URI

```http
department/delEmployee?id=
```

请求方式

```http
Get
```

响应示例

- 响应成功

  ```json
  {
  "code":"200",         // 成功状态码 
  "message":"success"   // 成功状态描述
  "data":null
  }
  ```

- 响应失败

  ```json
  {
  "code":"502",         // 成功状态码 
  "message":"NotLogin"   // 成功状态描述
  "data":null
  }
  ```

前端代码

```vue
<script>
async function delEmployee(row){
  ElMessageBox.confirm(
    '确定删除该员工？',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
  .then(()=>{
      request.get(`department/delEmployee?id=${row.id}`)
      .then( response=>{
        window.location.reload()
        ElMessage.success("删除成功")
      })
      .catch( error=>{
        ElMessage.error("删除失败请稍后再试")
      })
  })
}
</script>
```

后端代码

```java
Controller
/**
 *  根据指定id删除员工
 * @param req   请求中包含参数：员工id
 * @param resp  响应包含是否删除成功的状态
 * @throws ServletException
 * @throws IOException
 */
protected void delEmployee(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    int id = Integer.parseInt(req.getParameter("id"));
    int row = employeeService.delEmployeeById(id);
    Result<Object> result = null;
    if(row > 0) result = Result.ok(null);
    else result = Result.build(null,ResultEnum.NOTLOGIN);
    WebUtil.writeJson(resp,result);
}
Service
@Override
public int delEmployeeById(int id) {
    return  employeeDao.delEmployeeById(id);
}
Dao
@Override
public int delEmployeeById(int id) {
    String sql = "DELETE FROM employee WHERE id=?";
    return executeUpdate(sql,id);
}
```

#### 根据姓名查询员工

URI

```http
department/queryEmployee?name=
```

请求方式

```http
Get
```

响应示例

- 响应成功

  ```json
  {
  "code":"200",         // 成功状态码 
  "message":"success"   // 成功状态描述
  "data":{
      "employee":[
      {"id":"","name":"".... }
      ]
  }
  }
  ```

- 响应失败

  ```json
  {
  "code":"502",         // 成功状态码 
  "message":"NotLogin"   // 成功状态描述
  "data":null
  }
  ```

前端代码

```vue
<script>
let tableData = ref([])
async function queryEmployee(){
  let data = await request.get(`department/queryEmployee?name=${input.value}&depId=${employeeInfoStore.depId}`)
  tableData.value = data.employee
}
</script>
```

后端代码

```java
Controller
protected void queryEmployee(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String name = req.getParameter("name");
    int id = Integer.parseInt(req.getParameter("depId"));
    List<EmployeeInfoVo> employeeInfoVoList = employeeService.getEmployeeByName(name,id);
    Map<String,Object> map = new HashMap<>();
    map.put("employee",employeeInfoVoList);
    Result result = Result.ok(map);
    WebUtil.writeJson(resp,result);
}
Service
@Override
public List<EmployeeInfoVo> getEmployeeByName(String name,int id) {
    return  employeeDao.getEmployeeListByName(name,id);
}
Dao
@Override
public List<EmployeeInfoVo> getEmployeeListByName(String name,int id) {
    String sql = "SELECT employee.id,employee.name,sex,tel,birth,address,job_name jobName FROM employee,job WHERE employee.job_id = job.id and employee.dep_id=?";
    if(!name.equals("") && !name.equals(" ")){
        sql = sql.concat(" and employee.name = ?");
        return executeQuery(EmployeeInfoVo.class,sql,id,name);
    }
    return executeQuery(EmployeeInfoVo.class,sql,id);
}
```

#### 获取部门的工作类别

URI

```http
department/getJob?id=
```

请求方式

```http
Get
```

响应示例

- 响应成功

  ```json
  {
  "code":"200",         // 成功状态码 
  "message":"success"   // 成功状态描述
  "data":{
      "jobs":[
      {"id":"","jobName":"","salary":"" },
  	
      ]
  }
  }
  ```

- 响应失败

  ```json
  {
  "code":"502",         // 成功状态码 
  "message":"NotLogin"   // 成功状态描述
  "data":null
  }
  ```

前端代码

```vue
<script>
let add=ref(false)
let jobSelect = ref([])
async function getAddInfo(){
  add.value=true
  // 发送请求获取该部门的工作分类
  let data = await request.get(`department/getJob?id=${employeeInfoStore.depId}`)
  jobSelect.value = data.jobs
}
</script>
<template>
<el-drawer
    v-model="add"
    title="添加员工"
    direction="ltr"
    >
      <el-form  label-width="auto" style="max-width: 600px">
          <el-form-item label="姓名" >
          <el-input v-model="addForm.name" placeholder="输入姓名"/>
        </el-form-item>

        <el-form-item label="职位">
          <el-select v-model="addForm.jobId" placeholder="选择职位">
          <el-option v-for="(select,index) in jobSelect" :label=select.jobName  :value=select.id />
        </el-select>
        </el-form-item>

        <el-form-item  label="性别">
          <el-radio-group v-model="addForm.sex">
          <el-radio value="男" size="large">男</el-radio>
          <el-radio value="女" size="large">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="联系方式">
          <el-input v-model="addForm.tel" placeholder="输入联系方式"/>
        </el-form-item>

        <el-form-item label="出生日期">
          <el-col>
            <el-date-picker v-model="addForm.birth" type="date" placeholder="选择出生日期" style="width: 100%"/>
          </el-col>
        </el-form-item>

        <el-form-item label="身份证号">
          <el-input  v-model="addForm.cardId" placeholder="输入身份证号"/>
        </el-form-item>

        <el-form-item label="联系地址">
          <el-input v-model="addForm.address" placeholder="输入联系地址"/>
        </el-form-item>
        <el-button  type="primary" size="small" @click="addEmployee()">添加</el-button>
      </el-form>

    </el-drawer>
</template>
```

后端代码

```java
Controller
/**
 * 指定部门的工作名称
 * @param req   请求中包含了部门id
 * @param resp  响应该部门的工作分类
 * @throws ServletException
 * @throws IOException
 */
protected void getJob(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    int id = Integer.parseInt(req.getParameter("id"));
    List<Job> job = jobService.getJobByDepId(id);
    Map<String,Object> map = new HashMap<>();
    map.put("jobs",job);
    WebUtil.writeJson(resp,Result.ok(map));
}
Service
JobDao jobDao = new JobDaoImpl();
@Override
public List<Job> getJobByDepId(int id) {
    return jobDao.getJobByDepId(id);
}
Dao
@Override
public List<Job> getJobByDepId(int id) {
    String sql =  """
            SELECT id,job_name jobName
            FROM job
            WHERE dep_id = ?
            """;
    return executeQuery(Job.class,sql,id);
}
```

#### 添加员工信息

URI

```http
department/addEmployee
```

请求方式

```http
POST
```

响应示例

- 响应成功

  ```json
  {
  "code":"200",         // 成功状态码 
  "message":"success"   // 成功状态描述
  "data":null
  }
  ```

- 响应失败

  ```json
  {
  "code":"502",         // 成功状态码 
  "message":"NotLogin"   // 成功状态描述
  "data":null
  }
  ```

前端代码

```vue
<script>
async function addEmployee(){
  request.post(`department/addEmployee`,addForm)
  .then( response=>{
    ElMessage.success("添加成功")
    window.location.reload()
  })
  .catch(error=>{
    ElMessage.error("添加失败，稍后再试")
  })
}
</script>
```

后端代码

```java
Controller
 /**
 * 添加一个员工数据
 * @param req   请求中包含了一个员工的完整信息
 * @param resp  响应添加状态
 * @throws ServletException
 * @throws IOException
 */
protected void addEmployee(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Employee employee = WebUtil.readJson(req, Employee.class);
    employee.setPasswd("888888");
    int row = employeeService.addEmployee(employee);
    Result<Object> result = null;
    if(row >0) result = Result.ok(null);
    else  result = Result.build(null,ResultEnum.NOTLOGIN);
    WebUtil.writeJson(resp,result);
}
Service
@Override
public int addEmployee(Employee employee) {
    return  employeeDao.addEmployee(employee);
}
Dao
@Override
public int addEmployee(Employee employee) {
    String sql = "INSERT INTO employee VALUES (DEFAULT,?,?,?,0,?,?,?,?,?,?)";
    return executeUpdate(sql,employee.getName(),employee.getPasswd(),employee.getSex(),employee.getBirth(),employee.getAddress(),
            employee.getCardId(),employee.getTel(),employee.getDepId(),employee.getJobId());
}
```

#### 添加职位功能

点击添加职位按钮，填写表单，名称以及薪水，发送请求添加到数据库URI

```http
department/addJob
```

请求方式

```http
POST
```

响应示例

- 响应成功

  ```json
  {
  "code":"200",         // 成功状态码 
  "message":"success"   // 成功状态描述
  "data":null
  }
  ```

- 响应失败

  ```json
  {
  "code":"502",         // 成功状态码 
  "message":"NotLogin"   // 成功状态描述
  "data":null
  }
  ```

前端代码

```vue
<script>
let addJob = ref(false)
let addJobInfo = reactive({
  depId:employeeInfoStore.depId,
  jobName:"",
  salary:""
})
let addJobTitle = `${employeeInfoStore.depName}添加职位`
async function addJobRequest() {
  request.post("department/addJob",addJobInfo)
  .then(response=>{
    ElMessage.success("添加成功")
    window.location.reload()
  })
}
</script>
<template>
<el-drawer
v-model="addJob"
:title=addJobTitle
direction="ltr"
>
  <el-form  label-width="auto" style="max-width: 600px">
    <el-form-item label="工作名称" >
      <el-input v-model="addJobInfo.jobName" placeholder="输入工作名称"/>
    </el-form-item>

    <el-form-item label="薪资" >
      <el-input v-model="addJobInfo.salary" placeholder="输入薪资(必须是整数)"/>
    </el-form-item>
    <el-button  type="primary" size="small" @click="addJobRequest()">添加</el-button>
  </el-form>

</el-drawer>
</template>
```

后端代码

```java
Controller
  /**
 * 添加一个职位
 * @param req   请求中包含了部门id，职位名称，对应薪资
 * @param resp
 * @throws ServletException
 * @throws IOException
 */
protected void addJob(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Job job = WebUtil.readJson(req, Job.class);
    Result<Object> result = null;
    if(job.getJobName().equals("") || job.getJobName().equals(" ")  ||job.getSalary() == null) result = Result.build(null,ResultEnum.ABNORMALVALUE);
    else {
        int row = jobService.addJobByDepId(job);
        if(row>0) result = Result.ok(null);
        else result = Result.build(null,ResultEnum.NOTLOGIN);
    }
    WebUtil.writeJson(resp,result);
}
Service
@Override
public int addJobByDepId(Job job) {
    return jobDao.addJobbyDepId(job);
}
Dao
@Override
public int addEmployee(Employee employee) {
    String sql = "INSERT INTO employee VALUES (DEFAULT,?,?,?,0,?,?,?,?,?,?)";
    return executeUpdate(sql,employee.getName(),employee.getPasswd(),employee.getSex(),employee.getBirth(),employee.getAddress(),
            employee.getCardId(),employee.getTel(),employee.getDepId(),employee.getJobId());
}
```

#### 获取部门职位薪资信息

点击薪资管理按钮，请求该部门的职位-薪资信息

```http
department/getJobs?id=
```

请求方式

```http
get
```

响应示例

- 响应成功

  ```json
  {
  "code":"200",         // 成功状态码 
  "message":"success"   // 成功状态描述
  "data":{
      "jobs":[{"id":"","jobName":"","depId":"","salary":""}]
  }
  }
  ```

- 响应失败

  ```json
  {
  "code":"502"/"503",         // 成功状态码 
  "message":"NotLogin"/"AbnormalValue", // 成功状态描述
  "data":null
  }
  ```

前端代码

```vue
<template>

<el-drawer
v-model="salaryManageFlag"
title="薪资管理"
direction="ltr"
size="40%"
>
 <el-table :data="salaryData" stripe style="width: 100%">
        <el-table-column type="index" width="50" />
        <el-table-column label="工作名称">
          <template #default="scoped">
            <el-input v-model="scoped.row.jobName" placeholder="输入工作名称"/>
          </template>
        </el-table-column>
        <el-table-column label="薪资">
          <template #default="scoped">
            <el-input v-model="scoped.row.salary" placeholder="输入薪资(必须是整数)"/>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scoped">
            <el-button type="primary" size="small" @click="modifySalary(scoped.row)"> 更改 </el-button>
          </template>
        </el-table-column>
      </el-table>
</el-drawer>
</template>
<script>
let salaryManageFlag = ref(false)
let salaryData = ref([])
let salaryModify = ref()
async function getSalaryInfo(){
  salaryManageFlag.value = true
  let data = await request.get(`department/getJobs?id=${employeeInfoStore.depId}`)
  salaryData.value = data.jobs
}
</script>
```

后端代码

```java
Controller
/**
 * 获取部门的职位和薪资信息
 * @param req   请求中包含了部门id
 * @param resp  以json格式响应Job对象中的所有信息
 * @throws ServletException
 * @throws IOException
 */
protected void getJobs(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    int id = Integer.parseInt(req.getParameter("id"));
    Map<String,Object> map = new HashMap<>();
    List<Job> job = jobService.getAllJobInfo(id);
    map.put("jobs",job);
    WebUtil.writeJson(resp,Result.ok(map));
}
Service
@Override
public List<Job> getAllJobInfo(int id) {
    return jobDao.getJobInfoByDepId(id);
}
Dao
@Override
public List<Job> getJobInfoByDepId(int id) {
    String sql = "SELECT id,job_name jobName,salary FROM job WHERE dep_id=?";
    return executeQuery(Job.class,sql,id);
}
```

#### 薪资管理功能实现

点击更改按钮，更改对应的工作名称/薪资

```http
department/modifyJobs
```

请求方式

```http
POST
```

响应示例

- 响应成功

  ```json
  {
  "code":"200",         // 成功状态码 
  "message":"success"   // 成功状态描述
  "data":{
      "jobs":[{"id":"","jobName":"","depId":"","salary":""}]
  	}
  }
  ```

- 响应失败

  ```json
  {
  "code":"502"/"503",         // 成功状态码 
  "message":"NotLogin"/"AbnormalValue", // 成功状态描述
  "data":null
  }
  ```

前端代码

```vue
<script>
function modifySalary(row) {
  request.post("department/modifyJobs",row)
  .then(response=>{
    ElMessage.success("修改成功")
  })
}
</script>
```

后端代码

```java
Controller
/**
 *  更改工作名称/薪资
 * @param req   请求中包含了job id,jobName,salary
 * @param resp  修改成功响应200，否则返回502/503
 * @throws ServletException
 * @throws IOException
 */
protected void modifyJobs(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Job job = WebUtil.readJson(req, Job.class);
    String jobName = job.getJobName();
    Integer salary = job.getSalary();
    Result<Object> result = null;
    if(jobName.equals("")|| jobName.trim().equals("") || salary == null){
        result = Result.build(null,ResultEnum.ABNORMALVALUE);
    }else {
        int row = jobService.modifyJob(job);
        if(row >0) result = Result.ok(null);
        else result = Result.build(null,ResultEnum.NOTLOGIN);
    }
    WebUtil.writeJson(resp,result);
}
Service
@Override
public int modifyJob(Job job) {
    return jobDao.modifyJob(job);
}
Dao
@Override
public int modifyJob(Job job) {
    String sql = "UPDATE job SET job_name=?,salary=? WHERE id=?";
    return executeUpdate(sql,job.getJobName(),job.getSalary(),job.getId());
}
```

### 过滤器

#### LoginFilter

在通过url直接访问页面时，使用过滤器拦截没有登录过的操作

```java
@WebFilter(urlPatterns = {"/employee/*","/department/*","/manage/*"})
public class LoginFilter implements Filter {
    EmployeeDao employeeDao = new EmployeeDaoImpl();
    DepartmentDao departmentDao = new DepartmentDaoImpl();
    ManagerDao managerDao = new ManagerDaoImpl();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //在访问时获取token判断是否可以登录
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request  = (HttpServletRequest) servletRequest;
        String token = request.getHeader("token");
        if(token==null && JwHelper.isExpiration(token)){
            WebUtil.writeJson(response, Result.build(null, ResultEnum.NOTLOGIN));
        }else{
            // 根据身份判断
            int id = JwHelper.getUserId(token).intValue();
            String[] split = request.getRequestURI().split("/");
            String status = split[split.length-2];
            System.out.println(status);
            if(status.equals("employee")){
                Employee employee = employeeDao.FindEmployeeById(id);
                if(employee == null) WebUtil.writeJson(response,Result.build(null,ResultEnum.INCORRECTSTATUS));
                else filterChain.doFilter(request,response);
            }else if(status.equals("department")){
                Department department = departmentDao.FindDepartmentManagerById(id);
                if(department == null) WebUtil.writeJson(response,Result.build(null,ResultEnum.INCORRECTSTATUS));
                else filterChain.doFilter(request,response);
            } else {
                Manager managerById = managerDao.findManagerById(id);
                if(managerById==null) WebUtil.writeJson(response,Result.build(null,ResultEnum.INCORRECTSTATUS));
                else filterChain.doFilter(request,response);
            }

        }
    }
```

##### 注意点

**/* 这个url-pattern会包含 /**，因此不能直接采用/*作为过滤目标
urlPatterns = {"/employee/*","/department/*","/manage/*"} 是请求路径，不是页面的url
