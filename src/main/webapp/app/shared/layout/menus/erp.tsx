import React from 'react';

import { NavDropdown } from './menu-components';
import ERPMenuItems from 'app/erp/menu';

export const ERPMenu = () => (
  <NavDropdown icon="th-list" name="ERP" id="erp-menu" data-cy="entity" style={{ maxHeight: '80vh', overflow: 'auto' }}>
    <ERPMenuItems />
  </NavDropdown>
);
