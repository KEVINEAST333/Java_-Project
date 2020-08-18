package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.User;
import model.UserDao;
import util.OrderSystemException;
import util.OrderSystemUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private Gson gson = new GsonBuilder().create();
    static class Request {
        public String name;
        public String password;
    }
    static class Response {
        public int ok;
        public int isAdmin;
        public String name;
        public String reason;
    }
    @Override
    //登录
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        Response response = new Response();
        //把req请求转为string
        String body = OrderSystemUtil.readBody(req);
        UserDao userDao = new UserDao();
        //把body json  转为request对象
        Request request = gson.fromJson(body,Request.class);
        try {
            User user = userDao.selectByName(request.name);
            if(user == null || !user.getPassword().equals(request.password)) {
                throw new OrderSystemException("用户名或密码错误");
            }
            // 如果登陆成功, 就创建 session 对象. [重要]
            //req.getSession(true)代表没有session就创建
            //req.getSession(true)没有session就返回null
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            response.ok = 1;
            response.reason = "";
            response.name = user.getName();
            response.isAdmin = user.getIsAdmin();
        } catch (OrderSystemException e) {
            response.ok = 0;
            response.reason = e.getMessage();
            e.printStackTrace();
        } finally {
            //结果写回给客户端
            resp.setContentType("application/json; charset=utf-8");
            String jsonString = gson.toJson(response);
            resp.getWriter().write(jsonString);
        }
    }
    // 对应到检测登陆状态 API
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        Response response = new Response();
            try {
                //1.获取session如果没有不创建
                HttpSession session = req.getSession(false);
                if(session == null) {
                    throw new OrderSystemException("用户未登录");
                }
                // 2. 从 session 中获取 user 对象
               User user =  (User) session.getAttribute("user");
                if(user == null) {
                    throw new OrderSystemException("用户未登录");
                }
                // 3. 把 user 中的信息填充进返回值结果中.
                response.ok = 1;
                response.reason = "";
                response.name = user.getName();
                response.isAdmin = user.getIsAdmin();
            } catch (OrderSystemException e) {
                response.ok = 0;
                response.reason = e.getMessage();
                e.printStackTrace();
        } finally {
                //4.写回响应
                resp.setContentType("application/json; charset=utf-8");
                String json = gson.toJson(response);
                resp.getWriter().write(json);
            }
    }
}
