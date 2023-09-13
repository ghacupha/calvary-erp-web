import {
  ActionReducerMapBuilder,
  createAsyncThunk, createSlice,
  isFulfilled,
  isPending,
  SliceCaseReducers,
  ValidateSliceCaseReducers
} from '@reduxjs/toolkit';
import axios from 'axios';
import { defaultValue, ITransactionAccount } from 'app/shared/model/transaction-account.model';
import {
  isRejectedAction,
  serializeAxiosError
} from 'app/shared/reducers/reducer.utils';
import {
  createEntity,
  deleteEntity,
  getEntities,
  partialUpdateEntity,
  searchEntities,
  updateEntity
} from 'app/entities/transaction-account/transaction-account.reducer';
import { initialState } from 'app/shared/reducers/authentication';
import { SelectedEntityState } from 'app/erp/auto-complete/auto-complete-reducer.utils';

const apiUrl = 'api/transaction-accounts';
const apiSearchUrl = 'api/_search/transaction-accounts';

export const getSelectedEntity = createAsyncThunk(
  'selectedTransactionAccount/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<ITransactionAccount>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

// slice

const createSelectedEntitySlice = <T, Reducers extends SliceCaseReducers<SelectedEntityState<T>>>({
                                                                                                             name = '',
                                                                                                             initialState,
                                                                                                             reducers,
                                                                                                             extraReducers,
                                                                                                             skipRejectionHandling,
                                                                                                           }: {
  name: string;
  initialState: SelectedEntityState<T>;
  reducers?: ValidateSliceCaseReducers<SelectedEntityState<T>, Reducers>;
  extraReducers?: (builder: ActionReducerMapBuilder<SelectedEntityState<T>>) => void;
  skipRejectionHandling?: boolean;
}) => {
  return createSlice({
    name,
    initialState,
    reducers: {
      /**
       * Reset the entity state to initial state
       */
      reset() {
        return initialState;
      },
      ...reducers,
    },
    extraReducers(builder) {
      extraReducers(builder);
      /*
       * Common rejection logic is handled here.
       * If you want to add your own rejcetion logic, pass `skipRejectionHandling: true`
       * while calling `createEntitySlice`
       * */
      if (!skipRejectionHandling) {
        builder.addMatcher(isRejectedAction, (state, action) => {
          state.loading = false;
          state.updating = false;
          state.updateSuccess = false;
          state.errorMessage = action.error.message;
        });
      }
    },
  });
};

export const selectedTransactionAccountSlice = createSelectedEntitySlice({
  name: 'selectedTransactionAccount',
  initialState,
  extraReducers(builder) {
    builder
      .addCase(getSelectedEntity.fulfilled, (state, action) => {
        state.loading = false;
        state.selected = action.payload.data;
      })
      .addCase(deleteEntity.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
        state.selected = {};
      })
      .addMatcher(isFulfilled(getEntities, searchEntities), (state, action) => {
        const { data, headers } = action.payload;

        return {
          ...state,
          loading: false,
          entities: data,
          totalItems: parseInt(headers['x-total-count'], 10),
        };
      })
      .addMatcher(isFulfilled(createEntity, updateEntity, partialUpdateEntity), (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;
      })
      .addMatcher(isPending(getEntities, getSelectedEntity, searchEntities), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addMatcher(isPending(createEntity, updateEntity, partialUpdateEntity, deleteEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      });
  },
});

export const { reset } = selectedTransactionAccountSlice.actions;

// Reducer
export default selectedTransactionAccountSlice.reducer;
