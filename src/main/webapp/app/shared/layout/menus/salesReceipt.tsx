import React from 'react';

import { NavDropdown } from './menu-components';
import SalesReceiptMenuItems from 'app/erp/sales-receipt-menu';

export const SalesReceiptMenu = () => (
  <NavDropdown icon="th-list" name="Sales Receipt" id="sales-receipt-menu" data-cy="entity" style={{ maxHeight: '80vh', overflow: 'auto' }}>
    <SalesReceiptMenuItems />
  </NavDropdown>
);
