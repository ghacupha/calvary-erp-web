import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/transaction-account">
        <Translate contentKey="global.menu.entities.transactionAccount" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/transaction-entry">
        <Translate contentKey="global.menu.entities.transactionEntry" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/account-transaction">
        <Translate contentKey="global.menu.entities.accountTransaction" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
