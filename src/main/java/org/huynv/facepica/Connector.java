package org.huynv.facepica;

import java.sql.*;

public class Connector {
    private static Connection con;
    private static Statement stmt;
    private static String sql;

    private static void init() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost/facepica", "root", "code");
            stmt = con.createStatement();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    public static final Topican getTopicanByIdFace(String face_id) {
        init();
        Topican topican = new Topican(0,"none", "none");

        try {
            sql = "SELECT id, account, email FROM mappings, topican WHERE mappings.topican_id = topican.id AND face_id = '" + face_id + "'";
            ResultSet rs = stmt.executeQuery(sql);

            rs.next();
            topican = new Topican(rs.getInt(1), rs.getString(2), rs.getString(2));

            con.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        return topican;
    }

    public static final void attendance(int topican_id, String image) {
        init();

        try {
            sql = "INSERT INTO attendance (topican_id, image_name) VALUES (?,?)";

            PreparedStatement preparedStatement = con.prepareStatement(sql);

            preparedStatement.setInt(1, topican_id);
            preparedStatement.setString(2, image);

            preparedStatement.execute();

            con.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
}
