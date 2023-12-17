import dayjs from 'dayjs';
import { ITransactionClass } from 'app/shared/model/transaction-class.model';
import { IDealer } from 'app/shared/model/dealer.model';
import { ISalesReceiptTitle } from 'app/shared/model/sales-receipt-title.model';
import { ITransactionItemEntry } from 'app/shared/model/transaction-item-entry.model';
import { ITransferItemEntry } from 'app/shared/model/transfer-item-entry.model';

export interface ISalesReceipt {
  id?: number;
  description?: string | null;
  transactionDate?: string;
  hasBeenEmailed?: boolean | null;
  hasBeenProposed?: boolean | null;
  shouldBeEmailed?: boolean | null;
  transactionClass?: ITransactionClass | null;
  dealer?: IDealer;
  salesReceiptTitle?: ISalesReceiptTitle;
  transactionItemEntries?: ITransactionItemEntry[] | null;
  transferItemEntries?: ITransferItemEntry[] | null;
}

export const defaultValue: Readonly<ISalesReceipt> = {
  hasBeenEmailed: false,
  hasBeenProposed: false,
  shouldBeEmailed: false,
};
