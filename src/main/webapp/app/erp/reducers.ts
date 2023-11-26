import accountingEvent from 'app/erp/accounting-event/accounting-event.reducer';
import transactionAccount from 'app/erp/transaction-account/transaction-account.reducer';
import transactionEntry from 'app/erp/transaction-entry/transaction-entry.reducer';
import accountTransaction from 'app/erp/account-transaction/account-transaction.reducer';
import eventType from 'app/erp/event-type/event-type.reducer';
import dealerType from 'app/erp/dealer-type/dealer-type.reducer';
import dealer from 'app/erp/dealer/dealer.reducer';
import transactionAccountType from 'app/erp/transaction-account-type/transaction-account-type.reducer';
import transactionCurrency from 'app/erp/transaction-currency/transaction-currency.reducer';
import balanceSheetItemType from 'app/erp/balance-sheet-item-type/balance-sheet-item-type.reducer';
import balanceSheetItemValue from 'app/erp/balance-sheet-item-value/balance-sheet-item-value.reducer';
import salesReceipt from 'app/erp/sales-receipt/sales-receipt.reducer';
import transactionClass from 'app/erp/transaction-class/transaction-class.reducer';
import transactionItem from 'app/erp/transaction-item/transaction-item.reducer';
import transactionItemAmount from 'app/erp/transaction-item-amount/transaction-item-amount.reducer';
import transactionItemEntry from 'app/erp/transaction-item-entry/transaction-item-entry.reducer';
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
  salesReceipt,
  transactionClass,
  transactionItem,
  transactionItemAmount,
  transactionItemEntry,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
