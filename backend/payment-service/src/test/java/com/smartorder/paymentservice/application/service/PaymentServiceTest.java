package com.smartorder.paymentservice.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.smartorder.paymentservice.application.dto.OrderCreatedMessage;
import com.smartorder.paymentservice.domain.model.OrderId;
import com.smartorder.paymentservice.domain.model.PaymentStatus;
import com.smartorder.paymentservice.domain.port.PaymentEventPublisher;
import com.smartorder.paymentservice.domain.port.PaymentRequestRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRequestRepository repository;
    @Mock
    private PaymentEventPublisher publisher;
    @InjectMocks
    private PaymentService service;

    private final UUID orderId = UUID.randomUUID();
    private final OrderCreatedMessage message = new OrderCreatedMessage(orderId, 1000, "USD");

    @Test
    void whenClaimed_processesPaymentExactlyOnce() {
        when(repository.claim(any(), any())).thenReturn(true);

        service.processOrderCreated(message);

        verify(repository).claim(eq(OrderId.of(orderId)), any());
        // Exactly one terminal status is written (SUCCEEDED or FAILED — randomized).
        verify(repository).upsert(
                eq(OrderId.of(orderId)),
                argThat(s -> s == PaymentStatus.SUCCEEDED || s == PaymentStatus.FAILED),
                any());
        // Exactly one terminal event is published, whichever branch ran.
        assertThat(mockingDetails(publisher).getInvocations()).hasSize(1);
    }

    @Test
    void whenNotClaimed_skipsProcessing() {
        when(repository.claim(any(), any())).thenReturn(false);

        service.processOrderCreated(message);

        verify(repository).claim(any(), any());
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(publisher);
    }
}
