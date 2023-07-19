import dayjs from 'dayjs';
import { IEventType } from 'app/shared/model/event-type.model';
import { IDealer } from 'app/shared/model/dealer.model';

export interface IAccountingEvent {
  id?: number;
  eventDate?: string;
  eventType?: IEventType | null;
  dealer?: IDealer;
}

export const defaultValue: Readonly<IAccountingEvent> = {};
