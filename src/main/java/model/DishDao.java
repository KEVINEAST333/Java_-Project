package model;
import util.OrderSystemException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
// 操作菜品表.
// 1. 新增菜品
// 2. 删除菜品
// 3. 查询所有菜品
// 4. 查询指定菜品
// 修改菜品信息, 也是可以支持的. (主要就是改价格)
public class DishDao {
    public  void add(Dish dish) throws OrderSystemException {
        // 1. 获取数据库连接
        Connection connection = DBUtil.getConnection();
        // 2. 拼装 SQL
        String sql = "insert into dishes values(null, ?, ?)";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, dish.getName());
            statement.setInt(2, dish.getPrice());
            // 3. 执行 SQL
            int ret = statement.executeUpdate();
            if (ret != 1) {
                throw new OrderSystemException("插入菜品失败");
            }
            System.out.println("插入菜品成功!");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new OrderSystemException("插入菜品失败");
        } finally {
            DBUtil.close(connection, statement, null);
        }
    }
    public  void delete(int dishId) throws OrderSystemException {
        // 1. 获取数据库连接
        Connection connection = DBUtil.getConnection();
        // 2. 拼装 SQL
        String sql = "delete from dishes where dishId = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, dishId);
            // 3. 执行 SQL
            int ret = statement.executeUpdate();
            if (ret != 1) {
                throw new OrderSystemException("删除菜品失败");
            }
            System.out.println("删除菜品成功");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new OrderSystemException("删除菜品失败");
        } finally {
            DBUtil.close(connection, statement, null);
        }
    }
    public  List<Dish> selectAll() throws OrderSystemException {
        //1.通过mysql datasource对象得到connection连接
        Connection connection = DBUtil.getConnection();
        List<Dish> list = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        //2.通过prepareStatement构造sql语句
        String sql = "select * from dishes";
        try {
            statement = connection.prepareStatement(sql);
            //执行语句
            resultSet = statement.executeQuery();
            //遍历结果集
            while (resultSet.next()) {
                Dish dish = new Dish();
                dish.setName(resultSet.getString("name"));
                dish.setDishId(resultSet.getInt("dishId"));
                dish.setPrice(resultSet.getInt("price"));
                list.add(dish);
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new OrderSystemException("遍历所有菜品失败");
        } finally {
            DBUtil.close(connection, statement, resultSet);
        }
    }
        public  Dish selectById(int dishId) throws OrderSystemException {
            // 1. 获取数据库连接
            Connection connection = DBUtil.getConnection();
            // 2. 拼装 SQL
            String sql = "select * from dishes where dishId = ?";
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(sql);
                statement.setInt(1, dishId);
                // 3. 执行 SQL
                resultSet = statement.executeQuery();
                // 4. 遍历结果集
                if (resultSet.next()) {
                    Dish dish = new Dish();
                    dish.setDishId(resultSet.getInt("dishId"));
                    dish.setName(resultSet.getString("name"));
                    dish.setPrice(resultSet.getInt("price"));
                    return dish;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new OrderSystemException("按照 id 查找菜品出错");
            } finally {
                DBUtil.close(connection, statement, resultSet);
            }
            return null;
        }

    public static void main(String[] args) throws OrderSystemException {
        /*Dish dish = new Dish();
        dish.setName("水煮肉片");
        dish.setPrice(25);
        DishDao.add(dish);*/
      /*  System.out.println(DishDao.selectAll().toString());
        DishDao.delete(2);
        System.out.println(DishDao.selectAll().toString());*/
      //  System.out.println(DishDao.selectById(1));
    }
    }
