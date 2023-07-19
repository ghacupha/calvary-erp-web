import { ITransactionAccountType } from 'app/shared/model/transaction-account-type.model';
import { ITransactionCurrency } from 'app/shared/model/transaction-currency.model';

export interface ITransactionAccount {
  id?: number;
  accountName?: string;
  accountNumber?: string | null;
  openingBalance?: number | null;
  parentAccount?: ITransactionAccount | null;
  transactionAccountType?: ITransactionAccountType;
  transactionCurrency?: ITransactionCurrency;
}

export const defaultValue: Readonly<ITransactionAccount> = {};
