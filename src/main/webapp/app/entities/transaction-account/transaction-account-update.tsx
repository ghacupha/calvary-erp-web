import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getTransactionAccounts } from 'app/entities/transaction-account/transaction-account.reducer';
import { ITransactionAccount } from 'app/shared/model/transaction-account.model';
import { TransactionAccountType } from 'app/shared/model/enumerations/transaction-account-type.model';
import { getEntity, updateEntity, createEntity, reset } from './transaction-account.reducer';

export const TransactionAccountUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const transactionAccounts = useAppSelector(state => state.transactionAccount.entities);
  const transactionAccountEntity = useAppSelector(state => state.transactionAccount.entity);
  const loading = useAppSelector(state => state.transactionAccount.loading);
  const updating = useAppSelector(state => state.transactionAccount.updating);
  const updateSuccess = useAppSelector(state => state.transactionAccount.updateSuccess);
  const transactionAccountTypeValues = Object.keys(TransactionAccountType);

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
          transactionAccountType: 'ASSET',
          ...transactionAccountEntity,
          parentAccount: transactionAccountEntity?.parentAccount?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="calvaryErpApp.transactionAccount.home.createOrEditLabel" data-cy="TransactionAccountCreateUpdateHeading">
            <Translate contentKey="calvaryErpApp.transactionAccount.home.createOrEditLabel">Create or edit a TransactionAccount</Translate>
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
                  id="transaction-account-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label="Account Name"
                id="transaction-account-accountName"
                name="accountName"
                data-cy="accountName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
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
                label="Transaction Account Type"
                id="transaction-account-transactionAccountType"
                name="transactionAccountType"
                data-cy="transactionAccountType"
                type="select"
              >
                {transactionAccountTypeValues.map(transactionAccountType => (
                  <option value={transactionAccountType} key={transactionAccountType}>
                    {transactionAccountType}
                  </option>
                ))}
              </ValidatedField>
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/transaction-account" replace color="info">
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

export default TransactionAccountUpdate;
