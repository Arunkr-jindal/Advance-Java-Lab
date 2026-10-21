package com.shopsphere.dao;

import com.shopsphere.model.Coupon;
import com.shopsphere.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CouponDAO {

    private Coupon map(ResultSet rs) throws SQLException {
        Coupon c = new Coupon();
        c.setCouponId(rs.getInt("coupon_id"));
        c.setCode(rs.getString("code"));
        c.setDiscountType(rs.getString("discount_type"));
        c.setDiscountValue(rs.getDouble("discount_value"));
        c.setMinimumOrder(rs.getDouble("minimum_order"));
        c.setMaximumDiscount(rs.getDouble("maximum_discount"));
        c.setExpiryDate(rs.getDate("expiry_date"));
        c.setStatus(rs.getBoolean("status"));
        return c;
    }

    public Coupon findByCode(String code) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM coupons WHERE code = ?")) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public List<Coupon> findAll() throws SQLException {
        List<Coupon> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM coupons ORDER BY coupon_id DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public int insert(Coupon cp) throws SQLException {
        String sql = "INSERT INTO coupons (code, discount_type, discount_value, minimum_order, maximum_discount, expiry_date, status) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cp.getCode());
            ps.setString(2, cp.getDiscountType());
            ps.setDouble(3, cp.getDiscountValue());
            ps.setDouble(4, cp.getMinimumOrder());
            ps.setDouble(5, cp.getMaximumDiscount());
            ps.setDate(6, cp.getExpiryDate());
            ps.setBoolean(7, cp.isStatus());
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) { return k.next() ? k.getInt(1) : 0; }
        }
    }

    public boolean setStatus(int couponId, boolean active) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE coupons SET status = ? WHERE coupon_id = ?")) {
            ps.setBoolean(1, active);
            ps.setInt(2, couponId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean hasUserUsed(int couponId, int userId) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM coupon_usage WHERE coupon_id = ? AND user_id = ?")) {
            ps.setInt(1, couponId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public void recordUsage(Connection c, int couponId, int userId, int orderId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO coupon_usage (coupon_id, user_id, order_id) VALUES (?,?,?)")) {
            ps.setInt(1, couponId);
            ps.setInt(2, userId);
            ps.setInt(3, orderId);
            ps.executeUpdate();
        }
    }
}
