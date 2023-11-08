import { NavDropdown } from 'app/shared/layout/menus/menu-components';
import ERPMenuItems from 'app/erp/menu';
import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { NavItem, NavLink } from 'reactstrap';
import { NavLink as Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export const AboutMenu = () => (
  <NavItem>
    <NavLink tag={Link} to="/about-erp" className="d-flex align-items-center">
      <FontAwesomeIcon icon="eye" />
      <span>About</span>
    </NavLink>
  </NavItem>
);
