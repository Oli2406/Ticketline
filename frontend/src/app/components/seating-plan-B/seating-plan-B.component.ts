import { Component } from '@angular/core';
import { TicketDto, TicketType, SectorType, PriceCategory } from "../../dtos/ticket";
import { ToastrService } from 'ngx-toastr';
import { PerformanceService } from 'src/app/services/performance.service';
import { PerformanceListDto } from 'src/app/dtos/performance';
import { LocationService } from 'src/app/services/location.service';
import { ArtistService } from 'src/app/services/artist.service';
import { Artist } from "../../dtos/artist";
import { Location } from "../../dtos/location";
import { TicketService } from 'src/app/services/ticket.service';
import { forkJoin, map, Observable } from "rxjs";
import {CartService} from "../../services/cart.service";
import {ActivatedRoute} from "@angular/router";
import {TicketExpirationDialogComponent} from "../ticket-expiration-dialog/ticket-expiration-dialog.component";
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {Reservation} from "../../dtos/reservation";
import { AuthService } from "../../services/auth.service";
import {ReservationService} from "../../services/reservation.service";
import {PurchaseService} from "../../services/purchase.service";

@Component({
  selector: 'app-seating-plan-B',
  templateUrl: './seating-plan-B.component.html',
  styleUrls: ['./seating-plan-B.component.scss'],
})
export class SeatingPlanBComponent {
  // Standing Tickets
  standingTickets: number = 0;
  vipStandingTickets: number = 0;
  vipStandingPrice: number = 0;
  regularStandingPrice: number = 0;

  // Selected Tickets and Info
  selectedTickets: TicketDto[] = [];
  selectedStanding: { vip: number; premium: number } = { vip: 0, premium: 0 };

  // Enums for easier reference
  priceCategory = PriceCategory;
  ticketType = TicketType;
  sectorType = SectorType;

  // Total tickets and price
  totalTickets: number = 0;
  totalPrice: number = 0;

  performanceDetails: PerformanceListDto = null;
  artistDetails: Artist = null;
  locationDetails: Location = null;

  // Seated Tickets
  seatedBackC1: TicketDto[] = [];
  seatedBackC2: TicketDto[] = [];
  seatedBackC3: TicketDto[] = [];
  seatedBackC4: TicketDto[] = [];
  seatedBackC5: TicketDto[] = [];
  seatedBackC6: TicketDto[] = [];
  seatedBackC7: TicketDto[] = [];
  seatedBackC8: TicketDto[] = [];
  seatedBackC9: TicketDto[] = [];

  seatedBackB: TicketDto[] = [];


  performanceID: number = 0;

  reservedAndPurchasedSeats: number[] = [];
  cartedSeats: number[] = [];


  constructor(
    private toastr: ToastrService,
    private performanceService: PerformanceService,
    private locationService: LocationService,
    private artistService: ArtistService,
    private ticketService: TicketService,
    private cartService: CartService,
    private route: ActivatedRoute,
    private dialog: MatDialog,
    private authService: AuthService,
    private reservedService: ReservationService,
    private purchaseService: PurchaseService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const performanceId = +params['id'];
      const hall = params['hall'];

      if (performanceId) {
        this.performanceID = performanceId;
        this.getPerformanceDetails(performanceId);
        this.loadTicketsByPerformance(performanceId);
      }
    });

    this.getPerformanceDetails(this.performanceID);
    this.loadTicketsByPerformance(this.performanceID);
    this.loadUserSeats(); // Load user-owned seats
  }


  loadTicketsByPerformance(performanceId: number): void {
    this.ticketService.getTicketsByPerformanceId(performanceId).subscribe({
      next: (tickets: TicketDto[]) => {
        const seatedBackCRows: { [key: number]: TicketDto[] } = {};

        // Group and assign tickets for Sector C (rows 1 to 9)
        tickets.forEach((ticket) => {
          if (ticket.sectorType === SectorType.C) {
            ticket.price = this.performanceDetails.price - 10; // Set price for Sector C
            if (!seatedBackCRows[ticket.rowNumber]) {
              seatedBackCRows[ticket.rowNumber] = [];
            }
            seatedBackCRows[ticket.rowNumber].push(ticket);
          }
        });

        // Assign grouped tickets to dynamically named properties (seatedBackC1, seatedBackC2, ...)
        for (let row = 1; row <= 9; row++) {
          this[`seatedBackC${row}`] = seatedBackCRows[row]?.sort((a, b) => a.seatNumber - b.seatNumber) || [];
        }

        // Filter and set prices for Sector B
        this.seatedBackB = tickets
          .filter(ticket => ticket.sectorType === SectorType.B)
          .map(ticket => ({ ...ticket, price: this.performanceDetails.price + 10 }))
          .sort((a, b) => a.rowNumber - b.rowNumber || a.seatNumber - b.seatNumber);

        // Filter standing tickets for Sector A
        const standingTickets = tickets.filter(
          ticket => ticket.sectorType === SectorType.A && ticket.ticketType === TicketType.STANDING
        );

        // Count and set prices for VIP and Regular standing tickets
        this.vipStandingTickets = standingTickets.filter(
          ticket => ticket.priceCategory === PriceCategory.VIP && ticket.status === 'AVAILABLE'
        ).length;

        this.standingTickets = standingTickets.filter(
          ticket => ticket.priceCategory === PriceCategory.PREMIUM && ticket.status === 'AVAILABLE'
        ).length;

        // Set prices for standing tickets
        this.regularStandingPrice = this.performanceDetails.price;
        this.vipStandingPrice = this.performanceDetails.price + 30;
      },
      error: (err) => {
        console.error('Error fetching tickets:', err);
        this.toastr.error('Failed to load tickets. Please try again.', 'Error');
      }
    });
  }


  private loadUserSeats(): void {
    const userId = this.authService.getUserIdFromToken();
    if (userId) {
      forkJoin([
        this.reservedService.getReservationsByUser(userId),
        this.purchaseService.getPurchasesByUser(userId)
      ]).subscribe({
        next: ([reservations, purchases]) => {
          reservations.forEach(reservation => {
            reservation.tickets.forEach(ticket => {
              this.reservedAndPurchasedSeats.push(ticket.ticketId);
            });
          });

          purchases.forEach(purchase => {
            purchase.tickets.forEach(ticket => {
              this.reservedAndPurchasedSeats.push(ticket.ticketId);
            });
          });
        },
        error: (err) => {
          console.error('Error loading user seats:', err);
          this.toastr.error('Failed to load reserved and purchased seats.', 'Error');
        }
      });
    }
  }




  getPerformanceDetails(id: number): void {
    this.performanceService.getPerformanceById(id).subscribe({
      next: (performance) => {
        this.performanceDetails = performance;
        const performancePrice = performance.price; // Base performance price

        // Set prices for different sectors and standing tickets
        this.regularStandingPrice = performancePrice;
        this.vipStandingPrice = performancePrice + 30;

        // Update sector prices if tickets are already loaded
        this.seatedBackB.forEach(ticket => ticket.price = performancePrice + 10);
        for (let row = 1; row <= 9; row++) {
          this[`seatedBackC${row}`].forEach(ticket => ticket.price = performancePrice - 10);
        }
      },
      error: (err) => {
        console.error('Error fetching performance details:', err);
      }
    });
  }


  toggleTicketSelection(ticket: TicketDto): void {
    if (!ticket) return;

    // Check if the ticket is user-owned (reserved or purchased)
    if (this.reservedAndPurchasedSeats.includes(ticket.ticketId)) {
      if (ticket.status === 'RESERVED') {
        this.toastr.info('You have already reserved this ticket.', 'Info');
      } else if (ticket.status === 'SOLD') {
        this.toastr.info('You have already purchased this ticket.', 'Info');
      }
      return; // Prevent further actions for user-owned tickets
    }

    // Check if the ticket is already in the cart
    if (this.cartedSeats.includes(ticket.ticketId)) {
      this.toastr.info('You have already added this ticket to your cart.', 'Info');
      return; // Prevent further actions for carted tickets
    }

    // Handle regular ticket selection
    const index = this.selectedTickets.findIndex(
      (t) => t.ticketId === ticket.ticketId
    );

    if (index > -1) {
      this.selectedTickets.splice(index, 1);
      this.updateTotalPrice();
      return;
    }

    const totalSelected = this.selectedTickets.length + this.selectedStanding.vip + this.selectedStanding.premium;
    if (totalSelected >= 8) {
      this.toastr.error('You cannot select more than 8 tickets.', 'Error');
      return;
    }

    if (ticket.status !== 'AVAILABLE') {
      this.toastr.error('This ticket is not available.', 'Error');
      return;
    }

    this.selectedTickets.push(ticket);
    this.updateTotalPrice();
  }



  toggleStandingSector(priceCategory: PriceCategory): void {
    const totalSelected = this.selectedTickets.length + this.selectedStanding.vip + this.selectedStanding.premium;

    if (priceCategory === this.priceCategory.VIP) {
      if (this.selectedStanding.vip > 0) {
        this.selectedStanding.vip = 0;
      } else {
        if (totalSelected >= 8) {
          this.toastr.error('You cannot select more than 8 tickets.', 'Error');
          return;
        }
        if (this.vipStandingTickets <= 0) {
          this.toastr.warning('No VIP Standing tickets available!', 'Warning');
          return;
        }
        this.selectedStanding.vip = 1;
      }
    } else if (priceCategory === this.priceCategory.PREMIUM) {
      if (this.selectedStanding.premium > 0) {
        this.selectedStanding.premium = 0;
      } else {
        if (totalSelected >= 8) {
          this.toastr.error('You cannot select more than 8 tickets.', 'Error');
          return;
        }
        if (this.standingTickets <= 0) {
          this.toastr.warning('No Regular Standing tickets available!', 'Warning');
          return;
        }
        this.selectedStanding.premium = 1;
      }
    }
    this.updateTotalPrice();
  }

  validateStandingTickets(type: 'vip' | 'premium'): void {
    const totalSelected = this.selectedTickets.length + this.selectedStanding.vip + this.selectedStanding.premium;

    if (totalSelected > 8) {
      this.toastr.error('You cannot select more than 8 tickets.', 'Error');
      if (type === 'vip') {
        this.selectedStanding.vip = Math.max(0, 8 - this.selectedTickets.length - this.selectedStanding.premium);
      } else if (type === 'premium') {
        this.selectedStanding.premium = Math.max(0, 8 - this.selectedTickets.length - this.selectedStanding.vip);
      }
    }

    this.updateTotalPrice();
  }

  updateTotalPrice(): void {
    const seatedPrice = this.selectedTickets.reduce((sum, ticket) => sum + ticket.price, 0);
    const standingVipPrice = this.selectedStanding.vip * this.vipStandingPrice;
    const standingRegularPrice = this.selectedStanding.premium * this.regularStandingPrice;

    this.totalTickets = this.selectedTickets.length + this.selectedStanding.vip + this.selectedStanding.premium;
    this.totalPrice = seatedPrice + standingVipPrice + standingRegularPrice;
  }

   resetSelections(): void {
    const totalSelected = this.selectedTickets.length + this.selectedStanding.vip + this.selectedStanding.premium;
    this.selectedTickets = [];
    this.selectedStanding = { vip: 0, premium: 0 };
    this.totalTickets = 0;
    this.totalPrice = 0;
  }

  reserveTickets(): void {
    if (this.totalTickets === 0) {
      this.toastr.error('No tickets selected to reserve!', 'Warning');
      return;
    }

    this.toastr.info(
      'Please arrive at least 30 minutes before your reservation. Failure to do so will result in invalidation.',
      'Important Notice',
      {
        timeOut: 10000,
        extendedTimeOut: 5000,
        progressBar: true,
        closeButton: true
      }
    );

    const reservationDto: Reservation = {
      userId: this.authService.getUserIdFromToken(),
      ticketIds: [],
      reservedDate: new Date().toISOString()
    };

    // Handle seated tickets
    this.selectedTickets.forEach(ticket => {
      reservationDto.ticketIds.push(ticket.ticketId);
    });

    // Handle standing tickets (VIP and Standard)
    forkJoin([
      this.getAvailableStandingTickets(PriceCategory.VIP, this.selectedStanding.vip),
      this.getAvailableStandingTickets(PriceCategory.PREMIUM, this.selectedStanding.premium)
    ]).subscribe({
      next: ([vipTickets, standardTickets]) => {
        [...vipTickets, ...standardTickets].forEach(ticket => {
          reservationDto.ticketIds.push(ticket.ticketId);
        });

        // Send the reservation to the backend
        this.sendReservation(reservationDto);
      },
      error: (err) => {
        console.error('Error fetching standing tickets:', err);
        this.toastr.error('Failed to fetch standing tickets. Please try again.', 'Error');
      }
    });
  }

  private sendReservation(reservationDto: Reservation): void {
    this.reservedService.createReservation(reservationDto).subscribe({
      next: (response) => {
        this.toastr.success('Tickets successfully reserved!', 'Success');
        console.log('Reservation response:', response);
        // Clear the local reservation DTO
        reservationDto.ticketIds = [];
        this.resetSelections();
        this.loadTicketsByPerformance(this.performanceID);
        this.loadUserSeats();
      },
      error: (err) => {
        console.error('Error creating reservation:', err);
        this.toastr.error('Failed to reserve tickets. Please try again.', 'Error');
      }
    });
  }

  getAvailableStandingTickets(category: PriceCategory, count: number): Observable<TicketDto[]> {
    return this.ticketService.getTicketsByPerformanceId(this.performanceID).pipe(
      map(tickets => {
        const result: TicketDto[] = [];
        let availableCount = 0;

        for (const ticket of tickets) {
          if (
            ticket.sectorType === SectorType.A &&
            ticket.ticketType === TicketType.STANDING &&
            ticket.priceCategory === category &&
            ticket.status === 'AVAILABLE'
          ) {
            result.push(ticket);
            availableCount++;

            if (availableCount >= count) {
              break;
            }
          }
        }

        return result;
      })
    );
  }

  getClass(ticket: TicketDto): { [key: string]: boolean } {
    const isSeated = ticket.sectorType === SectorType.B || ticket.sectorType === SectorType.C;
    const isUserOwned = this.reservedAndPurchasedSeats.includes(ticket.ticketId) && isSeated;
    const isInCart = this.cartedSeats.includes(ticket.ticketId) && isSeated;

    return {
      available: ticket.status === 'AVAILABLE',
      reserved: ticket.status === 'RESERVED' && !isUserOwned && !isInCart,
      sold: ticket.status === 'SOLD' && !isUserOwned,
      'selected-seat': this.selectedTickets.includes(ticket) && !isUserOwned && !isInCart,
      'user-owned-seat': isUserOwned || isInCart
    };
  }


  addToCart(): void {
    if (this.totalTickets === 0) {
      this.toastr.error("No tickets selected to add to the cart!", "Error");
      return;
    }

    const dialogRef = this.dialog.open(TicketExpirationDialogComponent, {
      width: '500px',
      disableClose: true,
      panelClass: 'custom-dialog-container',
      backdropClass: 'custom-dialog-backdrop',
    });

    dialogRef.afterClosed().subscribe(() => {
      const updateRequests = [];

      this.selectedTickets.forEach(ticket => {
        ticket.status = 'RESERVED';
        this.cartService.addToCart(ticket);
        this.cartedSeats.push(ticket.ticketId); // Track tickets added to the cart
        updateRequests.push(this.ticketService.updateTicket(ticket));
      });

      if (this.selectedStanding.vip > 0) {
        this.getAvailableStandingTickets(PriceCategory.VIP, this.selectedStanding.vip).subscribe({
          next: vipTickets => {
            vipTickets.forEach(ticket => {
              ticket.status = 'RESERVED';
              this.cartService.addToCart(ticket);
              this.cartedSeats.push(ticket.ticketId); // Track tickets added to the cart
              updateRequests.push(this.ticketService.updateTicket(ticket));
            });
            this.resetSelections();
            this.toastr.success(`${this.selectedStanding.vip} VIP standing tickets added to cart!`, "Success");
          },
          error: err => {
            console.error('Error fetching VIP standing tickets:', err);
            this.toastr.error('Failed to add VIP standing tickets to the cart.', 'Error');
          }
        });
      }

      if (this.selectedStanding.premium > 0) {
        this.getAvailableStandingTickets(PriceCategory.PREMIUM, this.selectedStanding.premium).subscribe({
          next: premiumTickets => {
            premiumTickets.forEach(ticket => {
              ticket.status = 'RESERVED';
              this.cartService.addToCart(ticket);
              this.cartedSeats.push(ticket.ticketId);
              updateRequests.push(this.ticketService.updateTicket(ticket));
            });
            this.resetSelections();
            this.toastr.success(`${this.selectedStanding.premium} Premium standing tickets added to cart!`, "Success");
          },
          error: err => {
            console.error('Error fetching Premium standing tickets:', err);
            this.toastr.error('Failed to add Premium standing tickets to the cart.', 'Error');
          }
        });
      }

      forkJoin(updateRequests).subscribe({
        next: () => {
          this.toastr.success("Successfully added and reserved selected tickets to the cart.", "Success");
          this.resetSelections();
        },
        error: err => {
          console.error('Error reserving tickets while adding to cart:', err);
          this.toastr.error('Failed to reserve tickets. Please try again.', 'Error');
        }
      });
    });
  }

}
