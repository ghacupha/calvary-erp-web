import { ITransactionAccount } from 'app/shared/model/transaction-account.model';
import { TransactionEntryTypes } from 'app/shared/model/enumerations/transaction-entry-types.model';

export interface ITransactionEntry {
  id?: number;
  entryAmount?: number | null;
  transactionEntryType?: keyof typeof TransactionEntryTypes;
  transactionAccount?: ITransactionAccount;
}

export const defaultValue: Readonly<ITransactionEntry> = {};
