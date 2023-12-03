import dayjs from 'dayjs';
import { ITransactionClass } from 'app/shared/model/transaction-class.model';
import { IDealer } from 'app/shared/model/dealer.model';
import { ITransactionItemEntry } from 'app/shared/model/transaction-item-entry.model';
import { ISalesReceiptTitle } from 'app/shared/model/sales-receipt-title.model';

export interface ISalesReceipt {
  id?: number;
  description?: string | null;
  transactionDate?: string;
  hasBeenEmailed?: boolean | null;
  hasBeenProposed?: boolean | null;
  shouldBeEmailed?: boolean | null;
  transactionClass?: ITransactionClass | null;
  dealer?: IDealer;
  transactionItemEntries?: ITransactionItemEntry[];
  salesReceiptTitle?: ISalesReceiptTitle;
}

export const defaultValue: Readonly<ISalesReceipt> = {
  hasBeenEmailed: false,
  hasBeenProposed: false,
  shouldBeEmailed: false,
};
