import React, { useState } from 'react';
import { debounce } from 'lodash';
import axios from 'axios';
import { ITransactionAccount } from 'app/shared/model/transaction-account.model';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { IQueryParams } from 'app/shared/reducers/reducer.utils';
import { FormText } from 'reactstrap';

const apiSearchUrl = 'api/_search/transaction-accounts';

interface AutocompleteSearchProps {
  entity: string;
  selectedAccount: ITransactionAccount | null; // Add this line
  onSelectAccount: (account: ITransactionAccount) => void;
}

const AutocompleteSearch: React.FC<AutocompleteSearchProps> = ({ entity, selectedAccount, onSelectAccount }) => {
  const [inputValue, setInputValue] = useState<string>('');
  const [searchResults, setSearchResults] = useState<ITransactionAccount[]>([]);
  const [showResults, setShowResults] = useState(false); // State to control the display of results

  const fetchSearchResults: (query: string) => Promise<void> = debounce(async (query: string) => {
    const requestUrl = `${apiSearchUrl}?query=${query}`;
    try {
      const response = await axios.get(`${requestUrl}`);
      setSearchResults(response.data);
    } catch (error) {
      console.error('Error fetching search results:', error);
    }
  }, 300); // Adjust the debounce delay as needed

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value;
    console.log('Form input value:', value);
    setInputValue(value);
    fetchSearchResults(value);
    setShowResults(true); // Display the results
  };

  const handleResultClick = (result: ITransactionAccount) => {
    setInputValue(result.accountName || '');
    onSelectAccount(result); // Update selectedAccount in the main form
    setShowResults(false); // Close the list after selection
  };

  return (
    <div>
      {/* Search input field */}
      <input
        type="text"
        value={inputValue}
        onChange={handleInputChange}
        placeholder={`Search ${entity}`}
      />
      {/* Search results */}
      <ul>
        {searchResults.map(result => (
          <li key={result.id} onClick={() => handleResultClick(result)}>
            {result.accountName}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default AutocompleteSearch;
