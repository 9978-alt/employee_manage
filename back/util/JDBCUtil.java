package util;

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
