import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SalesReceiptTitle from './sales-receipt-title';
import SalesReceiptTitleDetail from './sales-receipt-title-detail';
import SalesReceiptTitleUpdate from './sales-receipt-title-update';
import SalesReceiptTitleDeleteDialog from './sales-receipt-title-delete-dialog';

const SalesReceiptTitleRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SalesReceiptTitle />} />
    <Route path="new" element={<SalesReceiptTitleUpdate />} />
    <Route path=":id">
      <Route index element={<SalesReceiptTitleDetail />} />
      <Route path="edit" element={<SalesReceiptTitleUpdate />} />
      <Route path="delete" element={<SalesReceiptTitleDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SalesReceiptTitleRoutes;
