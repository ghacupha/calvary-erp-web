import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
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
        <h2 data-cy="transactionAccountDetailsHeading">Transaction Account</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{transactionAccountEntity.id}</dd>
          <dt>
            <span id="accountName">Account Name</span>
          </dt>
          <dd>{transactionAccountEntity.accountName}</dd>
          <dt>
            <span id="accountNumber">Account Number</span>
          </dt>
          <dd>{transactionAccountEntity.accountNumber}</dd>
          <dt>
            <span id="openingBalance">Opening Balance</span>
          </dt>
          <dd>{transactionAccountEntity.openingBalance}</dd>
          <dt>Parent Account</dt>
          <dd>{transactionAccountEntity.parentAccount ? transactionAccountEntity.parentAccount.accountName : ''}</dd>
          <dt>Transaction Account Type</dt>
          <dd>{transactionAccountEntity.transactionAccountType ? transactionAccountEntity.transactionAccountType.name : ''}</dd>
          <dt>Transaction Currency</dt>
          <dd>{transactionAccountEntity.transactionCurrency ? transactionAccountEntity.transactionCurrency.code : ''}</dd>
        </dl>
        <Button tag={Link} to="/transaction-account" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/transaction-account/${transactionAccountEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default TransactionAccountDetail;
