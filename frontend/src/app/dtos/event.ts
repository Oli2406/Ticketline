export interface Event {
  eventId?: number; // Primary Key
  title: string;
  dateOfEvent: Date;
  category: string;
  description: string;
  duration: number;
  performanceIds?: number[];
}

export interface EventListDto {
  eventId: number;
  title: string;
  dateOfEvent: Date;
  category: string;
  description: string;
  duration: number;
}

export interface EventSearch {
  title?: string;
  category?: string;
  dateEarliest?: string;
  dateLatest?: string;
  minDuration?: number;
  maxDuration?: number;
}
