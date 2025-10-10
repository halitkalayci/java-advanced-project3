package com.smartorder.catalogservice.infrastructure.persistence.jdbc;

import com.smartorder.catalogservice.domain.model.Product;
import com.smartorder.catalogservice.domain.port.ProductRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcProductRepositoryAdapter implements ProductRepository {

    private static final ProductRowMapper ROW_MAPPER = new ProductRowMapper();

    private final JdbcTemplate jdbcTemplate;

    public JdbcProductRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UUID save(Product product) {
        jdbcTemplate.update(
                "INSERT INTO products (id, name, unit_cents, currency, active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                product.id(),
                product.name(),
                product.unitCents(),
                product.currency(),
                product.active(),
                Timestamp.from(product.createdAt()),
                product.updatedAt() == null ? null : Timestamp.from(product.updatedAt()));
        return product.id();
    }

    @Override
    public void update(Product product) {
        jdbcTemplate.update(
                "UPDATE products SET unit_cents = ?, currency = ?, active = ?, updated_at = ? WHERE id = ?",
                product.unitCents(),
                product.currency(),
                product.active(),
                Timestamp.from(product.updatedAt()),
                product.id());
    }

    @Override
    public Optional<Product> findById(UUID id) {
        List<Product> products = jdbcTemplate.query(
                "SELECT id, name, unit_cents, currency, active, created_at, updated_at FROM products WHERE id = ?",
                ROW_MAPPER,
                id);
        return products.stream().findFirst();
    }

    @Override
    public List<Product> findAll(int offset, int limit) {
        return jdbcTemplate.query(
                "SELECT id, name, unit_cents, currency, active, created_at, updated_at FROM products ORDER BY created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
                ROW_MAPPER,
                offset,
                limit);
    }

    private static class ProductRowMapper implements RowMapper<Product> {

        @Override
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Product(
                    UUID.fromString(rs.getString("id")),
                    rs.getString("name"),
                    rs.getLong("unit_cents"),
                    rs.getString("currency"),
                    rs.getBoolean("active"),
                    rs.getTimestamp("created_at").toInstant(),
                    toInstant(rs.getTimestamp("updated_at")));
        }

        private java.time.Instant toInstant(Timestamp timestamp) {
            return timestamp == null ? null : timestamp.toInstant().atZone(ZoneOffset.UTC).toInstant();
        }
    }
}

