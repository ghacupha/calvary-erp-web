import React, { useEffect, useState } from 'react';
import AsyncSelect from 'react-select/async';
import axios from 'axios';
import { translate } from 'react-jhipster';
import { getEntity as getTransactionItem } from '../transaction-item/transaction-item.reducer';
import { useAppDispatch } from 'app/config/store';
import { ITransactionItem } from 'app/shared/model/transaction-item.model';
import { getEntities as getTransactionItems } from 'app/erp/transaction-item/transaction-item.reducer';

const apiSearchUrl = 'api/_search/transaction-items';

interface TransactionItemAutocompleteProps {
  onSelectEntity: (account: ITransactionItem) => void;
}

const TransactionItemAutocomplete: React.FC<TransactionItemAutocompleteProps> = ({ onSelectEntity }) => {
  const [selectedEntity, setSelectedEntity] = useState<ITransactionItem | null>(null);
  const dispatch = useAppDispatch();

  const loadOptions = async (inputValue: string) => {
    const requestUrl = `${apiSearchUrl}?query=${inputValue}`;
    try {
      const response = await axios.get(requestUrl);
      return response.data.map((result: ITransactionItem) => ({
        value: result,
        label: result.itemName,
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
        border: '2px solid #3498db',
      },
    }),
  };

  const handleOptionSelect = (option: { value: ITransactionItem; label: string }) => {
    setSelectedEntity(option.value);

    onSelectEntity(selectedEntity);
  };

  useEffect(() => {
    if (selectedEntity) {
      dispatch(getTransactionItem(selectedEntity.id));
    }
  }, [selectedEntity]);

  return (
    <div>
      <AsyncSelect
        value={selectedEntity ? { value: selectedEntity, label: selectedEntity.itemName } : null}
        onChange={handleOptionSelect}
        loadOptions={loadOptions}
        placeholder={'Transaction Item'}
        styles={customStyles}
      />
    </div>
  );
};

export default TransactionItemAutocomplete;
