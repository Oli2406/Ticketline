import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AuthService} from '../../services/auth.service';
import {PurchaseService} from '../../services/purchase.service';
import {ReservationService} from '../../services/reservation.service';
import {ToastrService} from 'ngx-toastr';
import {Hall, PriceCategory, SectorType, TicketDto, TicketType} from '../../dtos/ticket';
import {PurchaseDetailDto, PurchaseListDto} from '../../dtos/purchase';
import {ReservationDetailDto, ReservationListDto} from '../../dtos/reservation';
import {PerformanceService} from 'src/app/services/performance.service';
import {LocationService} from '../../services/location.service';
import {PerformanceListDto} from '../../dtos/performance';
import {ArtistService} from '../../services/artist.service';
import {ReceiptService} from "../../services/receipt.service";
import {
  ConfirmationDialogMode,
  ConfirmDialogComponent
} from "../confirm-dialog/confirm-dialog.component";
import {TicketService} from "../../services/ticket.service";
import {CartService} from "../../services/cart.service";
import {forEach} from "lodash";
import {CancelpurchaseService} from "../../services/cancelpurchase.service";

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
  userPurchases: PurchaseDetailDto[] = [];
  userReservations: ReservationDetailDto[] = [];
  cancelledPurchase: PurchaseListDto;
  cancelledReservation: ReservationListDto;
  cancelledTicket: TicketDto = {
    ticketId: 1,
    rowNumber: 1,
    seatNumber: 1,
    priceCategory: PriceCategory.STANDARD,
    ticketType: TicketType.SEATED,
    sectorType: SectorType.A,
    price: 1,
    status: '', // e.g., "AVAILABLE", "RESERVED", "SOLD"
    performanceId: 1,
    reservationNumber: 1,
    hall: Hall.A,
    date: new Date(),
    reservedUntil: ''
  };
  cancelledTickets = [];
  cancelledTicketsPrice = 0;

  invoiceTickets: TicketDto[];
  invoicePurchase: PurchaseListDto = {
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

  userFirstName: string;
  userLastName: string;
  userEmail: string;
  invoiceCounter: number = 1;

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
    private cartService: CartService,
    private cancelPurchaseService: CancelpurchaseService
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
    this.purchaseService.getPurchaseDetailsByUser(userId).subscribe({
      next: (details: PurchaseDetailDto[]) => {
        this.userPurchases = details;
        this.processPurchases(details);
      },
      error: (err) => {
        console.error('Error fetching purchase details:', err.message);
        this.toastr.error('Failed to load your purchases. Please try again.', 'Error');
      },
    });
  }

  loadUserReservations(userId: string): void {
    this.reservationService.getReservationDetailsByUser(userId).subscribe({
      next: (details: ReservationDetailDto[]) => {
        this.userReservations = details;
        this.processReservations(details);
      },
      error: (err) => {
        console.error('Error fetching reservation details:', err.message);
        this.toastr.error('Failed to load your reservations. Please try again.', 'Error');
      },
    });
  }

  getPerformanceNameOfReservation(performanceId: number): string {
    const reservation = this.findReservationByPerformanceId(performanceId);
    return reservation?.performanceDetails[performanceId]?.name || 'Unknown Performance';
  }

  getArtistNameOfReservation(performanceId: number): string {
    const reservation = this.findReservationByPerformanceId(performanceId);
    return reservation?.performanceDetails[performanceId]?.artistName || 'Unknown Artist';
  }

  getPerformanceLocationOfReservation(performanceId: number): string {
    const reservation = this.findReservationByPerformanceId(performanceId);
    return reservation?.performanceDetails[performanceId]?.locationName || 'Unknown Location';
  }

  private findReservationByPerformanceId(performanceId: number): ReservationDetailDto | undefined {
    return this.userReservations.find(reservation =>
      Object.keys(reservation.performanceDetails).some(id => Number(id) === performanceId)
    );
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
    const purchase = this.findPurchaseByPerformanceId(performanceId);
    return purchase?.performanceDetails[performanceId]?.name || 'Unknown Performance';
  }

  getArtistName(performanceId: number): string {
    const purchase = this.findPurchaseByPerformanceId(performanceId);
    return purchase?.performanceDetails[performanceId]?.artistName || 'Unknown Artist';
  }

  getPerformanceLocation(performanceId: number): string {
    const purchase = this.findPurchaseByPerformanceId(performanceId);
    return purchase?.performanceDetails[performanceId]?.locationName || 'Unknown Location';
  }

  private findPurchaseByPerformanceId(performanceId: number): PurchaseDetailDto | undefined {
    return this.userPurchases.find(purchase =>
      Object.keys(purchase.performanceDetails).some(id => Number(id) === performanceId)
    );
  }

  getTicketPerformanceForInvoice(performanceId: number): string {
    for (const purchase of this.userPurchases) {
      if (purchase.performanceDetails && purchase.performanceDetails[performanceId]) {
        return purchase.performanceDetails[performanceId].name;
      }
    }

    return 'Unknown Performance';
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


  fetchPurchaseForInvoice(tickets: TicketDto[]) {
    this.invoiceTickets = tickets;

    const matchingPurchase = this.userPurchases.find((purchase) =>
      purchase.tickets.some((purchaseTicket) => purchaseTicket.ticketId === tickets[0].ticketId)
    );

    if (matchingPurchase) {
      this.invoicePurchase = matchingPurchase;
      this.invoiceTickets = matchingPurchase.tickets;

      this.toastr.info('Downloading Invoice.', 'Download');
      this.generateDownloadPDF();
    } else {
      this.toastr.error('No matching purchase found for the provided tickets.', 'Error');
    }
  }

  cancelCompletePurchase(tickets: TicketDto[]) {
    this.cancelledTickets = tickets;

    const matchingPurchase = this.userPurchases.find((purchase) =>
      purchase.tickets.some((purchaseTicket) => purchaseTicket.ticketId === tickets[0].ticketId)
    );

    if (matchingPurchase) {
      this.cancelledPurchase = {...matchingPurchase};
      this.cancelledPurchase.tickets = [];

      const updatePromises = tickets.map(ticket =>
        this.ticketService.updateTicket(ticket.ticketId, {
          ...ticket,
          status: 'AVAILABLE',
        }).toPromise()
      );

      //TODO interject here to save the cancelled purchase??? AND WRITE HTML FOR THE CANCELLED VIEW
      //TODO and check if you need additional variables, like the group for purchased and so on
      //TODO and you need to fetch them with the other purchases
      // --> TODO Maybe write a new service class
      Promise.all(updatePromises)
      .then(() => {
        this.purchaseService.updatePurchase(this.cancelledPurchase).subscribe({
          next: () => {
            this.generateCancelPurchasePDF();
            this.toastr.success('Purchase cancelled successfully.', 'Success');
            this.loadUserPurchases(this.authService.getUserIdFromToken());
          },
          error: (err) => {
            console.error('Error updating purchase:', err.message);
            this.toastr.error('Failed to cancel the purchase. Please try again.', 'Error');
          }
        });
      })
      .catch((error) => {
        console.error('Error updating tickets:', error.message);
        this.toastr.error('Failed to update tickets. Please try again.', 'Error');
      });
    } else {
      this.toastr.error('No matching purchase found for the provided tickets.', 'Error');
    }

    this.showConfirmDeletionDialogAllP = false;
  }

  cancelCompleteReservation(tickets: TicketDto[]) {
    this.cancelledTickets = tickets;
    console.log(this.cancelledTickets);

    const matchingReservation = this.userReservations.find((reservation) =>
      reservation.tickets.some((reservedTicket) => reservedTicket.ticketId === tickets[0].ticketId)
    );

    if (matchingReservation) {
      this.cancelledReservation = {...matchingReservation, tickets: []};

      const updatePromises = tickets.map(ticket =>
        this.ticketService.updateTicket(ticket.ticketId, {
          ...ticket,
          status: 'AVAILABLE',
        }).toPromise()
      );

      Promise.all(updatePromises)
      .then(() => {
        this.reservationService.updateReservation(this.cancelledReservation).subscribe({
          next: () => {
            this.toastr.success('Reservation cancelled successfully.', 'Success');
            this.loadUserReservations(this.authService.getUserIdFromToken());
          },
          error: (err) => {
            console.error('Error updating reservation:', err.message);
            this.toastr.error('Failed to cancel the reservation. Please try again.', 'Error');
          }
        });
      })
      .catch((error) => {
        console.error('Error updating tickets:', error.message);
        this.toastr.error('Failed to update tickets. Please try again.', 'Error');
      });
    } else {
      this.toastr.error('No matching purchase found for the provided tickets.', 'Error');
    }
    this.showConfirmDeletionDialogAllRes = false;
  }

  cancelPurchasedTicket(ticket: TicketDto) {
    this.cancelledTicket = ticket;

    const matchingPurchase = this.userPurchases.find((purchase) =>
      purchase.tickets.some((purchaseTicket) => purchaseTicket.ticketId === ticket.ticketId)
    );

    if (matchingPurchase) {
      this.address.street = matchingPurchase.street;
      this.address.postalCode = matchingPurchase.postalCode;
      this.address.city = matchingPurchase.city;

      const updatedPurchase = {
        ...matchingPurchase,
        tickets: matchingPurchase.tickets.filter((item) => item.ticketId !== ticket.ticketId)
      };

      this.purchaseService.updatePurchase(updatedPurchase).subscribe({
        next: () => {
          this.generateCancelPurchasePDF();
          this.toastr.success('Purchase cancelled successfully.', 'Success');
          this.loadUserPurchases(this.authService.getUserIdFromToken());
        },
        error: (err) => {
          console.error('Error updating purchase:', err.message);
          this.toastr.error('Failed to cancel the purchase. Please try again.', 'Error');
        }
      });
    } else {
      this.toastr.error('No matching purchase found for the selected ticket.', 'Error');
    }
    this.showConfirmDeletionDialogPTicket = false;
  }

  cancelReservationTicket(ticketReservation: TicketDto) {
    this.cancelledTicket = ticketReservation;

    const matchingReservation = this.userReservations.find((reservation) =>
      reservation.tickets.some((reservedTicket) => reservedTicket.ticketId === ticketReservation.ticketId)
    );

    if (matchingReservation) {
      const updatedReservation = {
        ...matchingReservation,
        tickets: matchingReservation.tickets.filter(
          (item) => item.ticketId !== ticketReservation.ticketId
        ),
      };

      this.reservationService.updateReservation(updatedReservation).subscribe({
        next: () => {
          this.toastr.success('Reservation ticket cancelled successfully.', 'Success');
          this.loadUserReservations(this.authService.getUserIdFromToken());
        },
        error: (err) => {
          console.error('Error updating reservation:', err.message);
          this.toastr.error('Failed to cancel the ticket in the reservation. Please try again.', 'Error');
        },
      });
    } else {
      this.toastr.error('No matching reservation found for the selected ticket.', 'Error');
    }

    this.showConfirmDeletionDialogReTicket = false;
  }


  showCancelMessagePurchase(ticket: TicketDto) {
    this.showConfirmDeletionDialogPTicket = true;
    this.cancelledTickets[0] = ticket;
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
        this.toastr.success('Ticket added to cart successfully!', 'Success');
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
    this.receiptService.exportToDownloadPDF();
  }

  public generateCancelPurchasePDF(): void {
    this.receiptService.exportToPDF();
  }

  public getCancelTicketsPrice(tickets: TicketDto[]) {
    this.cancelledTickets = tickets;
    let totalPrice = 0;

    this.cancelledTickets.forEach((ticket) => totalPrice += ticket.price);

    this.cancelledTicketsPrice = totalPrice;
    return totalPrice;

  }
}
