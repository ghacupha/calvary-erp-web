import transactionAccount from 'app/entities/transaction-account/transaction-account.reducer';
import transactionEntry from 'app/entities/transaction-entry/transaction-entry.reducer';
import accountTransaction from 'app/entities/account-transaction/account-transaction.reducer';
import eventType from 'app/entities/event-type/event-type.reducer';
import accountingEvent from 'app/entities/accounting-event/accounting-event.reducer';
import dealerType from 'app/entities/dealer-type/dealer-type.reducer';
import dealer from 'app/entities/dealer/dealer.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  transactionAccount,
  transactionEntry,
  accountTransaction,
  eventType,
  accountingEvent,
  dealerType,
  dealer,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
