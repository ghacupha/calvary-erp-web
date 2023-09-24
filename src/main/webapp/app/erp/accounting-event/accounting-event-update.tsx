import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IEventType } from 'app/shared/model/event-type.model';
import { getEntities as getEventTypes } from 'app/entities/event-type/event-type.reducer';
import { IDealer } from 'app/shared/model/dealer.model';
import { getEntities as getDealers } from 'app/entities/dealer/dealer.reducer';
import { IAccountingEvent } from 'app/shared/model/accounting-event.model';
import { getEntity, updateEntity, createEntity, reset } from './accounting-event.reducer';

export const AccountingEventUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const eventTypes = useAppSelector(state => state.eventType.entities);
  const dealers = useAppSelector(state => state.dealer.entities);
  const accountingEventEntity = useAppSelector(state => state.accountingEvent.entity);
  const loading = useAppSelector(state => state.accountingEvent.loading);
  const updating = useAppSelector(state => state.accountingEvent.updating);
  const updateSuccess = useAppSelector(state => state.accountingEvent.updateSuccess);

  const handleClose = () => {
    navigate('/accounting-event' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getEventTypes({}));
    dispatch(getDealers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...accountingEventEntity,
      ...values,
      eventType: eventTypes.find(it => it.id.toString() === values.eventType.toString()),
      dealer: dealers.find(it => it.id.toString() === values.dealer.toString()),
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
          ...accountingEventEntity,
          eventType: accountingEventEntity?.eventType?.id,
          dealer: accountingEventEntity?.dealer?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="calvaryErpApp.accountingEvent.home.createOrEditLabel" data-cy="AccountingEventCreateUpdateHeading">
            Create or edit a Accounting Event
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
                <ValidatedField name="id" required readOnly id="accounting-event-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Event Date"
                id="accounting-event-eventDate"
                name="eventDate"
                data-cy="eventDate"
                type="date"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField id="accounting-event-eventType" name="eventType" data-cy="eventType" label="Event Type" type="select">
                <option value="" key="0" />
                {eventTypes
                  ? eventTypes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="accounting-event-dealer" name="dealer" data-cy="dealer" label="Dealer" type="select" required>
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/accounting-event" replace color="info">
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

export default AccountingEventUpdate;
