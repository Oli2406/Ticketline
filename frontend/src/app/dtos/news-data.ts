export interface NewsData{
  id?: number;
  title: string;
  summary: string;
  content: string;
  imageUrl: string[]|null;
  dateOfNews: Date;
}
