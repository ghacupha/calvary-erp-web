import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Dealer from './dealer';
import DealerDetail from './dealer-detail';
import DealerUpdate from './dealer-update';
import DealerDeleteDialog from './dealer-delete-dialog';

const DealerRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Dealer />} />
    <Route path="new" element={<DealerUpdate />} />
    <Route path=":id">
      <Route index element={<DealerDetail />} />
      <Route path="edit" element={<DealerUpdate />} />
      <Route path="delete" element={<DealerDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DealerRoutes;
