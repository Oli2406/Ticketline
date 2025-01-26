import {Merchandise} from "./merchandise";
import {TicketDto} from "./ticket";

export interface Purchase {
  purchaseId?: number;
  userId: String;
  ticketIds: number[];
  merchandiseIds: number[];
  merchandiseQuantities: number[];
  totalPrice: number;
  purchaseDate: string;
  street: string;
  postalCode: string;
  city: string;
}

export interface PurchaseListDto {
  purchaseId: number;
  userId: number;
  tickets: TicketDto[];
  merchandises: Merchandise[];
  totalPrice: number;
  purchaseDate: Date;
  street: string;
  postalCode: string;
  city: string;
}

export interface PurchaseDetailDto {
  purchaseId: number;
  userId: number;
  tickets: TicketDto[];
  merchandises: Merchandise[];
  totalPrice: number;
  purchaseDate: Date;
  street: string;
  postalCode: string;
  city: string;
  performanceDetails: {
    [performanceId: number]: {
      name: string;
      artistName: string;
      locationName: string;
    };
  };
}
