import { ITransactionItem } from 'app/shared/model/transaction-item.model';
import { ISalesReceipt } from 'app/shared/model/sales-receipt.model';

export interface ITransferItemEntry {
  id?: number;
  description?: string | null;
  itemAmount?: number;
  transactionItem?: ITransactionItem;
  salesReceipt?: ISalesReceipt;
}

export const defaultValue: Readonly<ITransferItemEntry> = {};
