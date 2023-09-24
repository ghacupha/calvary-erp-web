import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ITransactionAccount } from 'app/shared/model/transaction-account.model';
import { getEntities as getTransactionAccounts } from 'app/entities/transaction-account/transaction-account.reducer';
import { getEntities as getBalanceSheetItemTypes } from 'app/entities/balance-sheet-item-type/balance-sheet-item-type.reducer';
import { IBalanceSheetItemType } from 'app/shared/model/balance-sheet-item-type.model';
import { getEntity, updateEntity, createEntity, reset } from './balance-sheet-item-type.reducer';

export const BalanceSheetItemTypeUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const transactionAccounts = useAppSelector(state => state.transactionAccount.entities);
  const balanceSheetItemTypes = useAppSelector(state => state.balanceSheetItemType.entities);
  const balanceSheetItemTypeEntity = useAppSelector(state => state.balanceSheetItemType.entity);
  const loading = useAppSelector(state => state.balanceSheetItemType.loading);
  const updating = useAppSelector(state => state.balanceSheetItemType.updating);
  const updateSuccess = useAppSelector(state => state.balanceSheetItemType.updateSuccess);

  const handleClose = () => {
    navigate('/balance-sheet-item-type');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }

    dispatch(getTransactionAccounts({}));
    dispatch(getBalanceSheetItemTypes({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...balanceSheetItemTypeEntity,
      ...values,
      transactionAccount: transactionAccounts.find(it => it.id.toString() === values.transactionAccount.toString()),
      parentItem: balanceSheetItemTypes.find(it => it.id.toString() === values.parentItem.toString()),
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
          ...balanceSheetItemTypeEntity,
          transactionAccount: balanceSheetItemTypeEntity?.transactionAccount?.id,
          parentItem: balanceSheetItemTypeEntity?.parentItem?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="calvaryErpApp.balanceSheetItemType.home.createOrEditLabel" data-cy="BalanceSheetItemTypeCreateUpdateHeading">
            Create or edit a Balance Sheet Item Type
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
                <ValidatedField name="id" required readOnly id="balance-sheet-item-type-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Item Sequence"
                id="balance-sheet-item-type-itemSequence"
                name="itemSequence"
                data-cy="itemSequence"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <ValidatedField
                label="Item Number"
                id="balance-sheet-item-type-itemNumber"
                name="itemNumber"
                data-cy="itemNumber"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                label="Short Description"
                id="balance-sheet-item-type-shortDescription"
                name="shortDescription"
                data-cy="shortDescription"
                type="text"
              />
              <ValidatedField
                id="balance-sheet-item-type-transactionAccount"
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
              <ValidatedField
                id="balance-sheet-item-type-parentItem"
                name="parentItem"
                data-cy="parentItem"
                label="Parent Item"
                type="select"
              >
                <option value="" key="0" />
                {balanceSheetItemTypes
                  ? balanceSheetItemTypes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.itemNumber}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/balance-sheet-item-type" replace color="info">
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

export default BalanceSheetItemTypeUpdate;
