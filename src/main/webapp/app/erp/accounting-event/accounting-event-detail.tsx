import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './accounting-event.reducer';

export const AccountingEventDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const accountingEventEntity = useAppSelector(state => state.accountingEvent.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="accountingEventDetailsHeading">Accounting Event</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{accountingEventEntity.id}</dd>
          <dt>
            <span id="eventDate">Event Date</span>
          </dt>
          <dd>
            {accountingEventEntity.eventDate ? (
              <TextFormat value={accountingEventEntity.eventDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>Event Type</dt>
          <dd>{accountingEventEntity.eventType ? accountingEventEntity.eventType.name : ''}</dd>
          <dt>Dealer</dt>
          <dd>{accountingEventEntity.dealer ? accountingEventEntity.dealer.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/accounting-event" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/accounting-event/${accountingEventEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default AccountingEventDetail;
