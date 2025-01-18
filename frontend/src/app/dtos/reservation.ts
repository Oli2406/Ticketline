import {TicketDto} from "./ticket";

export interface Reservation {
  reservedId?: number; // Primary Key
  userId: String;
  ticketIds: number[];
  reservedDate: string;
}

export interface ReservationListDto {
  reservedId: number;
  userId: number;
  tickets: TicketDto[];
  reservedDate: Date;
}
