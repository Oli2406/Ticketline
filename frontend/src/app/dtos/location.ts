export interface Location {
  locationId?: number;
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
  street?: string;
  city?: string;
  postalCode?: string;
  country?: string;
}
