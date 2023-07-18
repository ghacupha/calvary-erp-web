export interface ITransactionAccount {
  id?: number;
  accountName?: string;
  accountNumber?: string | null;
  accountBalance?: number | null;
  parentAccount?: ITransactionAccount | null;
}

export const defaultValue: Readonly<ITransactionAccount> = {};
