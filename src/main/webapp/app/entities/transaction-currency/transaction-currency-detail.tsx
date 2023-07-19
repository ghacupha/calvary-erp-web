import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

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
        <h2 data-cy="transactionCurrencyDetailsHeading">
          <Translate contentKey="calvaryErpApp.transactionCurrency.detail.title">TransactionCurrency</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{transactionCurrencyEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="calvaryErpApp.transactionCurrency.name">Name</Translate>
            </span>
          </dt>
          <dd>{transactionCurrencyEntity.name}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="calvaryErpApp.transactionCurrency.code">Code</Translate>
            </span>
          </dt>
          <dd>{transactionCurrencyEntity.code}</dd>
        </dl>
        <Button tag={Link} to="/transaction-currency" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/transaction-currency/${transactionCurrencyEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TransactionCurrencyDetail;
