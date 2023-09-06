import React, { useEffect, useState } from 'react';
import AsyncSelect from 'react-select/async';
import axios from 'axios';
import { ITransactionAccount } from 'app/shared/model/transaction-account.model';
import { translate } from 'react-jhipster';
import { getEntity } from 'app/entities/transaction-account/transaction-account.reducer';
import { useDispatch } from 'react-redux';
import { useAppDispatch, useAppSelector } from 'app/config/store';

const apiSearchUrl = 'api/_search/transaction-accounts';
const apIUrl = `api/transaction-accounts`;

interface AutocompleteSearchProps {
  // selectedAccount: ITransactionAccount | null;
  onSelectAccount: (account: ITransactionAccount) => void;
}

const AutocompleteSearch: React.FC<AutocompleteSearchProps> = ({ onSelectAccount }) => {
  const [selectedAccount, setSelectedAccount] = useState<ITransactionAccount | null>(null);
  const dispatch = useAppDispatch();

  const loadOptions = async (inputValue: string) => {
    const requestUrl = `${apiSearchUrl}?query=${inputValue}`;
    try {
      const response = await axios.get(requestUrl);
      return response.data.map((result: ITransactionAccount) => ({
        value: result,
        label: result.accountName,
      }));
    } catch (error) {
      console.error('Error fetching search results:', error);
      return [];
    }
  };

  const customStyles = {
    control: (provided, state) => ({
      ...provided,
      border: state.isFocused ? '2px solid #3498db' : '2px solid #ced4da',
      boxShadow: state.isFocused ? '0 0 3px rgba(52, 152, 219, 0.5)' : 'none',
      '&:hover': {
        border: '2px solid #3498db'
      }
    })
  };

  const handleOptionSelect = (option: { value: ITransactionAccount; label: string }) => {
    setSelectedAccount(option.value);

    dispatch(getEntity(selectedAccount.id));
  };

  // useEffect(() => {
  //   if (selectedAccount) {
  //     dispatch(getEntity(selectedAccount.id));
  //   }
  // }, [selectedAccount]);

  return (
    <AsyncSelect
      value={selectedAccount ? { value: selectedAccount, label: selectedAccount.accountName } : null}
      onChange={handleOptionSelect}
      loadOptions={loadOptions}
      placeholder={translate('calvaryErp.transactionEntry.transactionAccountPlaceholder')}
      styles={customStyles}
    />
  );
};

export default AutocompleteSearch;

