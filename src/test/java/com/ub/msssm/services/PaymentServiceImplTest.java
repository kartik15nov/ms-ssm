package com.ub.msssm.services;

import com.ub.msssm.domain.Payment;
import com.ub.msssm.domain.PaymentEvent;
import com.ub.msssm.domain.PaymentState;
import com.ub.msssm.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder().amount(new BigDecimal("12.99")).build();
    }

    @Transactional
    @Test
    void preAuthorize() {
        Payment savedPayment = paymentService.newPayment(payment);
        System.out.println("Should be NEW");
        System.out.println(savedPayment.getPaymentState());

        StateMachine<PaymentState, PaymentEvent> stateMachine = paymentService.preAuthorize(savedPayment.getId());

        Payment preAuthorizedPayment = paymentRepository.getOne(savedPayment.getId());

        System.out.println("Should be PRE_AUTH");
        System.out.println(stateMachine.getState().getId());
        System.out.println(preAuthorizedPayment);
    }
}