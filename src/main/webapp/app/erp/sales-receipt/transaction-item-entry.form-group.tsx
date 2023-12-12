import { useAppDispatch, useAppSelector } from 'app/config/store';
import { Link, useNavigate, useParams } from 'react-router-dom';
import React, { useEffect, useState } from 'react';

import { Button, Col, FormText, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntities as getTransactionItems } from 'app/erp/transaction-item/transaction-item.reducer';
import { createEntity, getEntity, updateEntity } from 'app/erp/transaction-item-entry/transaction-item-entry.reducer';
import AccountTransactionAutocomplete from 'app/erp/auto-complete/account-transaction.autocomplete';
import TransactionItemAutocomplete from 'app/erp/auto-complete/transaction-items.autocomplete';
import { ITransactionAccount } from 'app/shared/model/transaction-account.model';
import { ITransactionItem } from 'app/shared/model/transaction-item.model';

export const TransactionItemEntryFormGroup = ({ addNewEntry }) => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const transactionItems = useAppSelector(state => state.transactionItem.entities);
  const transactionItemEntryEntity = useAppSelector(state => state.transactionItemEntry.entity);
  const loading = useAppSelector(state => state.transactionItemEntry.loading);
  const updating = useAppSelector(state => state.transactionItemEntry.updating);
  const updateSuccess = useAppSelector(state => state.transactionItemEntry.updateSuccess);

  const [description, setDescription] = useState('');
  const [itemAmount, setItemAmount] = useState('');
  const [transactionItem, setTransactionItem] = useState<ITransactionItem>();

  const handleClose = () => {
    navigate('/sales-receipt');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }

    dispatch(getTransactionItems({}));
  }, [transactionItem]);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const handleAddEntry = values => {
    try {
      // Validate the form fields here before sending to the backend

      // Create the payload for the new entry
      const newEntryData = {
        ...transactionItemEntryEntity,
        description,
        itemAmount,
        transactionItem,
      };

      // Dispatch an action to create a new transaction-item-entry
      dispatch(createEntity(newEntryData));

      // Pass the newly created entry to the parent component
      addNewEntry(transactionItemEntryEntity);

      // Clear form fields after successful creation
      setDescription('');
      setItemAmount('');
      setTransactionItem({});
    } catch (error) {
      // Handle error (e.g., show error message)
      console.error('Error creating new entry:', error);
    }

    // fetch newly created entity from the Store

    // Pass the newly created entry to the parent component
    addNewEntry(transactionItemEntryEntity);
  };

  const handleTransactionItemSelectedEvent = pickedItem => {
    if (pickedItem) {
      setTransactionItem(pickedItem);
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...transactionItemEntryEntity,
          transactionItem: transactionItemEntryEntity?.transactionItem?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="calvaryErpApp.transactionItemEntry.home.createOrEditLabel" data-cy="TransactionItemEntryCreateUpdateHeading">
            Create or edit a Sales Receipt Entry
          </h2>
        </Col>
      </Row>
      {/*<Row className="justify-content-center">*/}
      <Row className="justify-content-center">
        <input
          type="text"
          value={description}
          onChange={e => setDescription(e.target.value)}
          placeholder="Description"
          data-cy={'Description'}
        />
        <input type="number" value={itemAmount} onChange={e => setItemAmount(e.target.value)} placeholder="Item Amount" />

        <TransactionItemAutocomplete onSelectEntity={handleTransactionItemSelectedEvent} />

        <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" onClick={handleAddEntry}>
          <FontAwesomeIcon icon="save" />
          &nbsp; Add Entry
        </Button>
      </Row>
    </div>
  );
};

export default TransactionItemEntryFormGroup;
