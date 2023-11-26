import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SalesReceipt from './sales-receipt';
import SalesReceiptDetail from './sales-receipt-detail';
import SalesReceiptUpdate from './sales-receipt-update';
import SalesReceiptDeleteDialog from './sales-receipt-delete-dialog';

const SalesReceiptRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SalesReceipt />} />
    <Route path="new" element={<SalesReceiptUpdate />} />
    <Route path=":id">
      <Route index element={<SalesReceiptDetail />} />
      <Route path="edit" element={<SalesReceiptUpdate />} />
      <Route path="delete" element={<SalesReceiptDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SalesReceiptRoutes;
