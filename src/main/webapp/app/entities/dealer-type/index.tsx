import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import DealerType from './dealer-type';
import DealerTypeDetail from './dealer-type-detail';
import DealerTypeUpdate from './dealer-type-update';
import DealerTypeDeleteDialog from './dealer-type-delete-dialog';

const DealerTypeRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<DealerType />} />
    <Route path="new" element={<DealerTypeUpdate />} />
    <Route path=":id">
      <Route index element={<DealerTypeDetail />} />
      <Route path="edit" element={<DealerTypeUpdate />} />
      <Route path="delete" element={<DealerTypeDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DealerTypeRoutes;
