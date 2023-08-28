import React, { useState } from 'react';
import { debounce } from 'lodash';
import axios from 'axios';
import { ITransactionAccount } from 'app/shared/model/transaction-account.model';

interface AutocompleteSearchProps {
  entity: string;
}

const AutocompleteSearch: React.FC<AutocompleteSearchProps> = ({ entity }) => {
  const [inputValue, setInputValue] = useState<string>('');
  const [searchResults, setSearchResults] = useState<ITransactionAccount[]>([]);

  const fetchSearchResults = debounce(async (inputValue: string) => {
    try {
      const response = await axios.get(`/api/${entity}?query=${inputValue}`);
      setSearchResults(response.data);
    } catch (error) {
      console.error('Error fetching search results:', error);
    }
  }, 300); // Adjust the debounce delay as needed

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value;
    setInputValue(value);
    fetchSearchResults(value);
  };

  const handleResultClick = (result: ITransactionAccount) => {
    setInputValue(result.accountName || ''); // Adjust according to your data structure
    // Handle storing the selected item's ID or relevant information
  };

  return (
    <div>
      <input
        type="text"
        value={inputValue}
        onChange={handleInputChange}
        placeholder={`Search ${entity}`}
      />
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
