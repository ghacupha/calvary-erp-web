import { ITransactionAccount } from 'app/shared/model/transaction-account.model';

export interface IBalanceSheetItemType {
  id?: number;
  itemSequence?: number;
  itemNumber?: string;
  shortDescription?: string | null;
  transactionAccount?: ITransactionAccount;
  parentItem?: IBalanceSheetItemType | null;
}

export const defaultValue: Readonly<IBalanceSheetItemType> = {};
