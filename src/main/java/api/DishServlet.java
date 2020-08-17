package api;

import com.google.gson.Gson;
import model.Dish;
import model.DishDao;
import model.User;
import util.OrderSystemException;
import util.OrderSystemUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/dish")
public class DishServlet extends HttpServlet {
    private Gson gson = new Gson().newBuilder().create();

    static class Request {
        public String name;
        public int price;
    }

    static class Response {
        public int ok;
        public String reason;
    }

    @Override
    //增加菜品
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        Response response = new Response();
        try {
            // 1. 检查用户的登陆状态.
            HttpSession session = req.getSession(false);
            if (session == null) {
                throw new OrderSystemException("用户未登录");
            }
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new OrderSystemException("用户未登录");
            }
            if (user.getIsAdmin() == 0) {
                throw new OrderSystemException("该用户不是管理员");
            }
            String body = OrderSystemUtil.readBody(req);
            Request request = gson.fromJson(body, Request.class);
            Dish dish = new Dish();
            dish.setName(request.name);
            dish.setPrice(request.price);
            DishDao dishDao = new DishDao();
            dishDao.add(dish);
            response.ok = 1;
            response.reason = "";
        } catch (OrderSystemException e) {
            response.ok = 0;
            response.reason = e.getMessage();
        } finally {
            //把数据写回resp
            resp.setContentType("application/json; charset=utf-8");
            String json = gson.toJson(response);
            resp.getWriter().write(json);

        }
    }

    //删除菜品
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        Response response = new Response();
        // 1. 检查用户的登陆状态.
        try {
            HttpSession session = req.getSession(false);
            if (session == null) {
                throw new OrderSystemException("用户未登录");
            }
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new OrderSystemException("用户未登录");
            }
            if (user.getIsAdmin() == 0) {
                throw new OrderSystemException("该用户不是管理员");
            }
            //读取到 dishId
            String dishIdStr = req.getParameter("dishId");
            if(dishIdStr == null) {
                throw new OrderSystemException("dishId 参数不正确");
            }
            int dishId = Integer.parseInt(dishIdStr);
            DishDao dishDao = new DishDao();
            dishDao.delete(dishId);
            response.ok = 1;
            response.reason = "";
        } catch (OrderSystemException e) {
            response.ok = 0;
            response.reason = e.getMessage();
            e.printStackTrace();
        } finally {
            resp.setContentType("application/json; charset=utf-8");
            String json = gson.toJson(response);
            resp.getWriter().write(json);
        }
    }
    //查看所有菜品
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Dish> dishes = new ArrayList<>();
        Response response = new Response();
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");
        try {
            // 1. 检测登陆状态
            HttpSession session = req.getSession(false);
            if (session == null) {
                throw new OrderSystemException("用户尚未登陆");
            }
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new OrderSystemException("用户尚未登陆");
            }
            // 2. 从数据库中读取数据
            DishDao dishDao = new DishDao();
            dishes = dishDao.selectAll();
            // 3. 把结果返回到页面即可
            String jsonString = gson.toJson(dishes);
            resp.getWriter().write(jsonString);
        } catch (OrderSystemException e) {
            // 失败的时候返回一个空的数组.
            String jsonString = gson.toJson(dishes);
            resp.getWriter().write(jsonString);
        }

    }

    }

