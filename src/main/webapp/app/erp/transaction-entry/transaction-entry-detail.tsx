import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './transaction-entry.reducer';

export const TransactionEntryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const transactionEntryEntity = useAppSelector(state => state.transactionEntry.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="transactionEntryDetailsHeading">Transaction Entry</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{transactionEntryEntity.id}</dd>
          <dt>
            <span id="entryAmount">Entry Amount</span>
          </dt>
          <dd>{transactionEntryEntity.entryAmount}</dd>
          <dt>
            <span id="transactionEntryType">Transaction Entry Type</span>
          </dt>
          <dd>{transactionEntryEntity.transactionEntryType}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{transactionEntryEntity.description}</dd>
          <dt>
            <span id="wasProposed">Was Proposed</span>
          </dt>
          <dd>{transactionEntryEntity.wasProposed ? 'true' : 'false'}</dd>
          <dt>
            <span id="wasPosted">Was Posted</span>
          </dt>
          <dd>{transactionEntryEntity.wasPosted ? 'true' : 'false'}</dd>
          <dt>
            <span id="wasDeleted">Was Deleted</span>
          </dt>
          <dd>{transactionEntryEntity.wasDeleted ? 'true' : 'false'}</dd>
          <dt>
            <span id="wasApproved">Was Approved</span>
          </dt>
          <dd>{transactionEntryEntity.wasApproved ? 'true' : 'false'}</dd>
          <dt>Transaction Account</dt>
          <dd>{transactionEntryEntity.transactionAccount ? transactionEntryEntity.transactionAccount.accountName : ''}</dd>
          <dt>Account Transaction</dt>
          <dd>{transactionEntryEntity.accountTransaction ? transactionEntryEntity.accountTransaction.referenceNumber : ''}</dd>
        </dl>
        <Button tag={Link} to="/transaction-entry" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/transaction-entry/${transactionEntryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default TransactionEntryDetail;
