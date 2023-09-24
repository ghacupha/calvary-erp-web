import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AccountingEvent from './accounting-event';
import TransactionAccount from './transaction-account';
import TransactionEntry from './transaction-entry';
import AccountTransaction from './account-transaction';
import EventType from './event-type';
import DealerType from './dealer-type';
import Dealer from './dealer';
import TransactionAccountType from './transaction-account-type';
import TransactionCurrency from './transaction-currency';
import BalanceSheetItemType from './balance-sheet-item-type';
import BalanceSheetItemValue from './balance-sheet-item-value';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="accounting-event/*" element={<AccountingEvent />} />
        <Route path="transaction-account/*" element={<TransactionAccount />} />
        <Route path="transaction-entry/*" element={<TransactionEntry />} />
        <Route path="account-transaction/*" element={<AccountTransaction />} />
        <Route path="event-type/*" element={<EventType />} />
        <Route path="dealer-type/*" element={<DealerType />} />
        <Route path="dealer/*" element={<Dealer />} />
        <Route path="transaction-account-type/*" element={<TransactionAccountType />} />
        <Route path="transaction-currency/*" element={<TransactionCurrency />} />
        <Route path="balance-sheet-item-type/*" element={<BalanceSheetItemType />} />
        <Route path="balance-sheet-item-value/*" element={<BalanceSheetItemValue />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
