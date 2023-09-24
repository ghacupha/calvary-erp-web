import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import BalanceSheetItemValue from './balance-sheet-item-value';
import BalanceSheetItemValueDetail from './balance-sheet-item-value-detail';
import BalanceSheetItemValueUpdate from './balance-sheet-item-value-update';
import BalanceSheetItemValueDeleteDialog from './balance-sheet-item-value-delete-dialog';

const BalanceSheetItemValueRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<BalanceSheetItemValue />} />
    <Route path="new" element={<BalanceSheetItemValueUpdate />} />
    <Route path=":id">
      <Route index element={<BalanceSheetItemValueDetail />} />
      <Route path="edit" element={<BalanceSheetItemValueUpdate />} />
      <Route path="delete" element={<BalanceSheetItemValueDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default BalanceSheetItemValueRoutes;
