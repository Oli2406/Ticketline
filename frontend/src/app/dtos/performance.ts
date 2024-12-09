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
  locationId: number;
  date: string;
  price: number;
  artistId: number;
  ticketNumber: number;
  hall: string;
}

export interface PerformanceWithNamesDto {
  performanceId: number;
  name: string;
  locationName: string;
  date: string;
  price: number;
  artistName: string;
  ticketNumber: number;
  hall: string;
}
