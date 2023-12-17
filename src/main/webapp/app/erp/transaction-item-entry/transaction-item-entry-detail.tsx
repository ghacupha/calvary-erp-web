import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './transaction-item-entry.reducer';

export const TransactionItemEntryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const transactionItemEntryEntity = useAppSelector(state => state.transactionItemEntry.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="transactionItemEntryDetailsHeading">Transaction Item Entry</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{transactionItemEntryEntity.id}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{transactionItemEntryEntity.description}</dd>
          <dt>
            <span id="itemAmount">Item Amount</span>
          </dt>
          <dd>{transactionItemEntryEntity.itemAmount}</dd>
          <dt>Transaction Item</dt>
          <dd>{transactionItemEntryEntity.transactionItem ? transactionItemEntryEntity.transactionItem.itemName : ''}</dd>
          <dt>Sales Receipt</dt>
          <dd>{transactionItemEntryEntity.salesReceipt ? transactionItemEntryEntity.salesReceipt.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/transaction-item-entry" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/transaction-item-entry/${transactionItemEntryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default TransactionItemEntryDetail;
