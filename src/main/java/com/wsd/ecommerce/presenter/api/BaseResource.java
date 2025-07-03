package com.wsd.ecommerce.presenter.api;

import com.wsd.ecommerce.presenter.model.ResponseMessage;
import com.wsd.ecommerce.presenter.utils.LocaleMessageService;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseResource {

    private LocaleMessageService localeMessageService;

    @Autowired
    public void setLocaleMessageService(LocaleMessageService localeMessageService) {
        this.localeMessageService = localeMessageService;
    }

    public String getMessage(ResponseMessage key) {
        return localeMessageService.getLocalMessage(key);
    }
}
