package com.ub.msssm.services;

import com.ub.msssm.domain.Payment;
import com.ub.msssm.domain.PaymentEvent;
import com.ub.msssm.domain.PaymentState;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {

    Payment newPayment(Payment payment);

    StateMachine<PaymentState, PaymentEvent> preAuthorize(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> declineAuthorization(Long paymentId);
}
