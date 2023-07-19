import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AccountingEvent from './accounting-event';
import AccountingEventDetail from './accounting-event-detail';
import AccountingEventUpdate from './accounting-event-update';
import AccountingEventDeleteDialog from './accounting-event-delete-dialog';

const AccountingEventRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AccountingEvent />} />
    <Route path="new" element={<AccountingEventUpdate />} />
    <Route path=":id">
      <Route index element={<AccountingEventDetail />} />
      <Route path="edit" element={<AccountingEventUpdate />} />
      <Route path="delete" element={<AccountingEventDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AccountingEventRoutes;
