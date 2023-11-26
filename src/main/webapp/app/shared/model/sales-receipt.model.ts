import { ITransactionClass } from 'app/shared/model/transaction-class.model';
import { IDealer } from 'app/shared/model/dealer.model';
import { ITransactionItemEntry } from 'app/shared/model/transaction-item-entry.model';

export interface ISalesReceipt {
  id?: number;
  salesReceiptTitle?: string | null;
  description?: string | null;
  transactionClass?: ITransactionClass | null;
  dealer?: IDealer;
  transactionItemEntries?: ITransactionItemEntry[];
}

export const defaultValue: Readonly<ISalesReceipt> = {};
