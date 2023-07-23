import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import BalanceSheetItemType from './balance-sheet-item-type';
import BalanceSheetItemTypeDetail from './balance-sheet-item-type-detail';
import BalanceSheetItemTypeUpdate from './balance-sheet-item-type-update';
import BalanceSheetItemTypeDeleteDialog from './balance-sheet-item-type-delete-dialog';

const BalanceSheetItemTypeRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<BalanceSheetItemType />} />
    <Route path="new" element={<BalanceSheetItemTypeUpdate />} />
    <Route path=":id">
      <Route index element={<BalanceSheetItemTypeDetail />} />
      <Route path="edit" element={<BalanceSheetItemTypeUpdate />} />
      <Route path="delete" element={<BalanceSheetItemTypeDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default BalanceSheetItemTypeRoutes;
