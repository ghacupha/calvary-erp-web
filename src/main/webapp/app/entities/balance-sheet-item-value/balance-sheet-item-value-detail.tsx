import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './balance-sheet-item-value.reducer';

export const BalanceSheetItemValueDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const balanceSheetItemValueEntity = useAppSelector(state => state.balanceSheetItemValue.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="balanceSheetItemValueDetailsHeading">
          <Translate contentKey="calvaryErpApp.balanceSheetItemValue.detail.title">BalanceSheetItemValue</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{balanceSheetItemValueEntity.id}</dd>
          <dt>
            <span id="shortDescription">
              <Translate contentKey="calvaryErpApp.balanceSheetItemValue.shortDescription">Short Description</Translate>
            </span>
          </dt>
          <dd>{balanceSheetItemValueEntity.shortDescription}</dd>
          <dt>
            <span id="effectiveDate">
              <Translate contentKey="calvaryErpApp.balanceSheetItemValue.effectiveDate">Effective Date</Translate>
            </span>
          </dt>
          <dd>
            {balanceSheetItemValueEntity.effectiveDate ? (
              <TextFormat value={balanceSheetItemValueEntity.effectiveDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="itemAmount">
              <Translate contentKey="calvaryErpApp.balanceSheetItemValue.itemAmount">Item Amount</Translate>
            </span>
          </dt>
          <dd>{balanceSheetItemValueEntity.itemAmount}</dd>
          <dt>
            <Translate contentKey="calvaryErpApp.balanceSheetItemValue.itemType">Item Type</Translate>
          </dt>
          <dd>{balanceSheetItemValueEntity.itemType ? balanceSheetItemValueEntity.itemType.itemNumber : ''}</dd>
        </dl>
        <Button tag={Link} to="/balance-sheet-item-value" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/balance-sheet-item-value/${balanceSheetItemValueEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default BalanceSheetItemValueDetail;
