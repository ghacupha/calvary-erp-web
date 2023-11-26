import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './sales-receipt.reducer';

export const SalesReceiptDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const salesReceiptEntity = useAppSelector(state => state.salesReceipt.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="salesReceiptDetailsHeading">Sales Receipt</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{salesReceiptEntity.id}</dd>
          <dt>
            <span id="salesReceiptTitle">Sales Receipt Title</span>
          </dt>
          <dd>{salesReceiptEntity.salesReceiptTitle}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{salesReceiptEntity.description}</dd>
          <dt>Transaction Class</dt>
          <dd>{salesReceiptEntity.transactionClass ? salesReceiptEntity.transactionClass.className : ''}</dd>
          <dt>Dealer</dt>
          <dd>{salesReceiptEntity.dealer ? salesReceiptEntity.dealer.name : ''}</dd>
          <dt>Transaction Item Entry</dt>
          <dd>
            {salesReceiptEntity.transactionItemEntries
              ? salesReceiptEntity.transactionItemEntries.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.description}</a>
                    {salesReceiptEntity.transactionItemEntries && i === salesReceiptEntity.transactionItemEntries.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/sales-receipt" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/sales-receipt/${salesReceiptEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default SalesReceiptDetail;
