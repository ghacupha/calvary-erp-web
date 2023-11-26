import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TransactionClass from './transaction-class';
import TransactionClassDetail from './transaction-class-detail';
import TransactionClassUpdate from './transaction-class-update';
import TransactionClassDeleteDialog from './transaction-class-delete-dialog';

const TransactionClassRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TransactionClass />} />
    <Route path="new" element={<TransactionClassUpdate />} />
    <Route path=":id">
      <Route index element={<TransactionClassDetail />} />
      <Route path="edit" element={<TransactionClassUpdate />} />
      <Route path="delete" element={<TransactionClassDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TransactionClassRoutes;
