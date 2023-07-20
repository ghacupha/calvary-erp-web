package io.github.calvary.erp.errors;

import io.github.calvary.web.rest.errors.BadRequestAlertException;
import io.github.calvary.web.rest.errors.ErrorConstants;

public class TransactionHasNotBeenProposedException extends BadRequestAlertException {

    public TransactionHasNotBeenProposedException(String defaultMessage, String entityName, String errorKey) {
        super(ErrorConstants.DEFAULT_TYPE, defaultMessage, entityName, errorKey);
    }
}
