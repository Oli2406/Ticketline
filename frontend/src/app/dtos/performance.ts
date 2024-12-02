export interface Performance {
  performanceId?: number; // Primary Key
  name: string;
  locationId: number; // Foreign Key
  date: string;
  price: number;
  artistId: number; // Foreign Key
  ticketNumber: number;
  hall: string;
}

export interface PerformanceListDto {
  performanceId: number;
  name: string;
  locationId: number; // Populated from Location
  date: string;
  price: number;
  artistId: number; // Populated from Artist
  ticketNumber: number;
  hall: string;
}
