import React, { useState } from 'react';
import Select from 'react-select';
import { debounce } from 'lodash';
import axios from 'axios';
import { ITransactionAccount } from 'app/shared/model/transaction-account.model';
import { translate } from 'react-jhipster';

const apiSearchUrl = 'api/_search/transaction-accounts';

interface AutocompleteSearchProps {
  entity: string;
  selectedAccount: ITransactionAccount | null;
  onSelectAccount: (account: ITransactionAccount) => void;
}

const AutocompleteSearch: React.FC<AutocompleteSearchProps> = ({ entity, selectedAccount, onSelectAccount }) => {
  const [inputValue, setInputValue] = useState<string>('');
  const [searchResults, setSearchResults] = useState<ITransactionAccount[]>([]);
  const [selectedOption, setSelectedOption] = React.useState<ITransactionAccount | null>(null);

  const fetchSearchResults: (query: string) => Promise<void> = debounce(async (query: string) => {
    const requestUrl = `${apiSearchUrl}?query=${query}`;
    try {
      const response = await axios.get(`${requestUrl}`);
      setSearchResults(response.data);
    } catch (error) {
      console.error('Error fetching search results:', error);
    }
  }, 300);

  const handleInputChange = (newValue: string) => {
    setInputValue(newValue);
    fetchSearchResults(newValue);
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
    <Select
      value={selectedOption ? { value: selectedOption, label: selectedOption.accountName } : null}
      onChange={handleOptionSelect}
      options={searchResults.map(result => {
        return {
          value: result,
          label: result.accountName,
        };
       })}
      onInputChange={handleInputChange}
      placeholder={translate('calvaryErp.transactionEntry.transactionAccountPlaceholder')}
      styles={customStyles}
    />
  );
};

export default AutocompleteSearch;
