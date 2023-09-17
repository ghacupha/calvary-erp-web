import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ITransactionAccount } from 'app/shared/model/transaction-account.model';
import { getEntities as getTransactionAccounts } from 'app/entities/transaction-account/transaction-account.reducer';
import { IAccountTransaction } from 'app/shared/model/account-transaction.model';
import { getEntities as getAccountTransactions } from 'app/entities/account-transaction/account-transaction.reducer';
import { ITransactionEntry } from 'app/shared/model/transaction-entry.model';
import { TransactionEntryTypes } from 'app/shared/model/enumerations/transaction-entry-types.model';
import { getEntity, updateEntity, createEntity, reset } from './transaction-entry.reducer';
import AutocompleteSearchTransactionAccount from 'app/erp/auto-complete-search-transaction-account';
import SearchAccountTransaction from 'app/erp/auto-complete/search-account-transaction';

export const TransactionEntryUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const transactionAccounts = useAppSelector(state => state.transactionAccount.entities);
  const accountTransactions = useAppSelector(state => state.accountTransaction.entities);
  const transactionEntryEntity = useAppSelector(state => state.transactionEntry.entity);
  const loading = useAppSelector(state => state.transactionEntry.loading);
  const updating = useAppSelector(state => state.transactionEntry.updating);
  const updateSuccess = useAppSelector(state => state.transactionEntry.updateSuccess);
  const transactionEntryTypesValues = Object.keys(TransactionEntryTypes);
  const [selectedAccount, setSelectedAccount] = useState<ITransactionAccount | null>(null);
  const [selectedTransaction, setSelectedTransaction] = useState<IAccountTransaction | null>(null);

  const selectedTransactionAccountEntity = useAppSelector(state => state.transactionAccount.selected);
  // const transactionAccountEntity = useAppSelector(state => state.transactionAccount.entity); // picking selected entity from store
  const accountTransactionEntity = useAppSelector(state => state.accountTransaction.entity); // picking selected entity from store

  const handleAccountSelect = (account: ITransactionAccount | null) => {
    if (account) {
      setSelectedAccount(account); // setting selectedAccount to view on the form
    }
  };

  const handleTransactionSelect = (transaction: IAccountTransaction | null) => {
    if (transaction) {
      setSelectedTransaction(transaction);
    }
  };

  const handleClose = () => {
    navigate('/transaction-entry' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getTransactionAccounts({}));
    dispatch(getAccountTransactions({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...transactionEntryEntity,
      ...values,
      transactionAccount: selectedTransactionAccountEntity, // use account selected from the store to persist
      accountTransaction: accountTransactionEntity,
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
          transactionEntryType: 'CREDIT',
          ...transactionEntryEntity,
          transactionAccount: transactionEntryEntity?.transactionAccount?.id,
          accountTransaction: transactionEntryEntity?.accountTransaction?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="calvaryErpApp.transactionEntry.home.createOrEditLabel" data-cy="TransactionEntryCreateUpdateHeading">
            <Translate contentKey="calvaryErpApp.transactionEntry.home.createOrEditLabel">Create or edit a TransactionEntry</Translate>
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
                  id="transaction-entry-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('calvaryErpApp.transactionEntry.entryAmount')}
                id="transaction-entry-entryAmount"
                name="entryAmount"
                data-cy="entryAmount"
                type="text"
              />
              <ValidatedField
                label={translate('calvaryErpApp.transactionEntry.transactionEntryType')}
                id="transaction-entry-transactionEntryType"
                name="transactionEntryType"
                data-cy="transactionEntryType"
                type="select"
              >
                {transactionEntryTypesValues.map(transactionEntryTypes => (
                  <option value={transactionEntryTypes} key={transactionEntryTypes}>
                    {translate('calvaryErpApp.TransactionEntryTypes.' + transactionEntryTypes)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('calvaryErpApp.transactionEntry.description')}
                id="transaction-entry-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                label={translate('calvaryErpApp.transactionEntry.wasProposed')}
                id="transaction-entry-wasProposed"
                name="wasProposed"
                data-cy="wasProposed"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('calvaryErpApp.transactionEntry.wasPosted')}
                id="transaction-entry-wasPosted"
                name="wasPosted"
                data-cy="wasPosted"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('calvaryErpApp.transactionEntry.wasDeleted')}
                id="transaction-entry-wasDeleted"
                name="wasDeleted"
                data-cy="wasDeleted"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('calvaryErpApp.transactionEntry.wasApproved')}
                id="transaction-entry-wasApproved"
                name="wasApproved"
                data-cy="wasApproved"
                check
                type="checkbox"
              />
              <AutocompleteSearchTransactionAccount
                onSelectAccount={handleAccountSelect}
                />

              <SearchAccountTransaction
                onSelectTransaction={handleTransactionSelect}
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/transaction-entry" replace color="info">
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

export default TransactionEntryUpdate;
