import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './transaction-item.reducer';

export const TransactionItemDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const transactionItemEntity = useAppSelector(state => state.transactionItem.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="transactionItemDetailsHeading">Transaction Item</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{transactionItemEntity.id}</dd>
          <dt>
            <span id="itemName">Item Name</span>
          </dt>
          <dd>{transactionItemEntity.itemName}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{transactionItemEntity.description}</dd>
          <dt>Transaction Class</dt>
          <dd>{transactionItemEntity.transactionClass ? transactionItemEntity.transactionClass.className : ''}</dd>
          <dt>Transaction Account</dt>
          <dd>{transactionItemEntity.transactionAccount ? transactionItemEntity.transactionAccount.accountName : ''}</dd>
        </dl>
        <Button tag={Link} to="/transaction-item" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/transaction-item/${transactionItemEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default TransactionItemDetail;
