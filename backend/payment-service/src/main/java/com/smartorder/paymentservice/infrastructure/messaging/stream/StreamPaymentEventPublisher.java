package com.smartorder.paymentservice.infrastructure.messaging.stream;

import com.smartorder.paymentservice.domain.event.PaymentFailed;
import com.smartorder.paymentservice.domain.event.PaymentSucceeded;
import com.smartorder.paymentservice.domain.port.PaymentEventPublisher;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class StreamPaymentEventPublisher implements PaymentEventPublisher {

    private final StreamBridge streamBridge;

    public StreamPaymentEventPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Override
    public void publishPaymentSucceeded(PaymentSucceeded event) {
        streamBridge.send("paymentSucceeded-out-0", event);
    }

    @Override
    public void publishPaymentFailed(PaymentFailed event) {
        streamBridge.send("paymentFailed-out-0", event);
    }
}

