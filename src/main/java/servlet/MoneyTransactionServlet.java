package servlet;

import model.BankClient;
import service.BankClientService;
import util.PageGenerator;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/transaction")
public class MoneyTransactionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String res = PageGenerator.getInstance().getPage("moneyTransactionPage.html", null);
        resp.getWriter().println(res);
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String senderName = req.getParameter("senderName");
        String senderPass = req.getParameter("senderPass");
        String countValue = req.getParameter("count");
        String nameTo = req.getParameter("nameTo");

        Long value = null;
        boolean result = false;
        try {
            value = Long.parseLong(countValue);
            BankClientService bankClientService = new BankClientService();
            BankClient sender = bankClientService.getClientByName(senderName);
            if (sender!= null && sender.getPassword().equals(senderPass)){ //если такой существует и пароль совпадает
                 result = bankClientService.sendMoneyToClient(sender, nameTo, value);
            }
        }catch (NumberFormatException e){
            //ignore
        }

        req.setAttribute("result", result);
        String path = "/result";
        RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher(path);
        requestDispatcher.forward(req, resp);
    }
}
