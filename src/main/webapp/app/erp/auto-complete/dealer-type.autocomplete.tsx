import React, { useEffect, useState } from 'react';
import AsyncSelect from 'react-select/async';
import axios from 'axios';
import { translate } from 'react-jhipster';
import { useAppDispatch } from 'app/config/store';
import { IDealerType } from 'app/shared/model/dealer-type.model';
import { getSelectedEntity } from 'app/entities/dealer-type/dealer-type.reducer';

const apiSearchUrl = 'api/_search/dealer-types';

interface DealerTypeAutocompleteProps {
  onSelectDealerType: (account: IDealerType) => void;
}

const DealerTypeAutocomplete: React.FC<DealerTypeAutocompleteProps> = ({ onSelectDealerType }) => {
  const [selectedDealerType, setSelectedDealerType] = useState<IDealerType | null>(null);
  const dispatch = useAppDispatch();

  const loadOptions = async (inputValue: string) => {
    const requestUrl = `${apiSearchUrl}?query=${inputValue}`;
    try {
      const response = await axios.get(requestUrl);
      return response.data.map((result: IDealerType) => ({
        value: result,
        label: result.name,
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

  const handleOptionSelect = (option: { value: IDealerType; label: string }) => {
    setSelectedDealerType(option.value);

    onSelectDealerType(selectedDealerType);
  };

  useEffect(() => {
    if (selectedDealerType) {
      dispatch(getSelectedEntity(selectedDealerType.id));
    }
  }, [selectedDealerType]);

  return (
    <div>
      {/*<div> Dealer Type </div>*/}
      <div>{translate('calvaryErp.dealer.dealerTypeLabel')}</div>
      <AsyncSelect
        value={selectedDealerType ? { value: selectedDealerType, label: selectedDealerType.name } : null}
        onChange={handleOptionSelect}
        loadOptions={loadOptions}
        placeholder={translate('calvaryErp.dealer.dealerTypePlaceholder')}
        styles={customStyles}
      />
    </div>
  );
};

export default DealerTypeAutocomplete;

