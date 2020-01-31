package dao;

import exception.DBException;
import model.BankClient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankClientDAO {

    private Connection connection;

    public BankClientDAO(Connection connection) {
        this.connection = connection;
    }

    public List<BankClient> getAllBankClient() throws DBException {
        String sql = "SELECT id, name, password, money FROM bank_client";
        List<BankClient> bankClients = null;
        try (Statement statement = connection.createStatement()) {
            bankClients = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String password = resultSet.getString("password");
                Long money = resultSet.getLong("money");
                BankClient bankClient = new BankClient(id, name, password, money);
                bankClients.add(bankClient);
            }
            resultSet.close();
            return bankClients;
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public BankClient getClientById(long id) throws DBException {
        String sql = "SELECT name, password, money FROM bank_client WHERE id = ?";
        BankClient bankClient = null;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String password = resultSet.getString("password");
                Long money = resultSet.getLong("money");
                bankClient = new BankClient(id, name, password, money);
            }
            resultSet.close();
            return bankClient;
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public BankClient getClientByName(String name) throws DBException {
        BankClient bankClient = null;
        String sql = "SELECT id, password, money FROM bank_client WHERE name=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String password = resultSet.getString("password");
                Long money = resultSet.getLong("money");
                bankClient = new BankClient(id, name, password, money);
            }
            resultSet.close();
            return bankClient;
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void deleteClientByName (String name) throws DBException {
        String sql = "DELETE FROM bank_client WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.execute();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void addClient(BankClient client) throws DBException {
        String sql = "INSERT INTO bank_client (name, password, money) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, client.getName());
            preparedStatement.setString(2, client.getPassword());
            preparedStatement.setLong(3, client.getMoney());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public boolean validateClient(String name, String password) throws DBException {
        boolean validateOrNot = false;
        String sql = "SELECT id FROM bank_client WHERE name = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            validateOrNot = resultSet.next();
            resultSet.close();
            return validateOrNot;
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public boolean isClientHasSum(String name, Long expectedSum) throws DBException {
        boolean hasSum = false;
        String sql = "SELECT id FROM bank_client WHERE name = ? AND money >= ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setLong(2, expectedSum);
            ResultSet resultSet = statement.executeQuery();
            hasSum = resultSet.next();
            resultSet.close();
            return hasSum;
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void updateClientsMoney(String name, String password, Long transactValue) throws DBException {
        String sql = "UPDATE bank_client SET money = ? WHERE name = ? AND password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            Long moneyNew = getClientByName(name).getMoney() + transactValue;
            preparedStatement.setLong(1, moneyNew );
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, password);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public long getClientIdByName(String name) throws DBException {
        String sql = "SELECT id FROM bank_client WHERE name = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            ResultSet result = preparedStatement.executeQuery();
            Long id = null;
            if (result.next()) {
                id = result.getLong(1);
            }
            result.close();
            return id;
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void createTable() throws DBException {
        try {
            Statement stmt = connection.createStatement();
            stmt.execute("create table if not exists bank_client (id bigint auto_increment, name varchar(256), password varchar(256), money bigint, primary key (id))");
            stmt.close();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void dropTable() throws DBException {
       try {
           Statement stmt = connection.createStatement();
           stmt.executeUpdate("DROP TABLE IF EXISTS bank_client");
           stmt.close();
       } catch (SQLException e) {
           throw new DBException(e);
       }
    }

}
