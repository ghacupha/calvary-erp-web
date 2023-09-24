import accountingEvent from 'app/entities/accounting-event/accounting-event.reducer';
import transactionAccount from 'app/entities/transaction-account/transaction-account.reducer';
import transactionEntry from 'app/entities/transaction-entry/transaction-entry.reducer';
import accountTransaction from 'app/entities/account-transaction/account-transaction.reducer';
import eventType from 'app/entities/event-type/event-type.reducer';
import dealerType from 'app/entities/dealer-type/dealer-type.reducer';
import dealer from 'app/entities/dealer/dealer.reducer';
import transactionAccountType from 'app/entities/transaction-account-type/transaction-account-type.reducer';
import transactionCurrency from 'app/entities/transaction-currency/transaction-currency.reducer';
import balanceSheetItemType from 'app/entities/balance-sheet-item-type/balance-sheet-item-type.reducer';
import balanceSheetItemValue from 'app/entities/balance-sheet-item-value/balance-sheet-item-value.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  accountingEvent,
  transactionAccount,
  transactionEntry,
  accountTransaction,
  eventType,
  dealerType,
  dealer,
  transactionAccountType,
  transactionCurrency,
  balanceSheetItemType,
  balanceSheetItemValue,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
