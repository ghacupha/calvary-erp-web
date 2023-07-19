import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './dealer-type.reducer';

export const DealerTypeDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const dealerTypeEntity = useAppSelector(state => state.dealerType.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="dealerTypeDetailsHeading">
          <Translate contentKey="calvaryErpApp.dealerType.detail.title">DealerType</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{dealerTypeEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="calvaryErpApp.dealerType.name">Name</Translate>
            </span>
          </dt>
          <dd>{dealerTypeEntity.name}</dd>
        </dl>
        <Button tag={Link} to="/dealer-type" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/dealer-type/${dealerTypeEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DealerTypeDetail;
