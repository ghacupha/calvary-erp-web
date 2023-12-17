import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TransferItemEntry from './transfer-item-entry';
import TransferItemEntryDetail from './transfer-item-entry-detail';
import TransferItemEntryUpdate from './transfer-item-entry-update';
import TransferItemEntryDeleteDialog from './transfer-item-entry-delete-dialog';

const TransferItemEntryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TransferItemEntry />} />
    <Route path="new" element={<TransferItemEntryUpdate />} />
    <Route path=":id">
      <Route index element={<TransferItemEntryDetail />} />
      <Route path="edit" element={<TransferItemEntryUpdate />} />
      <Route path="delete" element={<TransferItemEntryDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TransferItemEntryRoutes;
