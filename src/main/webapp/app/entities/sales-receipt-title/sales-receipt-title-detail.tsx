import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './sales-receipt-title.reducer';

export const SalesReceiptTitleDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const salesReceiptTitleEntity = useAppSelector(state => state.salesReceiptTitle.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="salesReceiptTitleDetailsHeading">Sales Receipt Title</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{salesReceiptTitleEntity.id}</dd>
          <dt>
            <span id="receiptTitle">Receipt Title</span>
          </dt>
          <dd>{salesReceiptTitleEntity.receiptTitle}</dd>
        </dl>
        <Button tag={Link} to="/sales-receipt-title" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/sales-receipt-title/${salesReceiptTitleEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default SalesReceiptTitleDetail;
