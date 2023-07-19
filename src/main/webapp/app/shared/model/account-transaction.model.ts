import dayjs from 'dayjs';
import { ITransactionEntry } from 'app/shared/model/transaction-entry.model';

export interface IAccountTransaction {
  id?: number;
  transactionDate?: string;
  description?: string | null;
  referenceNumber?: string | null;
  wasProposed?: boolean | null;
  wasPosted?: boolean | null;
  wasDeleted?: boolean | null;
  wasApproved?: boolean | null;
  transactionEntries?: ITransactionEntry[] | null;
}

export const defaultValue: Readonly<IAccountTransaction> = {
  wasProposed: false,
  wasPosted: false,
  wasDeleted: false,
  wasApproved: false,
};
