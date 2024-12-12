export interface Artist {
  artistId?: number; // Primary Key
  firstName: string;
  lastName: string;
  artistName: string;
}

export interface ArtistListDto {
  artistId: number;
  firstName: string;
  lastName: string;
  artistName: string;
}

export interface ArtistSearch {
  firstName?: string;
  lastName?: string;
  artistName?: string;
}
