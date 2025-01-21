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

export interface ReservationDetailDto {
  reservedId: number;
  userId: number;
  tickets: TicketDto[];
  reservedDate: Date;

  performanceDetails: {
    [performanceId: number]: {
      name: string;
      artistName: string;
      locationName: string;
    };
  };
}
