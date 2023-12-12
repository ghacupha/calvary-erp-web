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
import { ITransactionItemEntry } from 'app/shared/model/transaction-item-entry.model';
import { getEntity, updateEntity, createEntity, reset } from './transaction-item-entry.reducer';
import TransactionItemAutocomplete from 'app/erp/auto-complete/transaction-items.autocomplete';

export const TransactionItemEntryUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  // const transactionItems = useAppSelector(state => state.transactionItem.entities);
  const transactionItemEntryEntity = useAppSelector(state => state.transactionItemEntry.entity);
  const loading = useAppSelector(state => state.transactionItemEntry.loading);
  const updating = useAppSelector(state => state.transactionItemEntry.updating);
  const updateSuccess = useAppSelector(state => state.transactionItemEntry.updateSuccess);

  const transactionItemSelected = useAppSelector(state => state.transactionItem.entity);

  const [transactionItem, setTransactionItem] = useState<ITransactionItem>();

  const handleClose = () => {
    // TODO Navigate to current parent instance
    navigate('/transaction-item-entry');
  };

  const handleTransactionItemSelectedEvent = pickedItem => {
    if (pickedItem) {
      // setTransactionItem(pickedItem);
    }
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
      ...transactionItemEntryEntity,
      ...values,
      transactionItem: transactionItemSelected,
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
          ...transactionItemEntryEntity,
          transactionItem: transactionItemEntryEntity?.transactionItem?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="calvaryErpApp.transactionItemEntry.home.createOrEditLabel" data-cy="TransactionItemEntryCreateUpdateHeading">
            Create or edit a Transaction Item Entry
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
                <ValidatedField name="id" required readOnly id="transaction-item-entry-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Description"
                id="transaction-item-entry-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                label="Item Amount"
                id="transaction-item-entry-itemAmount"
                name="itemAmount"
                data-cy="itemAmount"
                type="text"
              />
              {/*<ValidatedField
                id="transaction-item-entry-transactionItem"
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
              </ValidatedField>*/}
              <TransactionItemAutocomplete onSelectEntity={handleTransactionItemSelectedEvent} />
              <FormText>This field is required.</FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/transaction-item-entry" replace color="info">
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

export default TransactionItemEntryUpdate;
