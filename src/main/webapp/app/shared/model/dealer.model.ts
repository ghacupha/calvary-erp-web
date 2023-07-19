import { IDealerType } from 'app/shared/model/dealer-type.model';

export interface IDealer {
  id?: number;
  name?: string;
  dealerType?: IDealerType;
}

export const defaultValue: Readonly<IDealer> = {};
