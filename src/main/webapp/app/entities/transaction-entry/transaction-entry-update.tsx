import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ITransactionAccount } from 'app/shared/model/transaction-account.model';
import { getEntities as getTransactionAccounts } from 'app/entities/transaction-account/transaction-account.reducer';
import { IAccountTransaction } from 'app/shared/model/account-transaction.model';
import { getEntities as getAccountTransactions } from 'app/entities/account-transaction/account-transaction.reducer';
import { ITransactionEntry } from 'app/shared/model/transaction-entry.model';
import { TransactionEntryTypes } from 'app/shared/model/enumerations/transaction-entry-types.model';
import { getEntity, updateEntity, createEntity, reset } from './transaction-entry.reducer';

export const TransactionEntryUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const transactionAccounts = useAppSelector(state => state.transactionAccount.entities);
  const accountTransactions = useAppSelector(state => state.accountTransaction.entities);
  const transactionEntryEntity = useAppSelector(state => state.transactionEntry.entity);
  const loading = useAppSelector(state => state.transactionEntry.loading);
  const updating = useAppSelector(state => state.transactionEntry.updating);
  const updateSuccess = useAppSelector(state => state.transactionEntry.updateSuccess);
  const transactionEntryTypesValues = Object.keys(TransactionEntryTypes);

  const handleClose = () => {
    navigate('/transaction-entry' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getTransactionAccounts({}));
    dispatch(getAccountTransactions({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...transactionEntryEntity,
      ...values,
      transactionAccount: transactionAccounts.find(it => it.id.toString() === values.transactionAccount.toString()),
      accountTransaction: accountTransactions.find(it => it.id.toString() === values.accountTransaction.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          transactionEntryType: 'DEBIT',
          ...transactionEntryEntity,
          transactionAccount: transactionEntryEntity?.transactionAccount?.id,
          accountTransaction: transactionEntryEntity?.accountTransaction?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="calvaryErpApp.transactionEntry.home.createOrEditLabel" data-cy="TransactionEntryCreateUpdateHeading">
            <Translate contentKey="calvaryErpApp.transactionEntry.home.createOrEditLabel">Create or edit a TransactionEntry</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="transaction-entry-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label="Entry Amount"
                id="transaction-entry-entryAmount"
                name="entryAmount"
                data-cy="entryAmount"
                type="text"
              />
              <ValidatedField
                label="Transaction Entry Type"
                id="transaction-entry-transactionEntryType"
                name="transactionEntryType"
                data-cy="transactionEntryType"
                type="select"
              >
                {transactionEntryTypesValues.map(transactionEntryTypes => (
                  <option value={transactionEntryTypes} key={transactionEntryTypes}>
                    {transactionEntryTypes}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField label="Description" id="transaction-entry-description" name="description" data-cy="description" type="text" />
              <ValidatedField
                id="transaction-entry-transactionAccount"
                name="transactionAccount"
                data-cy="transactionAccount"
                label="Transaction Account"
                type="select"
                required
              >
                <option value="" key="0" />
                {transactionAccounts
                  ? transactionAccounts.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.accountName}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="transaction-entry-accountTransaction"
                name="accountTransaction"
                data-cy="accountTransaction"
                label="Account Transaction"
                type="select"
              >
                <option value="" key="0" />
                {accountTransactions
                  ? accountTransactions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.referenceNumber}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/transaction-entry" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default TransactionEntryUpdate;
