package repository;

import data.interfaces.IDB;
import models.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository implements ICategoryRepository {
    private final IDB db;

    public CategoryRepository(IDB db) {
        this.db = db;
    }

    public List<Category> getAll() {
        String sql = "SELECT * FROM categories ORDER BY id ASC";
        List<Category> list = new ArrayList<>();

        try (Connection conn = db.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
