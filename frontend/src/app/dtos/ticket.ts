export enum TicketType {
  SEATED = 'SEATED',
  STANDING = 'STANDING',
}

export enum SectorType {
  A = 'A',
  B = 'B',
  C = 'C',
}

export enum PriceCategory {
  STANDARD = 'STANDARD',
  PREMIUM = 'PREMIUM',
  VIP = 'VIP',
}

export enum Hall {
  A = 'A',
  B = 'B',
  C = 'C',
}

export interface TicketDto {
  ticketId: number;
  rowNumber?: number;
  seatNumber?: number;
  priceCategory: PriceCategory;
  ticketType: TicketType;
  sectorType: SectorType;
  price: number;
  status: string; // e.g., "AVAILABLE", "RESERVED", "SOLD"
  performanceId: number;
  reservationNumber?: number;
  hall: Hall;
  date: Date;
  reservedUntil?: string;
}

export interface Ticket {
  ticketId?: number;
  rowNumber: number;
  seatNumber: number;
  priceCategory: PriceCategory;
  ticketType: TicketType;
  sectorType: SectorType;
  price: number;
  status: string; // e.g., "AVAILABLE", "RESERVED", "SOLD"
  performanceId: number;
  reservationNumber: number;
  hall: Hall;
  date: Date;
}
