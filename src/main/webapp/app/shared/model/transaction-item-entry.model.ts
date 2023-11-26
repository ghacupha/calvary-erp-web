import { ITransactionItem } from 'app/shared/model/transaction-item.model';

export interface ITransactionItemEntry {
  id?: number;
  description?: string | null;
  itemAmount?: number | null;
  transactionItem?: ITransactionItem;
}

export const defaultValue: Readonly<ITransactionItemEntry> = {};
