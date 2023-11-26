import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ITransactionClass } from 'app/shared/model/transaction-class.model';
import { getEntities as getTransactionClasses } from 'app/entities/transaction-class/transaction-class.reducer';
import { ITransactionAccount } from 'app/shared/model/transaction-account.model';
import { getEntities as getTransactionAccounts } from 'app/entities/transaction-account/transaction-account.reducer';
import { ITransactionItem } from 'app/shared/model/transaction-item.model';
import { getEntity, updateEntity, createEntity, reset } from './transaction-item.reducer';

export const TransactionItemUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const transactionClasses = useAppSelector(state => state.transactionClass.entities);
  const transactionAccounts = useAppSelector(state => state.transactionAccount.entities);
  const transactionItemEntity = useAppSelector(state => state.transactionItem.entity);
  const loading = useAppSelector(state => state.transactionItem.loading);
  const updating = useAppSelector(state => state.transactionItem.updating);
  const updateSuccess = useAppSelector(state => state.transactionItem.updateSuccess);

  const handleClose = () => {
    navigate('/transaction-item' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getTransactionClasses({}));
    dispatch(getTransactionAccounts({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...transactionItemEntity,
      ...values,
      transactionClass: transactionClasses.find(it => it.id.toString() === values.transactionClass.toString()),
      transactionAccount: transactionAccounts.find(it => it.id.toString() === values.transactionAccount.toString()),
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
          ...transactionItemEntity,
          transactionClass: transactionItemEntity?.transactionClass?.id,
          transactionAccount: transactionItemEntity?.transactionAccount?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="calvaryErpApp.transactionItem.home.createOrEditLabel" data-cy="TransactionItemCreateUpdateHeading">
            Create or edit a Transaction Item
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
                <ValidatedField name="id" required readOnly id="transaction-item-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Item Name"
                id="transaction-item-itemName"
                name="itemName"
                data-cy="itemName"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField label="Description" id="transaction-item-description" name="description" data-cy="description" type="text" />
              <ValidatedField
                id="transaction-item-transactionClass"
                name="transactionClass"
                data-cy="transactionClass"
                label="Transaction Class"
                type="select"
              >
                <option value="" key="0" />
                {transactionClasses
                  ? transactionClasses.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.className}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="transaction-item-transactionAccount"
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
              <FormText>This field is required.</FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/transaction-item" replace color="info">
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

export default TransactionItemUpdate;
