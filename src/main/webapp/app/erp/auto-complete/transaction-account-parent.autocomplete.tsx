import React, { useEffect, useState } from 'react';
import AsyncSelect from 'react-select/async';
import axios from 'axios';
import { ITransactionAccount } from 'app/shared/model/transaction-account.model';
import { translate } from 'react-jhipster';
import { getEntity } from 'app/entities/transaction-account/transaction-account.reducer';
import { useAppDispatch } from 'app/config/store';
import { getTransactionAccountEntity } from 'app/erp/auto-complete/search-account-transaction.reducer';

const apiSearchUrl = 'api/_search/transaction-accounts';

interface AutoCompleteParentTransactionAccountProps {
  onSelectAccount: (account: ITransactionAccount) => void;
}

const AutoCompleteParentTransactionAccount: React.FC<AutoCompleteParentTransactionAccountProps> = ({ onSelectAccount }) => {
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

    onSelectAccount(selectedAccount);
  };

  useEffect(() => {
    if (selectedAccount) {
      dispatch(getTransactionAccountEntity(selectedAccount.id));
    }
  }, [selectedAccount]);

  return (
    <div>
      <div>Account</div>
      <AsyncSelect
        value={selectedAccount ? { value: selectedAccount, label: selectedAccount.accountName } : null}
        onChange={handleOptionSelect}
        loadOptions={loadOptions}
        placeholder={translate('calvaryErp.transactionEntry.transactionAccountPlaceholder')}
        styles={customStyles}
      />
    </div>
  );
};

export default AutocompleParentTransactionAccount;

