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
import java.io.IOException;
import java.util.jar.JarEntry;
@WebServlet("/register")
//注册用户
public class RegisterServlet extends HttpServlet {
    Gson gson = new GsonBuilder().create();
    // 读取的 JSON 请求对象
    static class Request {
        public String name;
        public String password;
    }
    static class Response {
        public int ok;
        public String reason;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        Response response = new Response();
        String body = OrderSystemUtil.readBody(req);
        //把json转化为request 对象
        Request request = gson.fromJson(body,Request.class);
        UserDao userDao = new UserDao();
        try {
            if(userDao.selectByName(request.name) != null) {
                throw new OrderSystemException("该用户已存在");
            }
           // 把提交的用户名密码构造成 User 对象, 插入数据库
            User user = new User();
            user.setName(request.name);
            user.setPassword(request.password);
            user.setIsAdmin(0);
            userDao.add(user);
            response.ok = 1;
            response.reason = "";
        } catch (OrderSystemException e) {
            response.ok = 0;
            response.reason = e.getMessage();
            e.printStackTrace();
        } finally {
            //将response写回json中
            String json = gson.toJson(response);
            resp.setContentType("application/json; charset=utf-8");
            resp.getWriter().write(json);
        }
    }
}
