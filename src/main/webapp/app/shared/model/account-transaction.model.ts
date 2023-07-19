import dayjs from 'dayjs';
import { ITransactionEntry } from 'app/shared/model/transaction-entry.model';

export interface IAccountTransaction {
  id?: number;
  transactionDate?: string;
  description?: string | null;
  referenceNumber?: string | null;
  posted?: boolean | null;
  transactionEntries?: ITransactionEntry[] | null;
}

export const defaultValue: Readonly<IAccountTransaction> = {
  posted: false,
};
