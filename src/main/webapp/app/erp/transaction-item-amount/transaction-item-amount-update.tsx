import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ITransactionItem } from 'app/shared/model/transaction-item.model';
import { getEntities as getTransactionItems } from 'app/entities/transaction-item/transaction-item.reducer';
import { ITransactionItemAmount } from 'app/shared/model/transaction-item-amount.model';
import { getEntity, updateEntity, createEntity, reset } from './transaction-item-amount.reducer';

export const TransactionItemAmountUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const transactionItems = useAppSelector(state => state.transactionItem.entities);
  const transactionItemAmountEntity = useAppSelector(state => state.transactionItemAmount.entity);
  const loading = useAppSelector(state => state.transactionItemAmount.loading);
  const updating = useAppSelector(state => state.transactionItemAmount.updating);
  const updateSuccess = useAppSelector(state => state.transactionItemAmount.updateSuccess);

  const handleClose = () => {
    navigate('/transaction-item-amount');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }

    dispatch(getTransactionItems({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...transactionItemAmountEntity,
      ...values,
      transactionItem: transactionItems.find(it => it.id.toString() === values.transactionItem.toString()),
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
          ...transactionItemAmountEntity,
          transactionItem: transactionItemAmountEntity?.transactionItem?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="calvaryErpApp.transactionItemAmount.home.createOrEditLabel" data-cy="TransactionItemAmountCreateUpdateHeading">
            Create or edit a Transaction Item Amount
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
                <ValidatedField name="id" required readOnly id="transaction-item-amount-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Transaction Item Amount"
                id="transaction-item-amount-transactionItemAmount"
                name="transactionItemAmount"
                data-cy="transactionItemAmount"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  min: { value: 0, message: 'This field should be at least 0.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <ValidatedField
                id="transaction-item-amount-transactionItem"
                name="transactionItem"
                data-cy="transactionItem"
                label="Transaction Item"
                type="select"
                required
              >
                <option value="" key="0" />
                {transactionItems
                  ? transactionItems.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.itemName}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/transaction-item-amount" replace color="info">
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

export default TransactionItemAmountUpdate;
