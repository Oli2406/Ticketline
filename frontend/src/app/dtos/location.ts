export interface Location {
  locationId?: number; // Primary Key
  name: string;
  street: string;
  city: string;
  postalCode: string;
  country: string;
}

export interface LocationListDto {
  locationId: number;
  name: string;
  street: string;
  city: string;
  postalCode: string;
  country: string;
}

export interface LocationSearch {
  name?: string;
  city?: string;
  postalCode?: string;
  country?: string;
}
