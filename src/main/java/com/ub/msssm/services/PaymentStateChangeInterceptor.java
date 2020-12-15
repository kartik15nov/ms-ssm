package com.ub.msssm.services;

import com.ub.msssm.domain.Payment;
import com.ub.msssm.domain.PaymentEvent;
import com.ub.msssm.domain.PaymentState;
import com.ub.msssm.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {

    private final PaymentRepository paymentRepository;

    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state, Message<PaymentEvent> message,
                               Transition<PaymentState, PaymentEvent> transition, StateMachine<PaymentState, PaymentEvent> stateMachine) {

        Optional.ofNullable(message)
                .flatMap(paymentEventMessage -> Optional.ofNullable((Long) paymentEventMessage.getHeaders().getOrDefault(PaymentServiceImpl.PAYMENT_ID_HEADER, -1L)))
                .ifPresent(paymentId -> {
                    Payment payment = paymentRepository.getOne(paymentId);
                    payment.setPaymentState(state.getId());
                    paymentRepository.save(payment);
                });
    }
}
