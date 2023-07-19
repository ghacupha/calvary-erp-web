import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './dealer.reducer';

export const DealerDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const dealerEntity = useAppSelector(state => state.dealer.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="dealerDetailsHeading">
          <Translate contentKey="calvaryErpApp.dealer.detail.title">Dealer</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{dealerEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="calvaryErpApp.dealer.name">Name</Translate>
            </span>
          </dt>
          <dd>{dealerEntity.name}</dd>
          <dt>
            <Translate contentKey="calvaryErpApp.dealer.dealerType">Dealer Type</Translate>
          </dt>
          <dd>{dealerEntity.dealerType ? dealerEntity.dealerType.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/dealer" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/dealer/${dealerEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DealerDetail;
