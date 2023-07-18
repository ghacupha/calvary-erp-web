import React from 'react';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/transaction-account">
        Transaction Account
      </MenuItem>
      <MenuItem icon="asterisk" to="/transaction-entry">
        Transaction Entry
      </MenuItem>
      <MenuItem icon="asterisk" to="/account-transaction">
        Account Transaction
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
