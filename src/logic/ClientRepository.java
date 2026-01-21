public boolean deleteById(int id) {
    String sql = "DELETE FROM clients WHERE id = ?";
    try (Connection conn = db.getConnection();
         PreparedStatement st = conn.prepareStatement(sql)) {
        st.setInt(1, id);
        return st.executeUpdate() > 0;
    } catch (SQLException e) { return false; }
}