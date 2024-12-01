export interface NewsDetailDto {
  id:number;
  title: string;
  summary: string;
  content: string;
  imageUrl: string[] | null;
  date: Date;
}
