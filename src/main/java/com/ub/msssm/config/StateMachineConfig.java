package com.ub.msssm.config;

import com.ub.msssm.domain.PaymentEvents;
import com.ub.msssm.domain.PaymentStates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;

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
}
