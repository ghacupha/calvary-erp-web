import { TransactionAccountType } from 'app/shared/model/enumerations/transaction-account-type.model';

export interface ITransactionAccount {
  id?: number;
  accountName?: string;
  accountNumber?: string | null;
  transactionAccountType?: keyof typeof TransactionAccountType;
  openingBalance?: number | null;
  parentAccount?: ITransactionAccount | null;
}

export const defaultValue: Readonly<ITransactionAccount> = {};
