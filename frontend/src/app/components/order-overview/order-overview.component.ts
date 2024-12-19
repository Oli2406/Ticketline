import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { PurchaseService } from '../../services/purchase.service';
import { ToastrService } from 'ngx-toastr';
import { TicketDto } from '../../dtos/ticket';
import { PurchaseListDto } from '../../dtos/purchase';
import { PerformanceService } from 'src/app/services/performance.service';
import {LocationService} from "../../services/location.service";

@Component({
  selector: 'app-order-overview',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-overview.component.html',
  styleUrls: ['./order-overview.component.scss'],
})
export class OrderOverviewComponent implements OnInit {
  reservedTickets: TicketDto[] = [];
  purchasedTickets: TicketDto[] = [];
  sortedTickets: { date: Date; reserved: TicketDto[]; purchased: TicketDto[]; showDetails: boolean }[] = [];
  pastTickets: { date: Date; purchased: TicketDto[]; showDetails: boolean }[] = [];
  performanceNames: { [performanceId: number]: string } = {};
  performanceLocations: { [locationId: number]: string } = {};

  constructor(
    private authService: AuthService,
    private purchaseService: PurchaseService,
    private toastr: ToastrService,
    private performanceService: PerformanceService,
    private locationService: LocationService
  ) {}

  ngOnInit(): void {
    const userId = this.authService.getUserIdFromToken();
    if (userId) {
      this.loadUserPurchases(userId);
    } else {
      this.toastr.error('Unable to identify the user.', 'Error');
    }
  }

  loadUserPurchases(userId: string): void {
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
    const today = new Date();
    const currentMap: { [key: string]: { reserved: TicketDto[]; purchased: TicketDto[] } } = {};
    const pastMap: { [key: string]: { purchased: TicketDto[] } } = {};

    purchases.forEach((purchase) => {
      const purchaseDate = new Date(purchase.purchaseDate).toDateString();

      purchase.tickets.forEach((ticket) => {
        const eventDate = new Date(ticket.date);
        if (eventDate >= today) {
          // Aktuelle Käufe (zukünftige Veranstaltungen)
          if (!currentMap[purchaseDate]) {
            currentMap[purchaseDate] = { reserved: [], purchased: [] };
          }
          if (ticket.status === 'RESERVED') {
            currentMap[purchaseDate].reserved.push(ticket);
          } else if (ticket.status === 'SOLD') {
            currentMap[purchaseDate].purchased.push(ticket);
          }
        } else if (ticket.status === 'SOLD') {
          // Vergangene Käufe (nur gekaufte Tickets)
          if (!pastMap[purchaseDate]) {
            pastMap[purchaseDate] = { purchased: [] };
          }
          pastMap[purchaseDate].purchased.push(ticket);
        }
      });
    });

    // Convert currentMap to sorted array (by date descending)
    this.sortedTickets = Object.entries(currentMap)
      .map(([date, tickets]) => ({
        date: new Date(date), // Purchase date
        reserved: tickets.reserved,
        purchased: tickets.purchased,
        showDetails: false,
      }))
      .sort((a, b) => b.date.getTime() - a.date.getTime());

    // Convert pastMap to sorted array (by date descending)
    this.pastTickets = Object.entries(pastMap)
      .map(([date, tickets]) => ({
        date: new Date(date), // Purchase date
        purchased: tickets.purchased,
        showDetails: false,
      }))
      .sort((a, b) => b.date.getTime() - a.date.getTime());
  }

  getPerformanceName(performanceId: number): string {
    if (!this.performanceNames[performanceId]) {
      this.performanceService.getPerformanceById(performanceId).subscribe({
        next: (performance) => {
          this.performanceNames[performanceId] = performance.name;
        },
        error: (err) => {
          console.error(`Failed to fetch performance with ID ${performanceId}:`, err);
          this.performanceNames[performanceId] = 'Error loading name'; // Setze einen Fallback
        },
      });
      return 'Loading...'; // Platzhalter, während der Name geladen wird
    }

    return this.performanceNames[performanceId]; // Gibt den Namen zurück, wenn er schon geladen wurde
  }

  getPerformanceLocation(performanceId: number): string {
    if (!this.performanceLocations[performanceId]) {
      this.performanceService.getPerformanceById(performanceId).subscribe({
        next: (performance) => {
          if (performance.locationId) {
            this.locationService.getById(performance.locationId).subscribe({
              next: (location) => {
                this.performanceLocations[performanceId] = location.name;
              },
              error: (err) => {
                console.error(`Error fetching location details for ID ${performance.locationId}:`, err);
                this.performanceLocations[performanceId] = 'Error loading location';
              },
            });
          } else {
            this.performanceLocations[performanceId] = 'Location not found';
          }
        },
        error: (err) => {
          console.error(`Error fetching performance with ID ${performanceId}:`, err);
          this.performanceLocations[performanceId] = 'Error loading performance';
        },
      });
      return 'Loading...';
    }
    return this.performanceLocations[performanceId];
  }
}
