import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import {getEntity, postTransaction} from './account-transaction.reducer';

export const AccountTransactionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const postAction = () => {
    dispatch(postTransaction(accountTransactionEntity));
  };

  const accountTransactionEntity = useAppSelector(state => state.accountTransaction.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="accountTransactionDetailsHeading">
          <Translate contentKey="calvaryErpApp.accountTransaction.detail.title">AccountTransaction</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{accountTransactionEntity.id}</dd>
          <dt>
            <span id="transactionDate">
              <Translate contentKey="calvaryErpApp.accountTransaction.transactionDate">Transaction Date</Translate>
            </span>
          </dt>
          <dd>
            {accountTransactionEntity.transactionDate ? (
              <TextFormat value={accountTransactionEntity.transactionDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="description">
              <Translate contentKey="calvaryErpApp.accountTransaction.description">Description</Translate>
            </span>
          </dt>
          <dd>{accountTransactionEntity.description}</dd>
          <dt>
            <span id="referenceNumber">
              <Translate contentKey="calvaryErpApp.accountTransaction.referenceNumber">Reference Number</Translate>
            </span>
          </dt>
          <dd>{accountTransactionEntity.referenceNumber}</dd>
          <dt>
            <span id="wasProposed">
              <Translate contentKey="calvaryErpApp.accountTransaction.wasProposed">Was Proposed</Translate>
            </span>
          </dt>
          <dd>{accountTransactionEntity.wasProposed ? 'true' : 'false'}</dd>
          <dt>
            <span id="wasPosted">
              <Translate contentKey="calvaryErpApp.accountTransaction.wasPosted">Was Posted</Translate>
            </span>
          </dt>
          <dd>{accountTransactionEntity.wasPosted ? 'true' : 'false'}</dd>
          <dt>
            <span id="wasDeleted">
              <Translate contentKey="calvaryErpApp.accountTransaction.wasDeleted">Was Deleted</Translate>
            </span>
          </dt>
          <dd>{accountTransactionEntity.wasDeleted ? 'true' : 'false'}</dd>
          <dt>
            <span id="wasApproved">
              <Translate contentKey="calvaryErpApp.accountTransaction.wasApproved">Was Approved</Translate>
            </span>
          </dt>
          <dd>{accountTransactionEntity.wasApproved ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/account-transaction" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/account-transaction/${accountTransactionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
        <Button tag={Link} to={`/account-transaction/${accountTransactionEntity.id}/post`}replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.post">Post</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AccountTransactionDetail;
