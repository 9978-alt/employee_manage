package tests;

import org.junit.Test;
import util.JDBCUtil;

import java.sql.Connection;

public class JDBCTest {

    @Test
    public void testGetDataSource(){
        System.out.println(JDBCUtil.getDataSource());
    }

    @Test
    public void testGetConnection(){
        Connection connection = JDBCUtil.getConnection();
        System.out.println(connection);
        JDBCUtil.releaseConnection();
    }
}
