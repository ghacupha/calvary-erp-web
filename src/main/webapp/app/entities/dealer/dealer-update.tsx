import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IDealerType } from 'app/shared/model/dealer-type.model';
import { getEntities as getDealerTypes } from 'app/entities/dealer-type/dealer-type.reducer';
import { getEntity, updateEntity, createEntity, reset } from './dealer.reducer';
import DealerTypeAutocomplete from 'app/erp/auto-complete/dealer-type.autocomplete';

export const DealerUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const dealerTypes = useAppSelector(state => state.dealerType.entities);
  const dealerEntity = useAppSelector(state => state.dealer.entity);
  const loading = useAppSelector(state => state.dealer.loading);
  const updating = useAppSelector(state => state.dealer.updating);
  const updateSuccess = useAppSelector(state => state.dealer.updateSuccess);
  const dealerTypeEntitySelected = useAppSelector(state => state.dealerType.selected);

  const [selectedDealerType, setSelectedDealerType] = useState<IDealerType>(null);

  const handleClose = () => {
    navigate('/dealer' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getDealerTypes({}));
  }, []);

  const handleDealerTypeSelectEvent = (selectedDealerType) => {
    if (selectedDealerType) {
      setSelectedDealerType(selectedDealerType)
    }
  }

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...dealerEntity,
      ...values,
      dealerType: dealerTypeEntitySelected
      // dealerType: dealerTypes.find(it => it.id.toString() === values.dealerType.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...dealerEntity,
          dealerType: dealerEntity?.dealerType?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="calvaryErpApp.dealer.home.createOrEditLabel" data-cy="DealerCreateUpdateHeading">
            <Translate contentKey="calvaryErpApp.dealer.home.createOrEditLabel">Create or edit a Dealer</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="dealer-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('calvaryErpApp.dealer.name')}
                id="dealer-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              {/*<ValidatedField
                id="dealer-dealerType"
                name="dealerType"
                data-cy="dealerType"
                label={translate('calvaryErpApp.dealer.dealerType')}
                type="select"
                required
              >
                <option value="" key="0" />
                {dealerTypes
                  ? dealerTypes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>*/}

              <DealerTypeAutocomplete onSelectDealerType={handleDealerTypeSelectEvent} />

              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/dealer" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default DealerUpdate;
