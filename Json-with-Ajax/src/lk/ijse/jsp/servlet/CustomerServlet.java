package lk.ijse.jsp.servlet;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;


@WebServlet(urlPatterns = {"/pages/customer"})
public class CustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/assignment", "root", "1234");
            PreparedStatement pstm = connection.prepareStatement("select * from Customer");
            ResultSet rst = pstm.executeQuery();

            JsonArrayBuilder allCustomers = Json.createArrayBuilder();
            while (rst.next()) {
                String id = rst.getString(1);
                String name = rst.getString(2);
                String address = rst.getString(3);

                JsonObjectBuilder customerObject = Json.createObjectBuilder();
                customerObject.add("id", id);
                customerObject.add("name", name);
                customerObject.add("address", address);
                allCustomers.add(customerObject.build());
            }

            resp.getWriter().print(allCustomers.build());

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cusID = req.getParameter("cusID");
        String cusName = req.getParameter("cusName");
        String cusAddress = req.getParameter("cusAddress");
        String option = req.getParameter("option");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/assignment", "root", "1234");

            switch (option) {
                case "add":
                    PreparedStatement pstm = connection.prepareStatement("insert into Customer values(?,?,?)");
                    pstm.setObject(1, cusID);
                    pstm.setObject(2, cusName);
                    pstm.setObject(3, cusAddress);
                    resp.addHeader("Content-Type", "application/json");

                    if (pstm.executeUpdate() > 0) {
                        JsonObjectBuilder response = Json.createObjectBuilder();
                        response.add("state", "Ok");
                        response.add("message", "Successfully Added.!");
                        response.add("data", "");
                        resp.getWriter().print(response.build());
                    }
                    break;

                case "delete":
                    PreparedStatement pstm2 = connection.prepareStatement("delete from Customer where cusID=?");
                    pstm2.setObject(1, cusID);
                    resp.addHeader("Content-Type", "application/json");

                    if (pstm2.executeUpdate() > 0) {
                        JsonObjectBuilder response = Json.createObjectBuilder();
                        response.add("state", "Ok");
                        response.add("message", "Customer Deleted..!");
                        response.add("data", "");
                        resp.getWriter().print(response.build());
                    }
                    break;

                case "update":
                    PreparedStatement pstm3 = connection.prepareStatement("update Customer set cusName=?,cusAddress=? where cusID=?");
                    pstm3.setObject(3, cusID);
                    pstm3.setObject(1, cusName);
                    pstm3.setObject(2, cusAddress);
                    resp.addHeader("Content-Type", "application/json");

                    if (pstm3.executeUpdate() > 0) {
                        JsonObjectBuilder response = Json.createObjectBuilder();
                        response.add("state", "Ok");
                        response.add("message", "Customer Updated..!");
                        response.add("data", "");
                        resp.getWriter().print(response.build());
                    }
                    break;
            }


        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);

        } catch (SQLException e) {
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("state", "Error");
            response.add("message", e.getMessage());
            response.add("data", "");
            resp.setStatus(400);
            resp.getWriter().print(response.build());
        }
    }
}
