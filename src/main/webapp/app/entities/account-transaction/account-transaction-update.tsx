import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
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
            Create or edit a Account Transaction
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
                <ValidatedField name="id" required readOnly id="account-transaction-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Transaction Date"
                id="account-transaction-transactionDate"
                name="transactionDate"
                data-cy="transactionDate"
                type="date"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                label="Description"
                id="account-transaction-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                label="Reference Number"
                id="account-transaction-referenceNumber"
                name="referenceNumber"
                data-cy="referenceNumber"
                type="text"
                validate={{}}
              />
              <ValidatedField
                label="Was Proposed"
                id="account-transaction-wasProposed"
                name="wasProposed"
                data-cy="wasProposed"
                check
                type="checkbox"
              />
              <ValidatedField
                label="Was Posted"
                id="account-transaction-wasPosted"
                name="wasPosted"
                data-cy="wasPosted"
                check
                type="checkbox"
              />
              <ValidatedField
                label="Was Deleted"
                id="account-transaction-wasDeleted"
                name="wasDeleted"
                data-cy="wasDeleted"
                check
                type="checkbox"
              />
              <ValidatedField
                label="Was Approved"
                id="account-transaction-wasApproved"
                name="wasApproved"
                data-cy="wasApproved"
                check
                type="checkbox"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/account-transaction" replace color="info">
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

export default AccountTransactionUpdate;
