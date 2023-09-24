import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getTransactionAccounts } from 'app/entities/transaction-account/transaction-account.reducer';
import { ITransactionAccountType } from 'app/shared/model/transaction-account-type.model';
import { getEntities as getTransactionAccountTypes } from 'app/entities/transaction-account-type/transaction-account-type.reducer';
import { ITransactionCurrency } from 'app/shared/model/transaction-currency.model';
import { getEntities as getTransactionCurrencies } from 'app/entities/transaction-currency/transaction-currency.reducer';
import { ITransactionAccount } from 'app/shared/model/transaction-account.model';
import { getEntity, updateEntity, createEntity, reset } from './transaction-account.reducer';

export const TransactionAccountUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const transactionAccounts = useAppSelector(state => state.transactionAccount.entities);
  const transactionAccountTypes = useAppSelector(state => state.transactionAccountType.entities);
  const transactionCurrencies = useAppSelector(state => state.transactionCurrency.entities);
  const transactionAccountEntity = useAppSelector(state => state.transactionAccount.entity);
  const loading = useAppSelector(state => state.transactionAccount.loading);
  const updating = useAppSelector(state => state.transactionAccount.updating);
  const updateSuccess = useAppSelector(state => state.transactionAccount.updateSuccess);

  const handleClose = () => {
    navigate('/transaction-account' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getTransactionAccounts({}));
    dispatch(getTransactionAccountTypes({}));
    dispatch(getTransactionCurrencies({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...transactionAccountEntity,
      ...values,
      parentAccount: transactionAccounts.find(it => it.id.toString() === values.parentAccount.toString()),
      transactionAccountType: transactionAccountTypes.find(it => it.id.toString() === values.transactionAccountType.toString()),
      transactionCurrency: transactionCurrencies.find(it => it.id.toString() === values.transactionCurrency.toString()),
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
          ...transactionAccountEntity,
          parentAccount: transactionAccountEntity?.parentAccount?.id,
          transactionAccountType: transactionAccountEntity?.transactionAccountType?.id,
          transactionCurrency: transactionAccountEntity?.transactionCurrency?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="calvaryErpApp.transactionAccount.home.createOrEditLabel" data-cy="TransactionAccountCreateUpdateHeading">
            Create or edit a Transaction Account
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
                <ValidatedField name="id" required readOnly id="transaction-account-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Account Name"
                id="transaction-account-accountName"
                name="accountName"
                data-cy="accountName"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                label="Account Number"
                id="transaction-account-accountNumber"
                name="accountNumber"
                data-cy="accountNumber"
                type="text"
                validate={{}}
              />
              <ValidatedField
                label="Opening Balance"
                id="transaction-account-openingBalance"
                name="openingBalance"
                data-cy="openingBalance"
                type="text"
              />
              <ValidatedField
                id="transaction-account-parentAccount"
                name="parentAccount"
                data-cy="parentAccount"
                label="Parent Account"
                type="select"
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
              <ValidatedField
                id="transaction-account-transactionAccountType"
                name="transactionAccountType"
                data-cy="transactionAccountType"
                label="Transaction Account Type"
                type="select"
                required
              >
                <option value="" key="0" />
                {transactionAccountTypes
                  ? transactionAccountTypes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <ValidatedField
                id="transaction-account-transactionCurrency"
                name="transactionCurrency"
                data-cy="transactionCurrency"
                label="Transaction Currency"
                type="select"
                required
              >
                <option value="" key="0" />
                {transactionCurrencies
                  ? transactionCurrencies.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.code}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/transaction-account" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default TransactionAccountUpdate;
