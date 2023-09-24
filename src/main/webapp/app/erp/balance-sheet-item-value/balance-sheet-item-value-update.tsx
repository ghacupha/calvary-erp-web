import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IBalanceSheetItemType } from 'app/shared/model/balance-sheet-item-type.model';
import { getEntities as getBalanceSheetItemTypes } from 'app/entities/balance-sheet-item-type/balance-sheet-item-type.reducer';
import { IBalanceSheetItemValue } from 'app/shared/model/balance-sheet-item-value.model';
import { getEntity, updateEntity, createEntity, reset } from './balance-sheet-item-value.reducer';

export const BalanceSheetItemValueUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const balanceSheetItemTypes = useAppSelector(state => state.balanceSheetItemType.entities);
  const balanceSheetItemValueEntity = useAppSelector(state => state.balanceSheetItemValue.entity);
  const loading = useAppSelector(state => state.balanceSheetItemValue.loading);
  const updating = useAppSelector(state => state.balanceSheetItemValue.updating);
  const updateSuccess = useAppSelector(state => state.balanceSheetItemValue.updateSuccess);

  const handleClose = () => {
    navigate('/balance-sheet-item-value' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getBalanceSheetItemTypes({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...balanceSheetItemValueEntity,
      ...values,
      itemType: balanceSheetItemTypes.find(it => it.id.toString() === values.itemType.toString()),
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
          ...balanceSheetItemValueEntity,
          itemType: balanceSheetItemValueEntity?.itemType?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="calvaryErpApp.balanceSheetItemValue.home.createOrEditLabel" data-cy="BalanceSheetItemValueCreateUpdateHeading">
            Create or edit a Balance Sheet Item Value
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
                <ValidatedField name="id" required readOnly id="balance-sheet-item-value-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Short Description"
                id="balance-sheet-item-value-shortDescription"
                name="shortDescription"
                data-cy="shortDescription"
                type="text"
              />
              <ValidatedField
                label="Effective Date"
                id="balance-sheet-item-value-effectiveDate"
                name="effectiveDate"
                data-cy="effectiveDate"
                type="date"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                label="Item Amount"
                id="balance-sheet-item-value-itemAmount"
                name="itemAmount"
                data-cy="itemAmount"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <ValidatedField
                id="balance-sheet-item-value-itemType"
                name="itemType"
                data-cy="itemType"
                label="Item Type"
                type="select"
                required
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
              <FormText>This field is required.</FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/balance-sheet-item-value" replace color="info">
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

export default BalanceSheetItemValueUpdate;
