import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TransactionItem from './transaction-item';
import TransactionItemDetail from './transaction-item-detail';
import TransactionItemUpdate from './transaction-item-update';
import TransactionItemDeleteDialog from './transaction-item-delete-dialog';

const TransactionItemRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TransactionItem />} />
    <Route path="new" element={<TransactionItemUpdate />} />
    <Route path=":id">
      <Route index element={<TransactionItemDetail />} />
      <Route path="edit" element={<TransactionItemUpdate />} />
      <Route path="delete" element={<TransactionItemDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TransactionItemRoutes;
