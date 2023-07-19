import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IAccountTransaction } from 'app/shared/model/account-transaction.model';
import { getEntity, updateEntity, createEntity, reset } from './account-transaction.reducer';

export const AccountTransactionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const accountTransactionEntity = useAppSelector(state => state.accountTransaction.entity);
  const loading = useAppSelector(state => state.accountTransaction.loading);
  const updating = useAppSelector(state => state.accountTransaction.updating);
  const updateSuccess = useAppSelector(state => state.accountTransaction.updateSuccess);

  const handleClose = () => {
    navigate('/account-transaction' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...accountTransactionEntity,
      ...values,
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
          ...accountTransactionEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="calvaryErpApp.accountTransaction.home.createOrEditLabel" data-cy="AccountTransactionCreateUpdateHeading">
            <Translate contentKey="calvaryErpApp.accountTransaction.home.createOrEditLabel">Create or edit a AccountTransaction</Translate>
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
                  id="account-transaction-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('calvaryErpApp.accountTransaction.transactionDate')}
                id="account-transaction-transactionDate"
                name="transactionDate"
                data-cy="transactionDate"
                type="date"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('calvaryErpApp.accountTransaction.description')}
                id="account-transaction-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                label={translate('calvaryErpApp.accountTransaction.referenceNumber')}
                id="account-transaction-referenceNumber"
                name="referenceNumber"
                data-cy="referenceNumber"
                type="text"
                validate={{}}
              />
              <ValidatedField
                label={translate('calvaryErpApp.accountTransaction.wasProposed')}
                id="account-transaction-wasProposed"
                name="wasProposed"
                data-cy="wasProposed"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('calvaryErpApp.accountTransaction.wasPosted')}
                id="account-transaction-wasPosted"
                name="wasPosted"
                data-cy="wasPosted"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('calvaryErpApp.accountTransaction.wasDeleted')}
                id="account-transaction-wasDeleted"
                name="wasDeleted"
                data-cy="wasDeleted"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('calvaryErpApp.accountTransaction.wasApproved')}
                id="account-transaction-wasApproved"
                name="wasApproved"
                data-cy="wasApproved"
                check
                type="checkbox"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/account-transaction" replace color="info">
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

export default AccountTransactionUpdate;
