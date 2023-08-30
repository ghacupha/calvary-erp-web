import React, { useState } from 'react';
import AsyncSelect from 'react-select/async';
import axios from 'axios';
import { ITransactionAccount } from 'app/shared/model/transaction-account.model';
import { translate } from 'react-jhipster';

const apiSearchUrl = 'api/_search/transaction-accounts';

interface AutocompleteSearchProps {
  selectedAccount: ITransactionAccount | null;
  onSelectAccount: (account: ITransactionAccount) => void;
}

const AutocompleteSearch: React.FC<AutocompleteSearchProps> = ({ selectedAccount, onSelectAccount }) => {
  const [selectedOption, setSelectedOption] = useState<ITransactionAccount | null>(null);

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

  const handleOptionSelect = (option: { value: ITransactionAccount; label: string }) => {
    setSelectedOption(option.value);
    if (option.value) {
      onSelectAccount(option.value);
      selectedAccount = selectedOption;
    }
  };

  const customStyles = {
    control: (provided, state) => ({
      ...provided,
      border: state.isFocused ? '2px solid #3498db' : '2px solid #ced4da', // Example border style
      boxShadow: state.isFocused ? '0 0 3px rgba(52, 152, 219, 0.5)' : 'none', // Example box shadow
      '&:hover': {
        border: '2px solid #3498db',
      },
    }),
  };

  return (
    <AsyncSelect
      value={selectedOption ? { value: selectedOption, label: selectedOption.accountName } : null}
      onChange={handleOptionSelect}
      loadOptions={loadOptions} // This is a function to load options asynchronously
      placeholder={translate('calvaryErp.transactionEntry.transactionAccountPlaceholder')}
      styles={customStyles}
    />

  );
};

export default AutocompleteSearch;
