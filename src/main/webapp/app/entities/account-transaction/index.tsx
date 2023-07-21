import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AccountTransaction from './account-transaction';
import AccountTransactionDetail from './account-transaction-detail';
import AccountTransactionUpdate from './account-transaction-update';
import AccountTransactionDeleteDialog from './account-transaction-delete-dialog';

const AccountTransactionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AccountTransaction />} />
    <Route path="new" element={<AccountTransactionUpdate />} />
    <Route path=":id">
      <Route index element={<AccountTransactionDetail />} />
      <Route path="edit" element={<AccountTransactionUpdate />} />
      <Route path="delete" element={<AccountTransactionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AccountTransactionRoutes;
