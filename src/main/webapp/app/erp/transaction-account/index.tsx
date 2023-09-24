import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TransactionAccount from './transaction-account';
import TransactionAccountDetail from './transaction-account-detail';
import TransactionAccountUpdate from './transaction-account-update';
import TransactionAccountDeleteDialog from './transaction-account-delete-dialog';

const TransactionAccountRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TransactionAccount />} />
    <Route path="new" element={<TransactionAccountUpdate />} />
    <Route path=":id">
      <Route index element={<TransactionAccountDetail />} />
      <Route path="edit" element={<TransactionAccountUpdate />} />
      <Route path="delete" element={<TransactionAccountDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TransactionAccountRoutes;
