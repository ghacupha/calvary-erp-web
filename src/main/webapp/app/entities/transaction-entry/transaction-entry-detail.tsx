import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

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
        <h2 data-cy="transactionEntryDetailsHeading">
          <Translate contentKey="calvaryErpApp.transactionEntry.detail.title">TransactionEntry</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{transactionEntryEntity.id}</dd>
          <dt>
            <span id="entryAmount">
              <Translate contentKey="calvaryErpApp.transactionEntry.entryAmount">Entry Amount</Translate>
            </span>
          </dt>
          <dd>{transactionEntryEntity.entryAmount}</dd>
          <dt>
            <span id="transactionEntryType">
              <Translate contentKey="calvaryErpApp.transactionEntry.transactionEntryType">Transaction Entry Type</Translate>
            </span>
          </dt>
          <dd>{transactionEntryEntity.transactionEntryType}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="calvaryErpApp.transactionEntry.description">Description</Translate>
            </span>
          </dt>
          <dd>{transactionEntryEntity.description}</dd>
          <dt>
            <Translate contentKey="calvaryErpApp.transactionEntry.transactionAccount">Transaction Account</Translate>
          </dt>
          <dd>{transactionEntryEntity.transactionAccount ? transactionEntryEntity.transactionAccount.accountName : ''}</dd>
          <dt>
            <Translate contentKey="calvaryErpApp.transactionEntry.accountTransaction">Account Transaction</Translate>
          </dt>
          <dd>{transactionEntryEntity.accountTransaction ? transactionEntryEntity.accountTransaction.referenceNumber : ''}</dd>
        </dl>
        <Button tag={Link} to="/transaction-entry" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/transaction-entry/${transactionEntryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TransactionEntryDetail;
