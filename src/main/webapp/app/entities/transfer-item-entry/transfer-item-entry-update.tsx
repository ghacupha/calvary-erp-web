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
import { ISalesReceipt } from 'app/shared/model/sales-receipt.model';
import { getEntities as getSalesReceipts } from 'app/entities/sales-receipt/sales-receipt.reducer';
import { ITransferItemEntry } from 'app/shared/model/transfer-item-entry.model';
import { getEntity, updateEntity, createEntity, reset } from './transfer-item-entry.reducer';

export const TransferItemEntryUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const transactionItems = useAppSelector(state => state.transactionItem.entities);
  const salesReceipts = useAppSelector(state => state.salesReceipt.entities);
  const transferItemEntryEntity = useAppSelector(state => state.transferItemEntry.entity);
  const loading = useAppSelector(state => state.transferItemEntry.loading);
  const updating = useAppSelector(state => state.transferItemEntry.updating);
  const updateSuccess = useAppSelector(state => state.transferItemEntry.updateSuccess);

  const handleClose = () => {
    navigate('/transfer-item-entry');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }

    dispatch(getTransactionItems({}));
    dispatch(getSalesReceipts({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...transferItemEntryEntity,
      ...values,
      transactionItem: transactionItems.find(it => it.id.toString() === values.transactionItem.toString()),
      salesReceipt: salesReceipts.find(it => it.id.toString() === values.salesReceipt.toString()),
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
          ...transferItemEntryEntity,
          transactionItem: transferItemEntryEntity?.transactionItem?.id,
          salesReceipt: transferItemEntryEntity?.salesReceipt?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="calvaryErpApp.transferItemEntry.home.createOrEditLabel" data-cy="TransferItemEntryCreateUpdateHeading">
            Create or edit a Transfer Item Entry
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
                <ValidatedField name="id" required readOnly id="transfer-item-entry-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Description"
                id="transfer-item-entry-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                label="Item Amount"
                id="transfer-item-entry-itemAmount"
                name="itemAmount"
                data-cy="itemAmount"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <ValidatedField
                id="transfer-item-entry-transactionItem"
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
              <ValidatedField
                id="transfer-item-entry-salesReceipt"
                name="salesReceipt"
                data-cy="salesReceipt"
                label="Sales Receipt"
                type="select"
                required
              >
                <option value="" key="0" />
                {salesReceipts
                  ? salesReceipts.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/transfer-item-entry" replace color="info">
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

export default TransferItemEntryUpdate;
