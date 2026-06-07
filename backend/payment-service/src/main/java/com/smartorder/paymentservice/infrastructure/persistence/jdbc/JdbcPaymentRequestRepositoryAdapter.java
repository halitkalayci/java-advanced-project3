package com.smartorder.paymentservice.infrastructure.persistence.jdbc;

import com.smartorder.paymentservice.domain.model.OrderId;
import com.smartorder.paymentservice.domain.model.PaymentRequest;
import com.smartorder.paymentservice.domain.model.PaymentStatus;
import com.smartorder.paymentservice.domain.port.PaymentRequestRepository;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcPaymentRequestRepositoryAdapter implements PaymentRequestRepository {

    private static final String SELECT_BY_ORDER_ID =
            "SELECT order_id, status, last_attempt_at FROM payment_requests WHERE order_id = ?";
    private static final String UPSERT_STATEMENT =
            "MERGE INTO payment_requests (order_id, status, last_attempt_at) KEY (order_id) VALUES (?, ?, ?)";
    // Portable across H2 (MODE=PostgreSQL) and PostgreSQL: only the first writer
    // inserts a row; the primary key on order_id makes the claim atomic.
    private static final String CLAIM_STATEMENT =
            "INSERT INTO payment_requests (order_id, status, last_attempt_at) VALUES (?, ?, ?) "
                    + "ON CONFLICT DO NOTHING";

    private final JdbcTemplate jdbcTemplate;

    public JdbcPaymentRequestRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<PaymentRequest> findByOrderId(OrderId orderId) {
        return jdbcTemplate.query(SELECT_BY_ORDER_ID, paymentRequestRowMapper(), orderId.value()).stream().findFirst();
    }

    @Override
    public boolean claim(OrderId orderId, Instant lastAttemptAt) {
        int inserted = jdbcTemplate.update(
                CLAIM_STATEMENT,
                orderId.value(),
                PaymentStatus.PENDING.name(),
                lastAttemptAt == null ? null : Timestamp.from(lastAttemptAt));
        return inserted > 0;
    }

    @Override
    public void upsert(OrderId orderId, PaymentStatus status, Instant lastAttemptAt) {
        jdbcTemplate.update(
                UPSERT_STATEMENT,
                orderId.value(),
                status.name(),
                lastAttemptAt == null ? null : Timestamp.from(lastAttemptAt));
    }

    private RowMapper<PaymentRequest> paymentRequestRowMapper() {
        return (ResultSet rs, int rowNum) -> {
            OrderId orderId = OrderId.of((UUID) rs.getObject("order_id"));
            PaymentStatus status = PaymentStatus.valueOf(rs.getString("status"));
            Timestamp timestamp = rs.getTimestamp("last_attempt_at");
            Instant lastAttemptAt = timestamp == null ? null : timestamp.toInstant();
            return PaymentRequest.restore(orderId, status, lastAttemptAt);
        };
    }
}

