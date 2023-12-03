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
import { IDealer } from 'app/shared/model/dealer.model';
import { getEntities as getDealers } from 'app/entities/dealer/dealer.reducer';
import { ITransactionItemEntry } from 'app/shared/model/transaction-item-entry.model';
import { getEntities as getTransactionItemEntries } from 'app/entities/transaction-item-entry/transaction-item-entry.reducer';
import { ISalesReceiptTitle } from 'app/shared/model/sales-receipt-title.model';
import { getEntities as getSalesReceiptTitles } from 'app/entities/sales-receipt-title/sales-receipt-title.reducer';
import { ISalesReceipt } from 'app/shared/model/sales-receipt.model';
import { getEntity, updateEntity, createEntity, reset } from './sales-receipt.reducer';

export const SalesReceiptUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const transactionClasses = useAppSelector(state => state.transactionClass.entities);
  const dealers = useAppSelector(state => state.dealer.entities);
  const transactionItemEntries = useAppSelector(state => state.transactionItemEntry.entities);
  const salesReceiptTitles = useAppSelector(state => state.salesReceiptTitle.entities);
  const salesReceiptEntity = useAppSelector(state => state.salesReceipt.entity);
  const loading = useAppSelector(state => state.salesReceipt.loading);
  const updating = useAppSelector(state => state.salesReceipt.updating);
  const updateSuccess = useAppSelector(state => state.salesReceipt.updateSuccess);

  const handleClose = () => {
    navigate('/sales-receipt' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getTransactionClasses({}));
    dispatch(getDealers({}));
    dispatch(getTransactionItemEntries({}));
    dispatch(getSalesReceiptTitles({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...salesReceiptEntity,
      ...values,
      transactionItemEntries: mapIdList(values.transactionItemEntries),
      transactionClass: transactionClasses.find(it => it.id.toString() === values.transactionClass.toString()),
      dealer: dealers.find(it => it.id.toString() === values.dealer.toString()),
      salesReceiptTitle: salesReceiptTitles.find(it => it.id.toString() === values.salesReceiptTitle.toString()),
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
          ...salesReceiptEntity,
          transactionClass: salesReceiptEntity?.transactionClass?.id,
          dealer: salesReceiptEntity?.dealer?.id,
          transactionItemEntries: salesReceiptEntity?.transactionItemEntries?.map(e => e.id.toString()),
          salesReceiptTitle: salesReceiptEntity?.salesReceiptTitle?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="calvaryErpApp.salesReceipt.home.createOrEditLabel" data-cy="SalesReceiptCreateUpdateHeading">
            Create or edit a Sales Receipt
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
                <ValidatedField name="id" required readOnly id="sales-receipt-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField label="Description" id="sales-receipt-description" name="description" data-cy="description" type="text" />
              <ValidatedField
                label="Transaction Date"
                id="sales-receipt-transactionDate"
                name="transactionDate"
                data-cy="transactionDate"
                type="date"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                label="Has Been Emailed"
                id="sales-receipt-hasBeenEmailed"
                name="hasBeenEmailed"
                data-cy="hasBeenEmailed"
                check
                type="checkbox"
              />
              <ValidatedField
                label="Has Been Proposed"
                id="sales-receipt-hasBeenProposed"
                name="hasBeenProposed"
                data-cy="hasBeenProposed"
                check
                type="checkbox"
              />
              <ValidatedField
                label="Should Be Emailed"
                id="sales-receipt-shouldBeEmailed"
                name="shouldBeEmailed"
                data-cy="shouldBeEmailed"
                check
                type="checkbox"
              />
              <ValidatedField
                id="sales-receipt-transactionClass"
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
              <ValidatedField id="sales-receipt-dealer" name="dealer" data-cy="dealer" label="Dealer" type="select" required>
                <option value="" key="0" />
                {dealers
                  ? dealers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <ValidatedField
                label="Transaction Item Entry"
                id="sales-receipt-transactionItemEntry"
                data-cy="transactionItemEntry"
                type="select"
                multiple
                name="transactionItemEntries"
              >
                <option value="" key="0" />
                {transactionItemEntries
                  ? transactionItemEntries.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.description}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="sales-receipt-salesReceiptTitle"
                name="salesReceiptTitle"
                data-cy="salesReceiptTitle"
                label="Sales Receipt Title"
                type="select"
                required
              >
                <option value="" key="0" />
                {salesReceiptTitles
                  ? salesReceiptTitles.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.receiptTitle}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/sales-receipt" replace color="info">
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

export default SalesReceiptUpdate;
