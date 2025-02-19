package com.esportify.configuration;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class ApplicationConfiguration {

//    @Bean
//    public Validator validator() {
//        ValidatorFactory factory = Validation.byProvider(HibernateValidator.class)
//                .configure()
//                .messageInterpolator(new ResourceBundleMessageInterpolator()) // ✅ Utilise le bon interpolateur
//                .buildValidatorFactory();
//        return factory.getValidator();
//    }

}
