package com.smartorder.paymentservice.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void rejectsNegativeAmount() {
        assertThatThrownBy(() -> Money.of(-1, "USD"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNullCurrency() {
        assertThatThrownBy(() -> Money.of(100, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void createsValidMoney() {
        Money money = Money.of(1500, "USD");
        assertThat(money.cents()).isEqualTo(1500);
        assertThat(money.currency()).isEqualTo("USD");
    }
}
