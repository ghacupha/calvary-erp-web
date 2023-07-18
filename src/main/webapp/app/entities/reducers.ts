import transactionAccount from 'app/entities/transaction-account/transaction-account.reducer';
import transactionEntry from 'app/entities/transaction-entry/transaction-entry.reducer';
import accountTransaction from 'app/entities/account-transaction/account-transaction.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  transactionAccount,
  transactionEntry,
  accountTransaction,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
