export interface Event {
  eventId?: number;
  title: string;
  dateFrom: Date;
  dateTo: Date;
  category: string;
  description: string;
  performanceIds?: number[];
}

export interface EventListDto {
  eventId: number;
  title: string;
  dateFrom: Date;
  dateTo: Date;
  category: string;
  description: string;
}

export interface EventSearch {
  title?: string;
  category?: string;
  dateEarliest?: string;
  dateLatest?: string;
  performanceIds?: number[];
}

export interface EventSalesDto {
  eventId: number;
  eventTitle: string;
  soldTickets: number;
}
