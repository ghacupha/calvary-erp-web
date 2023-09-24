import { ITransactionAccount } from 'app/shared/model/transaction-account.model';
import { IAccountTransaction } from 'app/shared/model/account-transaction.model';
import { TransactionEntryTypes } from 'app/shared/model/enumerations/transaction-entry-types.model';

export interface ITransactionEntry {
  id?: number;
  entryAmount?: number | null;
  transactionEntryType?: TransactionEntryTypes;
  description?: string | null;
  wasProposed?: boolean | null;
  wasPosted?: boolean | null;
  wasDeleted?: boolean | null;
  wasApproved?: boolean | null;
  transactionAccount?: ITransactionAccount;
  accountTransaction?: IAccountTransaction | null;
}

export const defaultValue: Readonly<ITransactionEntry> = {
  wasProposed: false,
  wasPosted: false,
  wasDeleted: false,
  wasApproved: false,
};
