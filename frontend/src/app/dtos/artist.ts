export interface Artist {
  artistId?: number; // Primary Key
  firstName: string;
  surname: string;
  artistName: string;
}

export interface ArtistListDto {
  artistId: number;
  firstName: string;
  surname: string;
  artistName: string;
}

export interface ArtistSearch {
  firstName?: string;
  surname?: string;
  artistName?: string;
}
