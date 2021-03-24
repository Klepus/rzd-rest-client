package com.github.klepus.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class PrepareMessageService {

    private final Locale locale;
    private final MessageSource messageSource;

    public PrepareMessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
        this.locale = Locale.forLanguageTag("ru-RU");
    }

    public String getMessage(String message) {
        return messageSource.getMessage(message, null, locale);
    }

    public String getMessage(String message, Object... args) {
        return messageSource.getMessage(message, args, locale);
    }
}
