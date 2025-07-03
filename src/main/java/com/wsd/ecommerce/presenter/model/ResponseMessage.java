package com.wsd.ecommerce.presenter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseMessage {

    OPERATION_SUCCESSFUL("operation.success"),

    RECORD_NOT_FOUND("record.not.found"),

    LOCALE_RECORD_NOT_FOUND("locale.record.not.found"),

    INTER_SERVICE_COMMUNICATION_ERROR("inter.service.communication.exception"),

    INTERNAL_SERVICE_EXCEPTION("internal.service.exception"),

    DATABASE_EXCEPTION("database.exception"),

    JSON_PARSE_ERROR("json.parse.error");

    private final String responseMessage;
}
