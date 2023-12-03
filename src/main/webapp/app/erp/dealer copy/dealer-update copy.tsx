import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IDealerType } from 'app/shared/model/dealer-type.model';
import { getEntities as getDealerTypes } from 'app/entities/dealer-type/dealer-type.reducer';
import { IDealer } from 'app/shared/model/dealer.model';
import { getEntity, updateEntity, createEntity, reset } from './dealer.reducer';

export const DealerUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const dealerTypes = useAppSelector(state => state.dealerType.entities);
  const dealerEntity = useAppSelector(state => state.dealer.entity);
  const loading = useAppSelector(state => state.dealer.loading);
  const updating = useAppSelector(state => state.dealer.updating);
  const updateSuccess = useAppSelector(state => state.dealer.updateSuccess);

  const handleClose = () => {
    navigate('/dealer' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getDealerTypes({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...dealerEntity,
      ...values,
      dealerType: dealerTypes.find(it => it.id.toString() === values.dealerType.toString()),
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
          ...dealerEntity,
          dealerType: dealerEntity?.dealerType?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="calvaryErpApp.dealer.home.createOrEditLabel" data-cy="DealerCreateUpdateHeading">
            Create or edit a Dealer
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="dealer-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Name"
                id="dealer-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField label="Main Email" id="dealer-mainEmail" name="mainEmail" data-cy="mainEmail" type="text" />
              <ValidatedField id="dealer-dealerType" name="dealerType" data-cy="dealerType" label="Dealer Type" type="select" required>
                <option value="" key="0" />
                {dealerTypes
                  ? dealerTypes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/dealer" replace color="info">
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

export default DealerUpdate;
