import dayjs from 'dayjs';
import { IBalanceSheetItemType } from 'app/shared/model/balance-sheet-item-type.model';

export interface IBalanceSheetItemValue {
  id?: number;
  shortDescription?: string | null;
  effectiveDate?: string;
  itemAmount?: number;
  itemType?: IBalanceSheetItemType;
}

export const defaultValue: Readonly<IBalanceSheetItemValue> = {};
