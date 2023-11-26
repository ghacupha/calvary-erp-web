import { ITransactionClass } from 'app/shared/model/transaction-class.model';
import { ITransactionAccount } from 'app/shared/model/transaction-account.model';

export interface ITransactionItem {
  id?: number;
  itemName?: string;
  description?: string | null;
  transactionClass?: ITransactionClass | null;
  transactionAccount?: ITransactionAccount;
}

export const defaultValue: Readonly<ITransactionItem> = {};
