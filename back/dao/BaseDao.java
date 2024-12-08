package dao;

import util.JDBCUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
                    if(obj.getClass().equals(LocalDateTime.class)){
                        System.out.println(obj);
                        obj = Timestamp.valueOf((LocalDateTime) obj);
                        System.out.println(obj);
                    }
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
