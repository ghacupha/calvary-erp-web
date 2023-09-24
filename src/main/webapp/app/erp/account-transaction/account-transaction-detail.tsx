import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './account-transaction.reducer';

export const AccountTransactionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const accountTransactionEntity = useAppSelector(state => state.accountTransaction.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="accountTransactionDetailsHeading">Account Transaction</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{accountTransactionEntity.id}</dd>
          <dt>
            <span id="transactionDate">Transaction Date</span>
          </dt>
          <dd>
            {accountTransactionEntity.transactionDate ? (
              <TextFormat value={accountTransactionEntity.transactionDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{accountTransactionEntity.description}</dd>
          <dt>
            <span id="referenceNumber">Reference Number</span>
          </dt>
          <dd>{accountTransactionEntity.referenceNumber}</dd>
          <dt>
            <span id="wasProposed">Was Proposed</span>
          </dt>
          <dd>{accountTransactionEntity.wasProposed ? 'true' : 'false'}</dd>
          <dt>
            <span id="wasPosted">Was Posted</span>
          </dt>
          <dd>{accountTransactionEntity.wasPosted ? 'true' : 'false'}</dd>
          <dt>
            <span id="wasDeleted">Was Deleted</span>
          </dt>
          <dd>{accountTransactionEntity.wasDeleted ? 'true' : 'false'}</dd>
          <dt>
            <span id="wasApproved">Was Approved</span>
          </dt>
          <dd>{accountTransactionEntity.wasApproved ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/account-transaction" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/account-transaction/${accountTransactionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default AccountTransactionDetail;
