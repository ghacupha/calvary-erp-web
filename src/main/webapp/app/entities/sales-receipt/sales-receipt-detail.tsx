import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
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
            <span id="description">Description</span>
          </dt>
          <dd>{salesReceiptEntity.description}</dd>
          <dt>
            <span id="transactionDate">Transaction Date</span>
          </dt>
          <dd>
            {salesReceiptEntity.transactionDate ? (
              <TextFormat value={salesReceiptEntity.transactionDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="hasBeenEmailed">Has Been Emailed</span>
          </dt>
          <dd>{salesReceiptEntity.hasBeenEmailed ? 'true' : 'false'}</dd>
          <dt>
            <span id="hasBeenProposed">Has Been Proposed</span>
          </dt>
          <dd>{salesReceiptEntity.hasBeenProposed ? 'true' : 'false'}</dd>
          <dt>
            <span id="shouldBeEmailed">Should Be Emailed</span>
          </dt>
          <dd>{salesReceiptEntity.shouldBeEmailed ? 'true' : 'false'}</dd>
          <dt>Transaction Class</dt>
          <dd>{salesReceiptEntity.transactionClass ? salesReceiptEntity.transactionClass.className : ''}</dd>
          <dt>Dealer</dt>
          <dd>{salesReceiptEntity.dealer ? salesReceiptEntity.dealer.name : ''}</dd>
          <dt>Sales Receipt Title</dt>
          <dd>{salesReceiptEntity.salesReceiptTitle ? salesReceiptEntity.salesReceiptTitle.receiptTitle : ''}</dd>
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
