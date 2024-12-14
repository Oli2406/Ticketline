import {Location, LocationListDto} from "./location";
import {ArtistListDto} from "./artist";

export interface Performance {
  performanceId?: number;
  name: string;
  locationId: number;
  date: Date;
  price: number;
  artistId: number;
  ticketNumber: number;
  hall: string;
  duration: number;
}

export interface PerformanceListDto {
  performanceId: number;
  name: string;
  locationId: number;
  date: Date;
  price: number;
  artistId: number;
  ticketNumber: number;
  hall: string;
  duration: number;
}

export interface PerformanceDetailDto {
  performanceId: number;
  name: string;
  location: LocationListDto;
  date: Date;
  price: number;
  artist: ArtistListDto;
  ticketNumber: number;
  hall: string;
  duration: number;
}

export interface PerformanceSearch {
  date?: string;
  price?: number;
  hall?: string;
}
