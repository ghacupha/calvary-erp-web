import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './transaction-item-amount.reducer';

export const TransactionItemAmountDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const transactionItemAmountEntity = useAppSelector(state => state.transactionItemAmount.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="transactionItemAmountDetailsHeading">Transaction Item Amount</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{transactionItemAmountEntity.id}</dd>
          <dt>
            <span id="transactionItemAmount">Transaction Item Amount</span>
          </dt>
          <dd>{transactionItemAmountEntity.transactionItemAmount}</dd>
          <dt>Transaction Item</dt>
          <dd>{transactionItemAmountEntity.transactionItem ? transactionItemAmountEntity.transactionItem.itemName : ''}</dd>
        </dl>
        <Button tag={Link} to="/transaction-item-amount" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/transaction-item-amount/${transactionItemAmountEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default TransactionItemAmountDetail;
