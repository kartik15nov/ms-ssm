package com.ub.msssm.config;

import com.ub.msssm.domain.PaymentEvents;
import com.ub.msssm.domain.PaymentStates;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import java.util.UUID;

@SpringBootTest
class StateMachineConfigTest {

    @Autowired
    StateMachineFactory<PaymentStates, PaymentEvents> factory;

    @Test
    void testNewStateMachine() {
        StateMachine<PaymentStates, PaymentEvents> stateMachine = factory.getStateMachine(UUID.randomUUID());

        stateMachine.start();

        System.out.println("Initial State : " + stateMachine.getState().toString());

        stateMachine.sendEvent(PaymentEvents.PRE_AUTHORIZE);
        System.out.println("Current State : " + stateMachine.getState().toString());

        stateMachine.sendEvent(PaymentEvents.PRE_AUTH_APPROVED);
        System.out.println("Current State : " + stateMachine.getState().toString());

        stateMachine.sendEvent(PaymentEvents.PRE_AUTH_DECLINED);
        System.out.println("Current State : " + stateMachine.getState().toString());
    }
}