import React, { useEffect, useState } from 'react';
import AsyncSelect from 'react-select/async';
import axios from 'axios';
import { getEntity } from '../sales-receipt/sales-receipt.reducer';
import { useAppDispatch } from 'app/config/store';
import { ISalesReceipt } from 'app/shared/model/sales-receipt.model';

const apiSearchUrl = 'api/_search/sales-receipts';

interface AutocompleteSearchSalesReceiptProps {
  onSelectEntity: (salesReceipt: ISalesReceipt) => void;
}

const AutocompleteSearchSalesReceipt: React.FC<AutocompleteSearchSalesReceiptProps> = ({ onSelectEntity }) => {
  const [selectedSalesReceipt, setSalesReceipt] = useState<ISalesReceipt | null>(null);

  const dispatch = useAppDispatch();

  const loadOptions = async (inputValue: string) => {
    const requestUrl = `${apiSearchUrl}?query=${inputValue}`;
    try {
      const response = await axios.get(requestUrl);
      return response.data.map((result: ISalesReceipt) => ({
        value: result,
        label: result.id,
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

  const handleOptionSelect = (option: { value: ISalesReceipt; label: number }) => {
    setSalesReceipt(option.value);

    onSelectEntity(selectedSalesReceipt);
  };

  useEffect(() => {
    if (selectedSalesReceipt) {
      dispatch(getEntity(selectedSalesReceipt.id));
    }
  }, [selectedSalesReceipt]);

  return (
    <div>
      <div>Sales Receipt</div>
      <AsyncSelect
        value={selectedSalesReceipt ? { value: selectedSalesReceipt, label: selectedSalesReceipt.id } : null}
        onChange={handleOptionSelect}
        loadOptions={loadOptions}
        placeholder="Sales Receipt"
        styles={customStyles}
      />
    </div>
  );
};

export default AutocompleteSearchSalesReceipt;
