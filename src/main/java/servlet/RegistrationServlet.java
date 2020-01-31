package servlet;

import model.BankClient;
import service.BankClientService;
import util.PageGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String res = PageGenerator.getInstance().getPage("registrationPage.html", null);
        resp.getWriter().println(res);
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean result = false;
        String name = req.getParameter("name");
        String password = req.getParameter("password");
        String money = req.getParameter("money");
        Long moneyLong = null;

        try {
            moneyLong = Long.parseLong(money);
            BankClient bankClient = new BankClient(name, password, moneyLong);
            BankClientService bankClientService = new BankClientService();
             result = bankClientService.addClient(bankClient);
        }catch (NumberFormatException e){
           // result = false;
        }

        if (result){
            Map<String, Object> good = new HashMap<>();
            good.put("message", "Add client successful");
            String res = PageGenerator.getInstance().getPage("resultPage.html",  good);
            resp.getWriter().println(res);
            resp.setContentType("text/html;charset=utf-8");
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            Map<String, Object> bad = new HashMap<>();
            bad.put("message", "Client not add");
            String res = PageGenerator.getInstance().getPage("resultPage.html",  bad);
            resp.getWriter().println(res);
            resp.setContentType("text/html;charset=utf-8");
         //   resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

}
