import { ITransactionItem } from 'app/shared/model/transaction-item.model';

export interface ITransactionItemAmount {
  id?: number;
  transactionItemAmount?: number;
  transactionItem?: ITransactionItem;
}

export const defaultValue: Readonly<ITransactionItemAmount> = {};
