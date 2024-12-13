import {Merchandise} from "./merchandise";
import {Ticket} from "./ticket";

export interface Purchase {
  purchaseId?: number; // Primary Key
  userId: number;
  ticketIds: number[];
  merchandiseIds: number[];
  totalPrice: number;
  purchaseDate: Date;
}

export interface PurchaseListDto {
  purchaseId: number;
  userId: number;
  tickets: Ticket[];
  merchandises: Merchandise[];
  totalPrice: number;
  purchaseDate: Date;
}
