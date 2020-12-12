package com.ub.msssm.config;

import com.ub.msssm.domain.PaymentEvents;
import com.ub.msssm.domain.PaymentStates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Slf4j
@EnableStateMachineFactory
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentStates, PaymentEvents> {

    @Override
    public void configure(StateMachineStateConfigurer<PaymentStates, PaymentEvents> states) throws Exception {
        states.withStates()
                .initial(PaymentStates.NEW)
                .states(EnumSet.allOf(PaymentStates.class))
                .end(PaymentStates.AUTH)
                .end(PaymentStates.AUTH_ERROR)
                .end(PaymentStates.PRE_AUTH_ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentStates, PaymentEvents> transitions) throws Exception {
        transitions
                .withExternal().source(PaymentStates.NEW).target(PaymentStates.NEW).event(PaymentEvents.PRE_AUTHORIZE)
                .and()
                .withExternal().source(PaymentStates.NEW).target(PaymentStates.PRE_AUTH).event(PaymentEvents.PRE_AUTH_APPROVED)
                .and()
                .withExternal().source(PaymentStates.NEW).target(PaymentStates.AUTH_ERROR).event(PaymentEvents.PRE_AUTH_DECLINED);
    }
}
