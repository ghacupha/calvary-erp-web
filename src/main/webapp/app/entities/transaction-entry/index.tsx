import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TransactionEntry from './transaction-entry';
import TransactionEntryDetail from './transaction-entry-detail';
import TransactionEntryUpdate from './transaction-entry-update';
import TransactionEntryDeleteDialog from './transaction-entry-delete-dialog';

const TransactionEntryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TransactionEntry />} />
    <Route path="new" element={<TransactionEntryUpdate />} />
    <Route path=":id">
      <Route index element={<TransactionEntryDetail />} />
      <Route path="edit" element={<TransactionEntryUpdate />} />
      <Route path="delete" element={<TransactionEntryDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TransactionEntryRoutes;
