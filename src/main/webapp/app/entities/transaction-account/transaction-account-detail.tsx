import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './transaction-account.reducer';

export const TransactionAccountDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const transactionAccountEntity = useAppSelector(state => state.transactionAccount.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="transactionAccountDetailsHeading">
          <Translate contentKey="calvaryErpApp.transactionAccount.detail.title">TransactionAccount</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{transactionAccountEntity.id}</dd>
          <dt>
            <span id="accountName">
              <Translate contentKey="calvaryErpApp.transactionAccount.accountName">Account Name</Translate>
            </span>
          </dt>
          <dd>{transactionAccountEntity.accountName}</dd>
          <dt>
            <span id="accountNumber">
              <Translate contentKey="calvaryErpApp.transactionAccount.accountNumber">Account Number</Translate>
            </span>
          </dt>
          <dd>{transactionAccountEntity.accountNumber}</dd>
          <dt>
            <span id="transactionAccountType">
              <Translate contentKey="calvaryErpApp.transactionAccount.transactionAccountType">Transaction Account Type</Translate>
            </span>
          </dt>
          <dd>{transactionAccountEntity.transactionAccountType}</dd>
          <dt>
            <span id="openingBalance">
              <Translate contentKey="calvaryErpApp.transactionAccount.openingBalance">Opening Balance</Translate>
            </span>
          </dt>
          <dd>{transactionAccountEntity.openingBalance}</dd>
          <dt>
            <Translate contentKey="calvaryErpApp.transactionAccount.parentAccount">Parent Account</Translate>
          </dt>
          <dd>{transactionAccountEntity.parentAccount ? transactionAccountEntity.parentAccount.accountName : ''}</dd>
        </dl>
        <Button tag={Link} to="/transaction-account" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/transaction-account/${transactionAccountEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TransactionAccountDetail;
