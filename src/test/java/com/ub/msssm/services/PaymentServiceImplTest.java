package com.ub.msssm.services;

import com.ub.msssm.domain.Payment;
import com.ub.msssm.domain.PaymentEvent;
import com.ub.msssm.domain.PaymentState;
import com.ub.msssm.repository.PaymentRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.ub.msssm.domain.PaymentState.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PaymentServiceImplTest {

    static Map<PaymentState, Integer> repetitions = new HashMap<>();

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

    @Transactional
    @RepeatedTest(10)
    void authorize() {
        Payment savedPayment = paymentService.newPayment(payment);
        System.out.println("Should be NEW");
        System.out.println(savedPayment.getPaymentState());

        StateMachine<PaymentState, PaymentEvent> stateMachine = paymentService.preAuthorize(savedPayment.getId());

        if (stateMachine.getState().getId().equals(PaymentState.PRE_AUTH)) {
            System.out.println("Payment is pre authorized!");

            StateMachine<PaymentState, PaymentEvent> authSm = paymentService.authorizePayment(savedPayment.getId());

            System.out.println("Result of auth: " + authSm.getState().getId());
        } else
            System.out.println("Payment Failed pre-auth..!");
    }

    @Transactional
    @RepeatedTest(100)
    @DisplayName("Testing change states NEW -> PRE_AUTH -> AUTH")
    void auth() {
        //given
        Payment savedPayment = paymentService.newPayment(payment);

        //when
        StateMachine<PaymentState, PaymentEvent> sm = paymentService.preAuthorize(savedPayment.getId());
        paymentService.authorizePayment(savedPayment.getId());
        //then
        Payment paymentRepositoryOne = paymentRepository.getOne(savedPayment.getId());

        PaymentState state = paymentRepositoryOne.getPaymentState();

        repetitions.putIfAbsent(state, 0);
        repetitions.merge(state, 1, Integer::sum);

        assertThat(state).isIn(AUTH, AUTH_ERROR, PRE_AUTH_ERROR);
    }

    @Test
    @Order(Integer.MAX_VALUE)
    @DisplayName("Percentage of result states should match AUTH(64%), AUTH_ERROR(16%), PRE_AUTH_ERROR(20%)")
    void repetitionPercentageTest() {
        int totalCount = repetitions.values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();
        assertAll(
                () -> assertThat(100 * repetitions.get(AUTH) / totalCount).isBetween(56, 72),              //theoretically 64
                () -> assertThat(100 * repetitions.get(PRE_AUTH_ERROR) / totalCount).isBetween(14, 26),    //theoretically 20
                () -> assertThat(100 * repetitions.get(AUTH_ERROR) / totalCount).isBetween(10, 22)         //theoretically 16
        );
    }
}