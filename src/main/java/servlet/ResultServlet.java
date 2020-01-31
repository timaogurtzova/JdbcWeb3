package servlet;

import util.PageGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/result")
public class ResultServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> mapRes = new HashMap<>();
        mapRes.put("message", "null");
        String res = PageGenerator.getInstance().getPage("resultPage.html", mapRes);
        resp.getWriter().println(res);
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object result = req.getAttribute("result"); //узнаем,прошла ли транзакция в др.сервлете
        Boolean resultMoneyTransaction = false;

        Map<String, Object> mapRes = new HashMap<>();
        try {
            resultMoneyTransaction = (Boolean)result;
        } catch (ClassCastException e) {
            //ignore
        }
        if (resultMoneyTransaction){
            mapRes.put("message", "The transaction was successful");
            String res = PageGenerator.getInstance().getPage("resultPage.html", mapRes);
            resp.getWriter().println(res);
            resp.setContentType("text/html;charset=utf-8");
            resp.setStatus(HttpServletResponse.SC_OK);
        }else {
            mapRes.put("message", "transaction rejected");
            String res = PageGenerator.getInstance().getPage("resultPage.html", mapRes);
            resp.getWriter().println(res);
            resp.setContentType("text/html;charset=utf-8");
          // resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

}
