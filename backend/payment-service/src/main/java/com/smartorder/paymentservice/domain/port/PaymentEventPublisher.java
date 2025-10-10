package com.smartorder.paymentservice.domain.port;

import com.smartorder.paymentservice.domain.event.PaymentFailed;
import com.smartorder.paymentservice.domain.event.PaymentSucceeded;

public interface PaymentEventPublisher {

    void publishPaymentSucceeded(PaymentSucceeded event);

    void publishPaymentFailed(PaymentFailed event);
}

