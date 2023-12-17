import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './transfer-item-entry.reducer';

export const TransferItemEntryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const transferItemEntryEntity = useAppSelector(state => state.transferItemEntry.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="transferItemEntryDetailsHeading">Transfer Item Entry</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{transferItemEntryEntity.id}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{transferItemEntryEntity.description}</dd>
          <dt>
            <span id="itemAmount">Item Amount</span>
          </dt>
          <dd>{transferItemEntryEntity.itemAmount}</dd>
          <dt>Transaction Item</dt>
          <dd>{transferItemEntryEntity.transactionItem ? transferItemEntryEntity.transactionItem.itemName : ''}</dd>
          <dt>Sales Receipt</dt>
          <dd>{transferItemEntryEntity.salesReceipt ? transferItemEntryEntity.salesReceipt.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/transfer-item-entry" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/transfer-item-entry/${transferItemEntryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default TransferItemEntryDetail;
