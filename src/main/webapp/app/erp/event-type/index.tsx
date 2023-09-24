import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import EventType from './event-type';
import EventTypeDetail from './event-type-detail';
import EventTypeUpdate from './event-type-update';
import EventTypeDeleteDialog from './event-type-delete-dialog';

const EventTypeRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<EventType />} />
    <Route path="new" element={<EventTypeUpdate />} />
    <Route path=":id">
      <Route index element={<EventTypeDetail />} />
      <Route path="edit" element={<EventTypeUpdate />} />
      <Route path="delete" element={<EventTypeDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default EventTypeRoutes;
