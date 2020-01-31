package service;

import dao.BankClientDAO;
import exception.DBException;
import model.BankClient;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class BankClientService {

    public BankClientService() {
    }

    public List<BankClient> getAllClient() {
        try {
            return getBankClientDAO().getAllBankClient();
        }catch (DBException e){
           return null;
        }
    }

    public BankClient getClientById(long id) {
        try {
            return getBankClientDAO().getClientById(id);
        } catch (DBException e) {
           return null;
        }
    }

    public BankClient getClientByName(String name) {
        if (name == null){return null;}
        try {
            return getBankClientDAO().getClientByName(name);
        } catch (DBException e) {
            return null;
        }
    }

    public boolean deleteClient(String name) {
        if (name == null){return false;}
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.deleteClientByName(name);
            return true;
        } catch (DBException e) {
           return false;
        }
    }

    public boolean addClient(BankClient client) {
        if (client == null){
            return false;
        }
        String name = client.getName();
        String password = client.getPassword();
        Long money = client.getMoney();
        if(password == null || money == null ||name == null ){
            return false;
        }
        try {
            List<BankClient> bankClients = new BankClientDAO(getMysqlConnection()).getAllBankClient();
            for (BankClient addClientOrNot:bankClients){ //в БД не должно быть 2 пол-лей с одинаковым именем
                if(addClientOrNot.getName().equals(name)){
                    return false;
                }
            }
            getBankClientDAO().addClient(client);
            return true;
        }catch (DBException e) {
            return false;
        }
    }

    public boolean sendMoneyToClient(BankClient sender, String name, Long value) {
        String senderName = sender.getName();
        String senderPassword = sender.getPassword();
        Long senderMany = sender.getMoney();
       if (senderName == null || senderPassword == null || senderMany == null){return false;}

        Connection connection = getMysqlConnection();
        BankClientDAO bankClientDAO = new BankClientDAO(connection);

        try{
            boolean validate = bankClientDAO.validateClient(senderName, senderPassword);
            boolean hasSum = bankClientDAO.isClientHasSum(senderName, value);
            BankClient recipient = bankClientDAO.getClientByName(name);
            if ( validate && hasSum && recipient !=null){
                connection.setAutoCommit(false);
                bankClientDAO.updateClientsMoney(senderName, senderPassword, -value);
                bankClientDAO.updateClientsMoney(recipient.getName(), recipient.getPassword(), +value);
                connection.commit();
                return true;
            }
        }catch (DBException | SQLException e){

        }
        return false;
    }

    public void cleanUp() throws DBException{
        BankClientDAO dao = getBankClientDAO();
        dao.dropTable();
    }

    public void createTable() throws DBException {
        BankClientDAO dao = getBankClientDAO();
        dao.createTable();
    }

    private static Connection getMysqlConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            StringBuilder url = new StringBuilder();

            url.
                    append("jdbc:mysql://").        //db type
                    append("localhost:").           //host name
                    append("3306/").                //port
                    append("db_examplemy?").          //db name
                    append("user=root&").          //login
                    append("password=1234&").         //password
                    append("serverTimezone=UTC&").
                    append("useSSL = false");


            System.out.println("URL: " + url + "\n");

            Connection connection = DriverManager.getConnection(url.toString());
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    private static BankClientDAO getBankClientDAO() {
        return new BankClientDAO(getMysqlConnection());
    }
}
