import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TransactionAccountType from './transaction-account-type';
import TransactionAccountTypeDetail from './transaction-account-type-detail';
import TransactionAccountTypeUpdate from './transaction-account-type-update';
import TransactionAccountTypeDeleteDialog from './transaction-account-type-delete-dialog';

const TransactionAccountTypeRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TransactionAccountType />} />
    <Route path="new" element={<TransactionAccountTypeUpdate />} />
    <Route path=":id">
      <Route index element={<TransactionAccountTypeDetail />} />
      <Route path="edit" element={<TransactionAccountTypeUpdate />} />
      <Route path="delete" element={<TransactionAccountTypeDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TransactionAccountTypeRoutes;
