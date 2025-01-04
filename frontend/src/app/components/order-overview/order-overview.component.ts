import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AuthService} from '../../services/auth.service';
import {PurchaseService} from '../../services/purchase.service';
import {ReservationService} from '../../services/reservation.service';
import {ToastrService} from 'ngx-toastr';
import {TicketDto} from '../../dtos/ticket';
import {PurchaseListDto} from '../../dtos/purchase';
import {ReservationListDto} from '../../dtos/reservation';
import {PerformanceService} from 'src/app/services/performance.service';
import {LocationService} from '../../services/location.service';
import {PerformanceListDto} from '../../dtos/performance';
import {ArtistService} from '../../services/artist.service';

@Component({
  selector: 'app-order-overview',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-overview.component.html',
  styleUrls: ['./order-overview.component.scss'],
})
export class OrderOverviewComponent implements OnInit {
  reservedTickets: { date: Date; reserved: TicketDto[]; showDetails: boolean }[] = [];
  purchasedTickets: { date: Date; purchased: TicketDto[]; showDetails: boolean }[] = [];
  pastTickets: { date: Date; purchased: TicketDto[]; showDetails: boolean }[] = [];
  performanceNames: { [performanceId: number]: PerformanceListDto } = {};
  artistCache: { [artistId: number]: string } = {};
  performanceLocations: { [locationId: number]: string } = {};
  userPurchases: PurchaseListDto[];
  userReservations: ReservationListDto[];
  cancelledPurchase: PurchaseListDto;

  constructor(
    private authService: AuthService,
    private purchaseService: PurchaseService,
    private reservationService: ReservationService,
    private toastr: ToastrService,
    private performanceService: PerformanceService,
    private locationService: LocationService,
    private artistService: ArtistService
  ) {
  }

  ngOnInit(): void {
    const userId = this.authService.getUserIdFromToken();
    if (userId) {
      this.loadUserPurchases(userId);
      this.loadUserReservations(userId);
    } else {
      this.toastr.error('Unable to identify the user.', 'Error');
    }
  }

  loadUserPurchases(userId: string): void {
    this.purchaseService.getPurchasesByUser(userId).subscribe({
      next: (purchases: PurchaseListDto[]) => {
        this.userPurchases = purchases;
        this.processPurchases(purchases);
      },
      error: (err) => {
        console.error('Error fetching purchases:', err.message);
        this.toastr.error('Failed to load your purchases. Please try again.', 'Error');
      },
    });
  }

  loadUserReservations(userId: string): void {
    this.reservationService.getReservationsByUser(userId).subscribe({
      next: (reservations: ReservationListDto[]) => {
        this.userReservations = reservations;
        this.processReservations(reservations);
      },
      error: (err) => {
        console.error('Error fetching reservations:', err.message);
        this.toastr.error('Failed to load your reservations. Please try again.', 'Error');
      },
    });
  }

  private processPurchases(purchases: PurchaseListDto[]): void {
    const today = new Date();
    const currentMap: { [key: string]: TicketDto[] } = {};
    const pastMap: { [key: string]: TicketDto[] } = {};

    purchases.forEach((purchase) => {
      const purchaseDate = new Date(purchase.purchaseDate);

      purchase.tickets.forEach((ticket) => {
        const eventDate = new Date(ticket.date);

        if (eventDate >= today) {
          const key = purchaseDate.toISOString();
          if (!currentMap[key]) {
            currentMap[key] = [];
          }
          if (ticket.status === 'SOLD') {
            currentMap[key].push(ticket);
          }
        } else if (ticket.status === 'SOLD') {
          const key = purchaseDate.toISOString();
          if (!pastMap[key]) {
            pastMap[key] = [];
          }
          pastMap[key].push(ticket);
        }
      });
    });

    this.purchasedTickets = Object.entries(currentMap)
    .map(([date, tickets]) => ({
      date: new Date(date),
      purchased: tickets,
      showDetails: false,
    }))
    .sort((a, b) => b.date.getTime() - a.date.getTime());

    this.pastTickets = Object.entries(pastMap)
    .map(([date, tickets]) => ({
      date: new Date(date),
      purchased: tickets,
      showDetails: false,
    }))
    .sort((a, b) => b.date.getTime() - a.date.getTime());
  }

  private processReservations(reservations: ReservationListDto[]): void {
    const today = new Date();
    // Setze die Zeit auf Mitternacht, um nur das Datum zu vergleichen

    console.log(reservations);

    this.reservedTickets = reservations
    .filter((reservation) =>
      reservation.tickets.every((ticket) => new Date(ticket.date) >= today) // Alle Tickets müssen ab heute sein
    )
    .map((reservation) => ({
      date: new Date(reservation.reservedDate),
      reserved: reservation.tickets,
      showDetails: false,
    }))
    .sort((a, b) => b.date.getTime() - a.date.getTime());
  }

  cancelPurchase(ticket: TicketDto) {
    console.log('cancel purchase ticket id ' + ticket.ticketId);
    let cancelledPurchaseId: number;

    this.userPurchases.forEach((purchase) => {
      purchase.tickets.forEach((purchaseTicket) => {
        if (purchaseTicket.ticketId === ticket.ticketId) {
          cancelledPurchaseId = purchase.purchaseId;
          console.log(cancelledPurchaseId);

          // Fetch the purchase details
          this.purchaseService.getPurchaseById(cancelledPurchaseId).subscribe({
            next: (purchase: PurchaseListDto) => {
              this.cancelledPurchase = purchase;
              console.log(this.cancelledPurchase, "not updated")

              const updatedPurchase: PurchaseListDto = {
                purchaseId: cancelledPurchaseId,
                userId: this.cancelledPurchase.userId,
                tickets: this.cancelledPurchase.tickets.filter(item => item.ticketId !== ticket.ticketId),
                merchandises: this.cancelledPurchase.merchandises,
                totalPrice: this.cancelledPurchase.totalPrice,
                purchaseDate: this.cancelledPurchase.purchaseDate
              };

              console.log(updatedPurchase, 'updated');

              // Update the purchase
              this.purchaseService.updatePurchase(updatedPurchase).subscribe({
                next: () => {
                  this.toastr.success('Purchase cancelled successfully.', 'Success');
                  this.loadUserPurchases(this.authService.getUserIdFromToken());
                },
                error: (err) => {
                  console.error('Error updating purchase:', err.message);
                  this.toastr.error('Failed to cancel the purchase. Please try again.', 'Error');
                },
              });
            },
            error: (err) => {
              console.error('Error fetching purchase:', err.message);
              this.toastr.error('Failed to load purchase details. Please try again.', 'Error');
            },
          });
        }
      });
    });
  }

  //TODO cancel reservations like purchased tickets
  cancelReservation(ticketId: number) {
    console.log('cancel reservation' + ticketId);
  }


  getPerformanceName(performanceId: number): string {
    if (!this.performanceNames[performanceId]) {
      this.performanceService.getPerformanceById(performanceId).subscribe({
        next: (performance) => {
          this.performanceNames[performanceId] = performance;
        },
        error: (err) => {
          console.error(`Failed to fetch performance with ID ${performanceId}:`, err);
          this.performanceNames[performanceId].name = 'Error loading name';
        },
      });
      return 'Loading...';
    }

    return this.performanceNames[performanceId].name;
  }

  getArtistName(performanceId: number): string {
    if (!this.performanceNames[performanceId]) {
      this.performanceService.getPerformanceById(performanceId).subscribe({
        next: (performance) => {
          this.performanceNames[performanceId] = performance;

          if (!this.artistCache[performance.artistId]) {
            this.loadArtistName(performance.artistId);
          }
        },
        error: (err) => {
          console.error(`Failed to fetch performance with ID ${performanceId}:`, err);
        },
      });
      return 'Loading...';
    }

    const artistId = this.performanceNames[performanceId].artistId;

    if (!this.artistCache[artistId]) {
      this.loadArtistName(artistId);
      return 'Loading artist...';
    }

    return this.artistCache[artistId];
  }

  private loadArtistName(artistId: number): void {
    this.artistService.getById(artistId).subscribe({
      next: (artist) => {
        if (artist.artistName != null) {
          this.artistCache[artistId] = artist.artistName;
        } else {
          this.artistCache[artistId] = artist.firstName + ' ' + artist.lastName;
        }
      },
      error: (err) => {
        console.error(`Failed to fetch artist with ID ${artistId}:`, err);
        this.artistCache[artistId] = 'Unknown Artist';
      },
    });
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
