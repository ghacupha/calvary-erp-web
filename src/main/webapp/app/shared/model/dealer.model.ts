import { IDealerType } from 'app/shared/model/dealer-type.model';

export interface IDealer {
  id?: number;
  name?: string;
  mainEmail?: string | null;
  dealerType?: IDealerType;
}

export const defaultValue: Readonly<IDealer> = {};
