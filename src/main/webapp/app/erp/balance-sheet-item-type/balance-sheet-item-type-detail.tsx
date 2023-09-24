import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './balance-sheet-item-type.reducer';

export const BalanceSheetItemTypeDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const balanceSheetItemTypeEntity = useAppSelector(state => state.balanceSheetItemType.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="balanceSheetItemTypeDetailsHeading">Balance Sheet Item Type</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{balanceSheetItemTypeEntity.id}</dd>
          <dt>
            <span id="itemSequence">Item Sequence</span>
          </dt>
          <dd>{balanceSheetItemTypeEntity.itemSequence}</dd>
          <dt>
            <span id="itemNumber">Item Number</span>
          </dt>
          <dd>{balanceSheetItemTypeEntity.itemNumber}</dd>
          <dt>
            <span id="shortDescription">Short Description</span>
          </dt>
          <dd>{balanceSheetItemTypeEntity.shortDescription}</dd>
          <dt>Transaction Account</dt>
          <dd>{balanceSheetItemTypeEntity.transactionAccount ? balanceSheetItemTypeEntity.transactionAccount.accountName : ''}</dd>
          <dt>Parent Item</dt>
          <dd>{balanceSheetItemTypeEntity.parentItem ? balanceSheetItemTypeEntity.parentItem.itemNumber : ''}</dd>
        </dl>
        <Button tag={Link} to="/balance-sheet-item-type" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/balance-sheet-item-type/${balanceSheetItemTypeEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default BalanceSheetItemTypeDetail;
