package model;

import util.OrderSystemException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    //JDBC编程
    public  void add(User user) throws OrderSystemException {
        //1.从数据库的datasource获取connection连接
        Connection connection = DBUtil.getConnection();
        //2.使用PreparedStatement对象构造sql语句 比起Statement，PrepareStatement -》更安全防止sql注入可以替换参数？
        String sql = "insert into user values(null,?,?,?)";
        PreparedStatement statement = null;
        //3.执行sql语句（executeQuery -》适合遍历结果集的语句比如select等, executeUpdate-》适用于直接更新的sql语句 比如插入 insert/删除delete等）
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1,user.getName());
            statement.setString(2,user.getPassword());
            statement.setInt(3,user.getIsAdmin());
            int ret = statement.executeUpdate();
            if(ret != 1) {
                throw new OrderSystemException("插入用户失败");
            }
            System.out.println("插入用户成功");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new OrderSystemException("插入用户失败");
        }
        //关闭连接释放资源(close) (如果是查询语句, 还需要遍历结果集合)
        finally {
            DBUtil.close(connection,statement,null);
        }
    }
    public  User selectByName(String name) throws OrderSystemException {
        //从数据库datasource对象获取到连接
        Connection connection = DBUtil.getConnection();
        //使用PreStatement对象构造sql语句
        PreparedStatement statement = null;
        ResultSet result = null;
        String sql = "select * from user where name = ?";
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1,name);
            //执行语句executeUpdate executeQuery
            result = statement.executeQuery();
            //遍历结果集
            if(result.next()) {
                User user = new User();
                user.setUserId(result.getInt("userId"));
                user.setName(result.getString("name"));
                user.setPassword(result.getString("password"));
                user.setIsAdmin(result.getInt("isAdmin"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new OrderSystemException("按姓名查找用户失败");
        } finally {
            //关闭资源
            DBUtil.close(connection,statement,result);
        }
        return null;
    }

    public  User selectById(int userId) throws OrderSystemException {
        // 1. 获取数据库连接
        Connection connection = DBUtil.getConnection();
        // 2. 拼装 SQL
        String sql = "select * from user where userId = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            // 3. 执行 SQL
            resultSet = statement.executeQuery();
            // 4. 遍历结果集, userId 是主键. 不会重复的. 最多只能查到一条记录
            if (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getInt("userId"));
                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));
                user.setIsAdmin(resultSet.getInt("isAdmin"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new OrderSystemException("按 id 查找用户失败");
        } finally {
            // 5. 断开连接
            DBUtil.close(connection, statement, resultSet);
        }
        return null;
    }

    public static void main(String[] args) throws OrderSystemException {
/*        User user = new User();
        user.setName("wangdong");
        user.setPassword("123");
        user.setIsAdmin(1);
        UserDao.add(user);*/

      /*  System.out.println(selectById(1));
        System.out.println(selectByName("wangdong"));*/
    }

}
