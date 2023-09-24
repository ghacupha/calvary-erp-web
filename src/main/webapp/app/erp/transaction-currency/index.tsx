import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TransactionCurrency from './transaction-currency';
import TransactionCurrencyDetail from './transaction-currency-detail';
import TransactionCurrencyUpdate from './transaction-currency-update';
import TransactionCurrencyDeleteDialog from './transaction-currency-delete-dialog';

const TransactionCurrencyRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TransactionCurrency />} />
    <Route path="new" element={<TransactionCurrencyUpdate />} />
    <Route path=":id">
      <Route index element={<TransactionCurrencyDetail />} />
      <Route path="edit" element={<TransactionCurrencyUpdate />} />
      <Route path="delete" element={<TransactionCurrencyDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TransactionCurrencyRoutes;
