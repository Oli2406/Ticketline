export enum TicketType {
  SEATED = 'SEATED',
  STANDING = 'STANDING',
}

export enum SectorType {
  A = 'A',
  B = 'B',
}

export enum PriceCategory {
  STANDARD = 'STANDARD',
  PREMIUM = 'PREMIUM',
  VIP = 'VIP',
}

export interface TicketDto {
  ticketId: number;
  rowNumber?: number;
  seatNumber?: number;
  priceCategory: PriceCategory;
  ticketType: TicketType; // Updated
  sectorType: SectorType; // New sector enum
  price: number;
  status: string; // e.g., "AVAILABLE", "RESERVED", "SOLD"
  performanceId: number;
  reservationNumber?: number;
}
