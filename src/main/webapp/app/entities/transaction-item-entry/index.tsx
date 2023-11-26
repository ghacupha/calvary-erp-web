import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TransactionItemEntry from './transaction-item-entry';
import TransactionItemEntryDetail from './transaction-item-entry-detail';
import TransactionItemEntryUpdate from './transaction-item-entry-update';
import TransactionItemEntryDeleteDialog from './transaction-item-entry-delete-dialog';

const TransactionItemEntryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TransactionItemEntry />} />
    <Route path="new" element={<TransactionItemEntryUpdate />} />
    <Route path=":id">
      <Route index element={<TransactionItemEntryDetail />} />
      <Route path="edit" element={<TransactionItemEntryUpdate />} />
      <Route path="delete" element={<TransactionItemEntryDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TransactionItemEntryRoutes;
