import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TransactionItemAmount from './transaction-item-amount';
import TransactionItemAmountDetail from './transaction-item-amount-detail';
import TransactionItemAmountUpdate from './transaction-item-amount-update';
import TransactionItemAmountDeleteDialog from './transaction-item-amount-delete-dialog';

const TransactionItemAmountRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TransactionItemAmount />} />
    <Route path="new" element={<TransactionItemAmountUpdate />} />
    <Route path=":id">
      <Route index element={<TransactionItemAmountDetail />} />
      <Route path="edit" element={<TransactionItemAmountUpdate />} />
      <Route path="delete" element={<TransactionItemAmountDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TransactionItemAmountRoutes;
