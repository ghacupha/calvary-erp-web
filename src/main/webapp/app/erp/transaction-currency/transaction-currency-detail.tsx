import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './transaction-currency.reducer';

export const TransactionCurrencyDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const transactionCurrencyEntity = useAppSelector(state => state.transactionCurrency.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="transactionCurrencyDetailsHeading">Transaction Currency</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{transactionCurrencyEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{transactionCurrencyEntity.name}</dd>
          <dt>
            <span id="code">Code</span>
          </dt>
          <dd>{transactionCurrencyEntity.code}</dd>
        </dl>
        <Button tag={Link} to="/transaction-currency" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/transaction-currency/${transactionCurrencyEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default TransactionCurrencyDetail;
