package com.mayday9.splatoonbot.common.web;

import com.mayday9.splatoonbot.common.web.context.Messages;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.annotation.Resource;
import java.util.Locale;


public class DefaultController {
    protected Messages messages;

    protected String getMessage(String messageKey, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messages.getMessage(messageKey, args, locale);
    }

    @Resource
    public void setMessages(Messages messages) {
        this.messages = messages;
    }
}
