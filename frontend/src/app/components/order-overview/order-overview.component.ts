import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AuthService} from '../../services/auth.service';
import {PurchaseService} from '../../services/purchase.service';
import {ReservationService} from '../../services/reservation.service';
import {ToastrService} from 'ngx-toastr';
import {Hall, PriceCategory, SectorType, TicketDto, TicketType} from '../../dtos/ticket';
import {PurchaseListDto} from '../../dtos/purchase';
import {ReservationListDto} from '../../dtos/reservation';
import {PerformanceService} from 'src/app/services/performance.service';
import {LocationService} from '../../services/location.service';
import {PerformanceListDto} from '../../dtos/performance';
import {ArtistService} from '../../services/artist.service';
import {ReceiptService} from "../../services/receipt.service";
import {
  ConfirmationDialogMode,
  ConfirmDialogComponent
} from "../confirm-dialog/confirm-dialog.component";
import {catchError, generate, map} from "rxjs";
import {transform} from "lodash";
import {TicketService} from "../../services/ticket.service";
import {CartService} from "../../services/cart.service";
import {Merchandise} from "../../dtos/merchandise";

@Component({
  selector: 'app-order-overview',
  standalone: true,
  imports: [CommonModule, ConfirmDialogComponent],
  templateUrl: './order-overview.component.html',
  styleUrls: ['./order-overview.component.scss'],
})
export class OrderOverviewComponent implements OnInit {
  reservedTickets: {
    date: Date;
    reserved: TicketDto[];
    reservedId: number;
    showDetails: boolean
  }[] = [];
  purchasedTickets: { date: Date; purchased: TicketDto[]; showDetails: boolean }[] = [];
  pastTickets: { date: Date; purchased: TicketDto[]; showDetails: boolean }[] = [];
  performanceNames: { [performanceId: number]: PerformanceListDto } = {};
  performanceDate: Date;
  artistCache: { [artistId: number]: string } = {};
  performanceLocations: { [locationId: number]: string } = {};
  userPurchases: PurchaseListDto[];
  userReservations: ReservationListDto[];
  cancelledPurchase: PurchaseListDto;
  cancelledReservation: ReservationListDto;
  cancelledTicket: TicketDto = {
    ticketId: 1,
    rowNumber: 1,
    seatNumber: 1,
    priceCategory: PriceCategory.STANDARD,
    ticketType: TicketType.SEATED, // Updated
    sectorType: SectorType.A, // New sector enum
    price: 1,
    status: '', // e.g., "AVAILABLE", "RESERVED", "SOLD"
    performanceId: 1,
    reservationNumber: 1,
    hall: Hall.A,
    date: new Date(),
    reservedUntil: ''
  };
  cancelledTickets: TicketDto[];

  //variables for downloading the invoice again
  invoiceTickets: TicketDto[];
  invoicePurchase: PurchaseListDto ={
    purchaseId: 1,
    userId: 1,
    tickets: [],
    merchandises: [],
    totalPrice: 0,
    purchaseDate: new Date(),
    street: '',
    postalCode: '',
    city: ''
  }

  address = {
    street: 'ExampleStreet',
    postalCode: 'ExamplePostalCode',
    city: 'ExampleCity',
  };

  //load user details for the invoice
  userFirstName: string;
  userLastName: string;
  userEmail: string;
  invoiceCounter: number = 1;

  //confirmation dialogue for cancelling a ticket(s)
  ConfirmationDialogMode = ConfirmationDialogMode;
  showConfirmDeletionDialogPTicket = false;
  showConfirmDeletionDialogReTicket = false;
  showConfirmDeletionDialogAllP = false;
  showConfirmDeletionDialogAllRes = false;
  cancelMessagePurchase = 'Do you really want to cancel your purchased ticket(s)?';
  cancelMessageReservation = 'Do you really want to cancel your reservation(s)?';

  constructor(
    private authService: AuthService,
    private purchaseService: PurchaseService,
    private reservationService: ReservationService,
    private toastr: ToastrService,
    private performanceService: PerformanceService,
    private locationService: LocationService,
    private artistService: ArtistService,
    private receiptService: ReceiptService,
    private ticketService: TicketService,
    private cartService: CartService
  ) {
  }

  ngOnInit(): void {
    const userId = this.authService.getUserIdFromToken();
    if (userId) {
      this.loadUserPurchases(userId);
      this.loadUserReservations(userId);
      this.fetchUser();
      this.loadInvoiceCounter();
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

    this.reservedTickets = reservations
    .filter((reservation) =>
      reservation.tickets.every((ticket) => new Date(ticket.date) >= today) // Alle Tickets müssen ab heute sein
    )
    .map((reservation) => ({
      date: new Date(reservation.reservedDate),
      reserved: reservation.tickets,
      reservedId: reservation.reservedId,
      showDetails: false,
    }))
    .sort((a, b) => b.date.getTime() - a.date.getTime());
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

  getTicketPerformanceForInvoice(performanceId: number): string{
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

  fetchUser(): void {
    this.userFirstName = this.authService.getUserFirstNameFromToken();
    this.userLastName = this.authService.getUserLastNameFromToken();
    this.userEmail = this.authService.getUserEmailFromToken();
  }

  public setInvoiceDate(): Date {
    return new Date();
  }

  loadInvoiceCounter(): void {
    const savedCounter = localStorage.getItem('invoiceCounter');
    if (savedCounter) {
      this.invoiceCounter = parseInt(savedCounter, 10);
    }
  }

  saveInvoiceCounter(): void {
    localStorage.setItem('invoiceCounter', this.invoiceCounter.toString());
  }

  setInvoiceNumber(): string {
    return new Date().getFullYear().toString() + '-00' + this.invoiceCounter;
  }


  fetchPurchaseForInvoice(tickets:TicketDto[]){
    let purchaseId: number;
    this.invoiceTickets= tickets;
    console.log(this.invoiceTickets);
    let counter = 0;

    this.userPurchases.forEach((purchase) => {
      purchase.tickets.forEach((purchaseTicket) => {
        if ((purchaseTicket.ticketId === tickets[0].ticketId) && counter < 1) {
          purchaseId= purchase.purchaseId;
          console.log(purchaseId);

          this.purchaseService.getPurchaseById(purchaseId).subscribe({
            next: (purchase: PurchaseListDto) => {
              this.invoicePurchase = purchase;
              this.invoiceTickets= purchase.tickets;
              //TODO address, and total price are not set correctly
              counter++;
              this.purchaseService.getPurchaseById(this.invoicePurchase.purchaseId).subscribe({
                next:() => {
                  this.toastr.info('Downloading Invoice.', 'Download');
                  this.generateDownloadPDF();
                  }
              }
              )
            },
            error: (err) => {
              console.error('Error fetching purchase:', err.message);
              this.toastr.error('Failed to load purchase details. Please try again.', 'Error');
            },
          });
        }
      });
    });

    //this.loadUserPurchases(this.authService.getUserIdFromToken());
  }

  cancelCompletePurchase(tickets: TicketDto[]) {
    let cancelledPurchaseId: number;
    this.cancelledTickets = tickets;

    try {
      this.userPurchases.forEach((purchase) => {
        purchase.tickets.forEach((purchaseTicket) => {
          if (purchaseTicket.ticketId === tickets[0].ticketId) {
            cancelledPurchaseId = purchase.purchaseId;
            console.log(cancelledPurchaseId);

            this.purchaseService.getPurchaseById(cancelledPurchaseId).subscribe({
              next: (purchase: PurchaseListDto) => {
                this.cancelledPurchase = purchase;
                this.cancelledPurchase.tickets = [];

                this.purchaseService.updatePurchase(this.cancelledPurchase).subscribe({
                  next: () => {
                    this.toastr.success('Purchase cancelled successfully.', 'Success');
                    this.loadUserPurchases(this.authService.getUserIdFromToken());
                    //this.generateCancelPurchasePDF();
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
    } catch
      (error) {
      console.error('Error deleting purchase:', error);
      this.toastr.error('Failed to cancel the purchase. Please try again.', 'Error');
    }

    this.showConfirmDeletionDialogAllP = false;
  }

  cancelCompleteReservation(tickets: TicketDto[]) {
    let cancelledReservationId: number;
    this.cancelledTickets = tickets;

    try {
      this.userReservations.forEach((reservation) => {
        reservation.tickets.forEach((reservedTicket) => {
          if (reservedTicket.ticketId === tickets[0].ticketId) {
            cancelledReservationId = reservation.reservedId;

            this.reservationService.getReservationById(cancelledReservationId).subscribe({
              next: (reservation: ReservationListDto) => {
                this.cancelledReservation = reservation;
                this.cancelledReservation.tickets = [];

                this.reservationService.updateReservation(this.cancelledReservation).subscribe({
                  next: () => {
                    this.toastr.success('Reservation cancelled successfully. ', 'Success');
                    this.loadUserReservations(this.authService.getUserIdFromToken());
                  },
                  error: (err) => {
                    console.error('Error updating reservation:', err.message);
                    this.toastr.error('Failed to cancel the reservation. Please try again.', 'Error');
                  },
                });
              },
              error: (err) => {
                console.error('Error fetching reservation:', err.message);
                this.toastr.error('Failed to load reservation details. Please try again.', 'Error');
              },
            });
          }
        });
      });
    } catch
      (error) {
      console.error('Error deleting purchase:', error);
      this.toastr.error('Failed to cancel the purchase. Please try again.', 'Error');
    }

    this.showConfirmDeletionDialogAllRes = false;
  }

  cancelPurchasedTicket(ticket: TicketDto) {
    this.cancelledTicket = ticket;
    let cancelledPurchaseId: number;

    this.userPurchases.forEach((purchase) => {
      purchase.tickets.forEach((purchaseTicket) => {
        if (purchaseTicket.ticketId === ticket.ticketId) {
          cancelledPurchaseId = purchase.purchaseId;

          // Fetch the purchase details
          this.purchaseService.getPurchaseById(cancelledPurchaseId).subscribe({
            next: (purchase: PurchaseListDto) => {
              this.cancelledPurchase = purchase;

              this.address.street = this.cancelledPurchase.street;
              this.address.postalCode = this.cancelledPurchase.postalCode;
              this.address.city = this.cancelledPurchase.city;

              const updatedPurchase: PurchaseListDto = {
                purchaseId: cancelledPurchaseId,
                userId: this.cancelledPurchase.userId,
                tickets: this.cancelledPurchase.tickets.filter(item => item.ticketId !== ticket.ticketId),
                merchandises: this.cancelledPurchase.merchandises,
                totalPrice: this.cancelledPurchase.totalPrice,
                purchaseDate: this.cancelledPurchase.purchaseDate,
                street: this.cancelledPurchase.street,
                postalCode: this.cancelledPurchase.postalCode,
                city: this.cancelledPurchase.city
              };

              // Update the purchase
              this.purchaseService.updatePurchase(updatedPurchase).subscribe({
                next: () => {
                  this.toastr.success('Purchase cancelled successfully.', 'Success');
                  this.loadUserPurchases(this.authService.getUserIdFromToken());
                  this.generateCancelPurchasePDF();
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
    this.showConfirmDeletionDialogPTicket = false;
  }


  cancelReservationTicket(ticketReservation: TicketDto) {
    console.log('cancel reservation ' + ticketReservation.ticketId);
    this.cancelledTicket = ticketReservation;
    let cancelledReservationId: number;

    this.userReservations.forEach((reservation) => {
      reservation.tickets.forEach((reservedTicket) => {
        if (reservedTicket.ticketId === ticketReservation.ticketId) {
          cancelledReservationId = reservation.reservedId;

          // Fetch the reservation details
          this.reservationService.getReservationById(cancelledReservationId).subscribe({
            next: (reservation: ReservationListDto) => {
              this.cancelledReservation = reservation;

              const updatedReservation: ReservationListDto = {
                reservedId: cancelledReservationId,
                userId: this.cancelledReservation.userId,
                tickets: this.cancelledReservation.tickets.filter(item => item.ticketId !== ticketReservation.ticketId),
                reservedDate: this.cancelledReservation.reservedDate
              };

              // Update the reservation
              this.reservationService.updateReservation(updatedReservation).subscribe({
                next: () => {
                  this.toastr.success('Reservation cancelled successfully. ', 'Success');
                  this.loadUserReservations(this.authService.getUserIdFromToken());
                },
                error: (err) => {
                  console.error('Error updating reservation:', err.message);
                  this.toastr.error('Failed to cancel the reservation. Please try again.', 'Error');
                },
              });
            },
            error: (err) => {
              console.error('Error fetching reservation:', err.message);
              this.toastr.error('Failed to load reservation details. Please try again.', 'Error');
            },
          });
        }
      });
    });
    this.showConfirmDeletionDialogReTicket = false;
  }

  showCancelMessagePurchase(ticket: TicketDto) {
    this.showConfirmDeletionDialogPTicket = true;
    this.cancelledTicket = ticket;
  }

  showCancelMessageCompletePurchase(tickets: TicketDto[]) {
    this.showConfirmDeletionDialogAllP = true;
    this.cancelledTickets = tickets;

  }

  showCancelMessageReservation(ticket: TicketDto) {
    this.showConfirmDeletionDialogReTicket = true;
    this.cancelledTicket = ticket;
  }

  showCancelMessageCompleteReservation(tickets: TicketDto[]) {
    this.showConfirmDeletionDialogAllRes = true;
    this.cancelledTickets = tickets;
  }

  addToCart(ticket: TicketDto): void {
    if (ticket.status !== 'RESERVED') {
      this.toastr.error('This ticket cannot be added to the cart.', 'Error');
      return;
    }

    const reservationId = this.findReservationIdByTicket(ticket);
    if (reservationId === null) {
      this.toastr.error('Failed to find reservation for the ticket.', 'Error');
      return;
    }

    this.reservationService.deleteTicketFromReservation(reservationId, ticket.ticketId).subscribe({
      next: () => {
        this.removeTicketFromReservations(ticket);
        this.cartService.addToCart(ticket);
        this.ticketService.updateTicket(ticket.ticketId, ticket).subscribe({
          next: () => {
            this.toastr.success('Ticket added to cart successfully!', 'Success');
          },
          error: (err) => {
            console.error('Error updating ticket status:', err);
            this.toastr.error('Failed to update ticket status. Please try again.', 'Error');
          }
        });
      },
      error: (err) => {
        console.error('Error deleting ticket from reservation:', err);
        this.toastr.error('Failed to remove ticket from reservation. Please try again.', 'Error');
      }
    });
  }


  private removeTicketFromReservations(ticket: TicketDto): void {
    this.reservedTickets.forEach((group, groupIndex) => {
      const ticketIndex = group.reserved.findIndex((t) => t.ticketId === ticket.ticketId);

      if (ticketIndex > -1) {
        group.reserved.splice(ticketIndex, 1);

        if (group.reserved.length === 0) {
          this.reservedTickets.splice(groupIndex, 1);
        }
      }
    });
  }

  private findReservationIdByTicket(ticket: TicketDto): number | null {
    for (const group of this.reservedTickets) {
      if (group.reserved.some((t: TicketDto) => t.ticketId === ticket.ticketId)) {
        return group.reservedId;
      }
    }
    return null;
  }

  public generateDownloadPDF() {
    this.receiptService.exportToPDFDownload();
  }

  public generateCancelPurchasePDF(): void {
    this.receiptService.exportToPDF();
  }
}
