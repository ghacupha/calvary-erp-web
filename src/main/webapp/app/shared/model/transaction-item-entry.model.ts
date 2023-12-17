import { ITransactionItem } from 'app/shared/model/transaction-item.model';
import { ISalesReceipt } from 'app/shared/model/sales-receipt.model';

export interface ITransactionItemEntry {
  id?: number;
  description?: string | null;
  itemAmount?: number | null;
  transactionItem?: ITransactionItem;
  salesReceipt?: ISalesReceipt | null;
}

export const defaultValue: Readonly<ITransactionItemEntry> = {};
