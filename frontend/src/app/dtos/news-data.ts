export interface NewsDto {
  id?: number;
  title: string;
  summary: string;
  content: string;
  images: string[] | null;
  date: Date;
}

export interface NewsDetailDto {
  id:number;
  title: string;
  summary: string;
  content: string;
  images: string[] | null;
  date: Date;
}
