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
  performanceIds?: number[];
}

export interface EventSearch {
  title?: string;
  category?: string;
  dateEarliest?: Date;
  dateLatest?: Date;
  minDuration?: number;
  maxDuration?: number;
  performanceIds?: number[];
}
