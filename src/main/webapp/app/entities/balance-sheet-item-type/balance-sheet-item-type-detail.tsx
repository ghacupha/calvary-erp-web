import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

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
        <h2 data-cy="balanceSheetItemTypeDetailsHeading">
          <Translate contentKey="calvaryErpApp.balanceSheetItemType.detail.title">BalanceSheetItemType</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{balanceSheetItemTypeEntity.id}</dd>
          <dt>
            <span id="itemSequence">
              <Translate contentKey="calvaryErpApp.balanceSheetItemType.itemSequence">Item Sequence</Translate>
            </span>
          </dt>
          <dd>{balanceSheetItemTypeEntity.itemSequence}</dd>
          <dt>
            <span id="itemNumber">
              <Translate contentKey="calvaryErpApp.balanceSheetItemType.itemNumber">Item Number</Translate>
            </span>
          </dt>
          <dd>{balanceSheetItemTypeEntity.itemNumber}</dd>
          <dt>
            <span id="shortDescription">
              <Translate contentKey="calvaryErpApp.balanceSheetItemType.shortDescription">Short Description</Translate>
            </span>
          </dt>
          <dd>{balanceSheetItemTypeEntity.shortDescription}</dd>
          <dt>
            <Translate contentKey="calvaryErpApp.balanceSheetItemType.transactionAccount">Transaction Account</Translate>
          </dt>
          <dd>{balanceSheetItemTypeEntity.transactionAccount ? balanceSheetItemTypeEntity.transactionAccount.accountName : ''}</dd>
          <dt>
            <Translate contentKey="calvaryErpApp.balanceSheetItemType.parentItem">Parent Item</Translate>
          </dt>
          <dd>{balanceSheetItemTypeEntity.parentItem ? balanceSheetItemTypeEntity.parentItem.itemNumber : ''}</dd>
        </dl>
        <Button tag={Link} to="/balance-sheet-item-type" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/balance-sheet-item-type/${balanceSheetItemTypeEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default BalanceSheetItemTypeDetail;
