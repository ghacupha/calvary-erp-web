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
      <MenuItem icon="asterisk" to="/event-type">
        <Translate contentKey="global.menu.entities.eventType" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/accounting-event">
        <Translate contentKey="global.menu.entities.accountingEvent" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/dealer-type">
        <Translate contentKey="global.menu.entities.dealerType" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/dealer">
        <Translate contentKey="global.menu.entities.dealer" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/transaction-account-type">
        <Translate contentKey="global.menu.entities.transactionAccountType" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/transaction-currency">
        <Translate contentKey="global.menu.entities.transactionCurrency" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
