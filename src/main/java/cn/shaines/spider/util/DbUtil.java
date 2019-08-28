package cn.shaines.spider.util;

import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 需要依赖的jar
 *
 * // 添加 MySQL连接驱动 的依赖
 * compile('mysql:mysql-connector-java:6.0.5')
 * // 添加druid连接池
 * compile group: 'com.alibaba', name: 'druid', version: '1.1.10'
 *
 * @description: 数据库工具类
 * @ate: created in 2019-04-27 14:29:58
 * @auther: houyu
 */
public class DbUtil {


    /**
     * 执行查询,返回一个List<Map<String, Object>>
     *      List<Map<String, Object>> maps = executeQuery("SELECT * FROM blog WHERE id = ?", 1);
     */
    public static List<Map<String,Object>> executeQuery(String sql, Object... params) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<Map<String,Object>> mapList = new ArrayList();
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            if(params != null){
                for(int i = 0 ; i < params.length ; i++){
                    ps.setObject(i + 1, params[i]);
                }
            }
            rs = ps.executeQuery();
            //类ResultSet有getMetaData()会返回数据的列和对应的值的信息，然后我们将列名和对应的值作为map的键值存入map对象之中...
            ResultSetMetaData rsmd = rs.getMetaData();
            while(rs.next()){
                Map<String,Object> map = new LinkedHashMap<>();
                for(int i = 0 ; i < rsmd.getColumnCount(); i++){
                    String col_name = rsmd.getColumnName(i + 1);
                    Object col_value = rs.getObject(col_name);
                    if(col_value == null){
                        col_value = "";
                    }
                    map.put(col_name, col_value);
                }
                mapList.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            close(conn, ps, rs);
        }
        return mapList;
    }

    /**
     * 执行更新
     *      int num = executeUpdate("update blog set title=? where id = ?", "update_blog_title",1);
     */
    public static int executeUpdate(String sql, Object... params) {
        Connection connection = getConnection();
        PreparedStatement ps = null;
        int updateNum = 0;
        // 去除特殊字符,如表情等
        try {
            sql = sql.replaceAll("[\ud800\udc00-\udbff\udfff\ud800-\udfff]", "");
            ps = connection.prepareStatement(sql);
            if(params != null){
                for(int i = 0 ; i < params.length ; i++){
                    ps.setObject(i + 1, params[i]);
                }
            }
            updateNum = ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection, ps);
        }
        return updateNum;
    }

    /**
     * 创建表
     */
    public static boolean createTable(String tableName, String... columns){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE ").append(tableName).append(" ( ");
        for(String column : columns){
            stringBuilder.append(column.replaceAll("'", "''")).append(" VARCHAR(1024) ,");
        }
        stringBuilder.append(" )");
        executeUpdate(stringBuilder.toString().replace(", )", " )"));
        return true;
    }

    /**
     * 插入数据
     */
    public static boolean insertData(String tableName, Map<String, Object> dataMap){
        return insertData(tableName, Arrays.asList(dataMap));
    }

    /**
     * 插入数据
     */
    public static boolean insertData(String tableName, List<Map<String, Object>> dataList){
        if(dataList != null && dataList.size() > 0) {
            StringBuilder keyBuilder = new StringBuilder(128);
            Set<String> keySet = dataList.get(0).keySet();
            keyBuilder.append("(");
            for(String key : keySet) {
                if(key == null || key.isEmpty()) {
                    continue;
                }
                keyBuilder.append(key).append(", ");
            }
            keyBuilder = keyBuilder.delete(keyBuilder.length() - 2, keyBuilder.length());
            keyBuilder.append(")");
            //
            StringBuilder valBuilder = new StringBuilder(128 * dataList.size());
            for(Map<String, Object> map : dataList) {
                valBuilder.append("(");
                Collection<Object> values = map.values();
                Iterator<Object> iterator = values.iterator();
                while(iterator.hasNext()) {
                    Object next = iterator.next();
                    next = next == null ? "" : next.toString().replaceAll("'", "''");
                    valBuilder.append("'").append(next).append("'").append(", ");
                }
                valBuilder.delete(valBuilder.length() - 2, valBuilder.length());
                valBuilder.append("), ");
            }
            valBuilder.delete(valBuilder.length() - 2, valBuilder.length());
            //
            String sql = String.format("INSERT INTO `%s` %s VALUES %s;", tableName, keyBuilder.toString(), valBuilder.toString());
            int i = executeUpdate(sql);
            return i != 0;
        }
        return false;
    }

    /** --------------------------------------------------------------------------------------- */

    // 数据库连接池
    private static DruidDataSource dataSource;

    static {
        // 数据库名
        final String DATABASE_NAME = "spider";
        // 数据库驱动
        final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
        // 连接路径
        final String URL = "jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?characterEncoding=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
        // 数据库登录名称
        final String USERNAME = "root";
        // 数据库登录密码
        final String PASSWORD = "mysql123456";

        dataSource = new DruidDataSource();
        dataSource.setDriverClassName(DRIVER_NAME);
        dataSource.setUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setInitialSize(20);
        // dataSource.setMinIdle(1);
        dataSource.setMaxActive(1000);
        // dataSource.setPoolPreparedStatements(false);
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection(){
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            System.err.println("获取连接报错");
            // e.printStackTrace();
            return null;
        }
    }

    /**
     * 关闭资源
     */
    public static <T extends AutoCloseable> void close(T... ts){
        for (int i = 0, len = ts.length; i < len; i++) {
            if (ts[i] != null) {
                try {
                    ts[i].close();
                } catch (Exception e) {
                    System.err.println("关闭资源报错");
                    // e.printStackTrace();
                }
            }
        }

    }

    /** --------------------------------------------------------------------------------------- */



}
