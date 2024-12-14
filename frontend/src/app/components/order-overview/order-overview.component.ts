import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { PurchaseService } from '../../services/purchase.service';
import { ToastrService } from 'ngx-toastr';
import { TicketDto } from '../../dtos/ticket';
import { PurchaseListDto } from '../../dtos/purchase';
import jwtDecode from 'jwt-decode';

@Component({
  selector: 'app-user-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-overview.component.html',
  styleUrls: ['./order-overview.component.scss'],
})
export class OrderOverviewComponent implements OnInit {
  reservedTickets: TicketDto[] = [];
  purchasedTickets: TicketDto[] = [];
  sortedTickets: { date: Date; reserved: TicketDto[]; purchased: TicketDto[] }[] = [];

  constructor(
    private authService: AuthService,
    private purchaseService: PurchaseService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    const userId = this.authService.getUserIdFromToken();
    if (userId) {
      const decoded: any = userId;
      const numericUserId = parseInt(decoded.id, 10);
      this.loadUserPurchases(numericUserId);
      console.log('User ID:', decoded);
      console.log(numericUserId);
    } else {
      this.toastr.error('Unable to identify the user.', 'Error');
    }
  }

  private decodeBase64Url(encodedString: string): string {
    const base64 = encodedString.replace(/-/g, '+').replace(/_/g, '/');
    const paddedBase64 = base64.padEnd(base64.length + (4 - (base64.length % 4)) % 4, '=');
    return atob(paddedBase64);
  }

  loadUserPurchases(userId: number): void {
    this.purchaseService.getPurchasesByUser(userId).subscribe({
      next: (purchases: PurchaseListDto[]) => {
        this.processPurchases(purchases);
      },
      error: (err) => {
        console.error('Error fetching purchases:', err.message);
        this.toastr.error('Failed to load your purchases. Please try again.', 'Error');
      },
    });
  }

  private processPurchases(purchases: PurchaseListDto[]): void {
    // Map to organize tickets by date
    const ticketMap: { [key: string]: { reserved: TicketDto[]; purchased: TicketDto[] } } = {};

    purchases.forEach((purchase) => {
      purchase.tickets.forEach((ticket) => {
        const ticketDate = new Date(ticket.date).toDateString();
        if (!ticketMap[ticketDate]) {
          ticketMap[ticketDate] = { reserved: [], purchased: [] };
        }
        if (ticket.status === 'RESERVED') {
          ticketMap[ticketDate].reserved.push(ticket);
        } else if (ticket.status === 'PURCHASED') {
          ticketMap[ticketDate].purchased.push(ticket);
        }
      });
    });

    // Convert ticketMap to sorted array
    this.sortedTickets = Object.entries(ticketMap)
      .map(([date, tickets]) => ({
        date: new Date(date),
        reserved: tickets.reserved,
        purchased: tickets.purchased,
      }))
      .sort((a, b) => b.date.getTime() - a.date.getTime()); // Sort by descending date
  }
}
