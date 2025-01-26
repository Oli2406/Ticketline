import {Component} from '@angular/core';
import {TicketDto, TicketType, SectorType, PriceCategory} from "../../dtos/ticket";
import {ToastrService} from 'ngx-toastr';
import {PerformanceService} from 'src/app/services/performance.service';
import {PerformanceListDto} from 'src/app/dtos/performance';
import {LocationService} from 'src/app/services/location.service';
import {ArtistService} from 'src/app/services/artist.service';
import {Artist} from "../../dtos/artist";
import {Location} from "../../dtos/location";
import {TicketService} from 'src/app/services/ticket.service';
import {catchError, forkJoin, map, Observable, throwError} from "rxjs";
import {CartService} from "../../services/cart.service";
import {AuthService} from "../../services/auth.service";
import {ActivatedRoute} from '@angular/router';
import {
  TicketExpirationDialogComponent
} from "../ticket-expiration-dialog/ticket-expiration-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {Reservation} from "../../dtos/reservation";
import {ReservationService} from "../../services/reservation.service";
import {PurchaseService} from "../../services/purchase.service";

@Component({
  selector: 'app-seating-plan-A',
  templateUrl: './seating-plan-A.component.html',
  styleUrls: ['./seating-plan-A.component.scss'],
})
export class SeatingPlanAComponent {
  // Standing Tickets
  standingTickets: number = 0; // Regular standing tickets dynamically counted
  vipStandingTickets: number = 0; // VIP standing tickets dynamically counted
  vipStandingPrice: number = 0; // Price of VIP standing tickets dynamically fetched
  regularStandingPrice: number = 0; // Price of regular standing tickets dynamically fetched

  // Selected Tickets and Info
  selectedTickets: TicketDto[] = [];
  selectedStanding: { vip: number; standard: number } = {vip: 0, standard: 0};

  // Enums for easier reference
  priceCategory = PriceCategory;
  ticketType = TicketType;
  sectorType = SectorType;

  totalTickets: number = 0;
  totalPrice: number = 0;

  performanceDetails: PerformanceListDto = null;
  artistDetails: Artist = null;
  locationDetails: Location = null;

  // Seated Tickets
  seatedBackC: TicketDto[] = [];
  seatedBackB: TicketDto[] = [];

  performanceID: number = 0;

  reservedAndPurchasedSeats: number[] = [];
  cartedSeats: number[] = [];

  private userTicketsPerPerformance: { [performanceId: number]: number } = {};

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
  ) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const performanceId = +params['id'];
      const hall = params['hall'];
      if (performanceId) {
        this.performanceID = performanceId;
        this.getPerformanceDetails(performanceId);
        this.loadTicketsByPerformance(performanceId).subscribe({
          next: tickets => {
            console.log('Tickets loaded successfully:', tickets);
          },
          error: err => {
            console.error('Error loading tickets on initialization:', err);
          }
        });
      }
    });
    this.loadUserSeats();
  }

  private loadUserSeats(): void {
    const userId = this.authService.getUserIdFromToken();
    if (userId) {
      forkJoin([
        this.reservedService.getReservationsByUser(userId),
        this.purchaseService.getPurchasesByUser(userId)
      ]).subscribe({
        next: ([reservations, purchases]) => {
          this.userTicketsPerPerformance[this.performanceID] = 0;

          reservations.forEach(reservation => {
            reservation.tickets.forEach(ticket => {
              if (ticket.performanceId === this.performanceID) {
                this.userTicketsPerPerformance[this.performanceID]++;
                this.reservedAndPurchasedSeats.push(ticket.ticketId);
              }
            });
          });

          purchases.forEach(purchase => {
            purchase.tickets.forEach(ticket => {
              if (ticket.performanceId === this.performanceID) {
                this.userTicketsPerPerformance[this.performanceID]++;
                this.reservedAndPurchasedSeats.push(ticket.ticketId);
              }
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

  getTotalUserTicketsForPerformance(): number {
    const reservedAndPurchased = this.userTicketsPerPerformance[this.performanceID] || 0;
    const carted = this.cartedSeats.length;
    const selected = this.selectedTickets.length + this.selectedStanding.vip + this.selectedStanding.standard;
    return reservedAndPurchased + carted + selected;
  }

  loadTicketsByPerformance(performanceId: number): Observable<TicketDto[]> {
    return this.ticketService.getTicketsByPerformanceId(performanceId).pipe(
      map((tickets: TicketDto[]) => {

        this.seatedBackC = tickets
        .filter(ticket => ticket.sectorType === SectorType.C)
        .sort((a, b) => a.rowNumber - b.rowNumber || a.seatNumber - b.seatNumber)
        .map(ticket => ({...ticket, price: this.performanceDetails.price + 40}));

        this.seatedBackB = tickets
        .filter(ticket => ticket.sectorType === SectorType.B)
        .sort((a, b) => a.rowNumber - b.rowNumber || a.seatNumber - b.seatNumber)
        .map(ticket => ({...ticket, price: this.performanceDetails.price + 40}));

        const standingTickets = tickets.filter(
          ticket => ticket.sectorType === SectorType.A && ticket.ticketType === TicketType.STANDING
        );

        this.vipStandingTickets = standingTickets.filter(
          ticket => ticket.priceCategory === PriceCategory.VIP && ticket.status === 'AVAILABLE'
        ).length;

        this.standingTickets = standingTickets.filter(
          ticket => ticket.priceCategory === PriceCategory.STANDARD && ticket.status === 'AVAILABLE'
        ).length;

        return tickets;
      }),
      catchError(err => {
        console.error('Error fetching tickets:', err);
        this.toastr.error('Failed to load tickets. Please try again.', 'Error');
        return throwError(err);
      })
    );
  }

  getPerformanceDetails(id: number): void {
    this.performanceService.getPerformanceById(id).subscribe({
      next: (performance) => {
        this.performanceDetails = performance;
        const performancePrice = performance.price;

        // Set dynamic ticket prices based on performance price
        this.regularStandingPrice = performancePrice;
        this.vipStandingPrice = performancePrice + 70;
        this.seatedBackB.forEach(ticket => ticket.price = performancePrice + 40);
        this.seatedBackC.forEach(ticket => ticket.price = performancePrice + 40);

        // Fetch artist details
        if (this.performanceDetails.artistId) {
          this.artistService.getById(this.performanceDetails.artistId).subscribe({
            next: (artist) => {
              this.artistDetails = artist;
            },
            error: (err) => {
              console.error('Error fetching artist details:', err);
            },
          });
        }

        if (this.performanceDetails.locationId) {
          this.locationService.getById(this.performanceDetails.locationId).subscribe({
            next: (location) => {
              this.locationDetails = location;
            },
            error: (err) => {
              console.error('Error fetching location details:', err);
            },
          });
        }
      },
      error: (err) => {
        console.error('Error fetching performance details:', err);
      },
    });
  }

  toggleTicketSelection(ticket: TicketDto): void {
    if (!ticket) return;

    if (this.reservedAndPurchasedSeats.includes(ticket.ticketId)) {
      if (ticket.status === 'RESERVED') {
        this.toastr.info('You have already reserved this ticket.', 'Info');
      } else if (ticket.status === 'SOLD') {
        this.toastr.info('You have already purchased this ticket.', 'Info');
      }
      return;
    }

    if (this.cartedSeats.includes(ticket.ticketId)) {
      this.toastr.info('You have already added this ticket to your cart.', 'Info');
      return;
    }

    if (ticket.status === 'RESERVED' || ticket.status === 'SOLD') {
      this.toastr.error('This ticket is not available.', 'Error');
      return;
    }

    if (this.cartedSeats.includes(ticket.ticketId)) {
      this.toastr.info('You have already added this ticket to your cart.', 'Info');
      return;
    }

    const totalSelected = this.getTotalUserTicketsForPerformance();
    if (totalSelected >= 8) {
      const reservedCount = this.userTicketsPerPerformance[this.performanceID] || 0;

      if (reservedCount === 0) {
        this.toastr.error('You cannot select more than 8 tickets.', 'Error');
      } else if (reservedCount === 1) {
        this.toastr.error(`You have already reserved or purchased 1 ticket. You can only select up to ${8 - reservedCount} more tickets.`, 'Error');
      } else {
        this.toastr.error(`You have already reserved or purchased ${reservedCount} tickets. You can only select up to ${8 - reservedCount} more tickets.`, 'Error');
      }

      return;
    }


    const index = this.selectedTickets.findIndex((t) => t.ticketId === ticket.ticketId);
    if (index > -1) {
      this.selectedTickets.splice(index, 1);
    } else {
      this.selectedTickets.push(ticket);
    }

    this.updateTotalPrice();
  }

  toggleStandingSector(priceCategory: PriceCategory): void {
    const totalUserTickets = this.getTotalUserTicketsForPerformance();

    if (totalUserTickets >= 8) {
      const reservedCount = this.userTicketsPerPerformance[this.performanceID] || 0;

      if (reservedCount === 0) {
        this.toastr.error('You cannot select more than 8 tickets.', 'Error');
      } else if (reservedCount === 1) {
        this.toastr.error(`You have already reserved or purchased 1 ticket. You can only select up to ${8 - reservedCount} more tickets.`, 'Error');
      } else {
        this.toastr.error(`You have already reserved or purchased ${reservedCount} tickets. You can only select up to ${8 - reservedCount} more tickets.`, 'Error');
      }

      return;
    }


    if (priceCategory === this.priceCategory.VIP) {
      if (this.selectedStanding.vip > 0) {
        this.selectedStanding.vip = 0;
      } else {
        if (this.vipStandingTickets <= 0) {
          this.toastr.warning('No VIP Standing tickets available!', 'Warning');
          return;
        }
        this.selectedStanding.vip = 1;
      }
    } else if (priceCategory === this.priceCategory.STANDARD) {
      if (this.selectedStanding.standard > 0) {
        this.selectedStanding.standard = 0;
      } else {
        if (this.standingTickets <= 0) {
          this.toastr.warning('No Regular Standing tickets available!', 'Warning');
          return;
        }
        this.selectedStanding.standard = 1;
      }
    }

    this.updateTotalPrice();
  }

  validateStandingTickets(type: 'vip' | 'standard'): void {
    const totalSelected = this.selectedTickets.length + this.selectedStanding.vip + this.selectedStanding.standard;

    if (totalSelected > 8) {
      this.toastr.error('You cannot select more than 8 tickets.', 'Error');
      if (type === 'vip') {
        this.selectedStanding.vip = Math.max(0, 8 - this.selectedTickets.length - this.selectedStanding.standard);
      } else if (type === 'standard') {
        this.selectedStanding.standard = Math.max(0, 8 - this.selectedTickets.length - this.selectedStanding.vip);
      }
    }

    this.updateTotalPrice();
  }

  updateTotalPrice(): void {
    const seatedPrice = this.selectedTickets.reduce((sum, ticket) => sum + ticket.price, 0);
    const standingVipPrice = this.selectedStanding.vip * this.vipStandingPrice;
    const standingRegularPrice = this.selectedStanding.standard * this.regularStandingPrice;

    this.totalTickets = this.selectedTickets.length + this.selectedStanding.vip + this.selectedStanding.standard;
    this.totalPrice = seatedPrice + standingVipPrice + standingRegularPrice;
  }

  public resetSelections(): void {
    this.selectedTickets = [];
    this.selectedStanding = {vip: 0, standard: 0};
    this.totalTickets = 0;
    this.totalPrice = 0;
  }

  reserveTickets(): void {
    if (this.totalTickets === 0) {
      this.toastr.error('No tickets selected to reserve!', 'Warning');
      return;
    }

    const reservedCount = this.userTicketsPerPerformance[this.performanceID] || 0;
    if (this.getTotalUserTicketsForPerformance() > 8) {
      this.toastr.error(`You have already reserved or purchased ${reservedCount} tickets. You can only reserve up to ${8 - reservedCount} more tickets.`, 'Error');
      return;
    }

    this.loadTicketsByPerformance(this.performanceID).subscribe({
      next: availableTickets => {
        const unavailableTickets = this.selectedTickets.filter(
          ticket => !availableTickets.some(available => available.ticketId === ticket.ticketId)
        );

        if (unavailableTickets.length > 0) {
          this.toastr.error("Some of the selected tickets are no longer available.", "Error");
          return;
        }

        const reservationDto: Reservation = {
          userId: this.authService.getUserIdFromToken(),
          ticketIds: this.selectedTickets.map(ticket => ticket.ticketId),
          reservedDate: new Date().toISOString()
        };

        if (this.selectedStanding.vip > 0 || this.selectedStanding.standard > 0) {
          forkJoin([
            this.getAvailableStandingTickets(PriceCategory.VIP, this.selectedStanding.vip),
            this.getAvailableStandingTickets(PriceCategory.STANDARD, this.selectedStanding.standard)
          ]).subscribe({
            next: ([vipTickets, standardTickets]) => {
              vipTickets.forEach(ticket => reservationDto.ticketIds.push(ticket.ticketId));
              standardTickets.forEach(ticket => reservationDto.ticketIds.push(ticket.ticketId));

              this.sendReservation(reservationDto);
            },
            error: err => {
              console.error('Error fetching standing tickets:', err);
              this.toastr.error('Failed to reserve tickets. Please try again.', 'Error');
            }
          });
        } else {
          this.sendReservation(reservationDto);
        }
      },
      error: err => {
        console.error('Error validating tickets before reservation:', err);
        this.toastr.error('Failed to validate tickets. Please try again.', 'Error');
      }
    });
  }

  private sendReservation(reservationDto: Reservation): void {
    this.reservedService.createReservation(reservationDto).subscribe({
      next: () => {
        this.toastr.success('Reservation successful!', 'Success');
        this.resetSelections();
        this.loadTicketsByPerformance(this.performanceID); // Reload tickets to update availability
        this.loadUserSeats(); // Update reserved and purchased seats
      },
      error: err => {
        console.error('Error creating reservation:', err);
        this.toastr.error('Failed to create reservation. Some tickets are unavailable', 'Error');
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
    this.loadTicketsByPerformance(this.performanceID).subscribe({
      next: availableTickets => {
        const unavailableTickets = this.selectedTickets.filter(
          ticket => !availableTickets.some(available => available.ticketId === ticket.ticketId)
        );

        if (unavailableTickets.length > 0) {
          this.toastr.error("Some of the selected tickets are no longer available.", "Error");
          return;
        }

        if (this.totalTickets === 0) {
          this.toastr.error("No tickets selected to add to the cart!", "Error");
          return;
        }

        if (this.getTotalUserTicketsForPerformance() > 8) {
          this.toastr.error('You cannot select more than 8 tickets.', 'Error');
          return;
        }

        const dialogRef = this.dialog.open(TicketExpirationDialogComponent, {
          width: '500px',
          disableClose: true,
          panelClass: 'custom-dialog-container',
          backdropClass: 'custom-dialog-backdrop',
        });

        dialogRef.afterClosed().subscribe(() => {
          const seatedUpdateRequests = this.selectedTickets.map(ticket => {
            ticket.status = 'RESERVED';
            return this.ticketService.updateTicket(ticket.ticketId, {
              ...ticket,
              status: 'RESERVED',
            });
          });

          const vipCount = this.selectedStanding.vip;
          const standardCount = this.selectedStanding.standard;

          let vipRequest$ = vipCount > 0
            ? this.getAvailableStandingTickets(PriceCategory.VIP, vipCount)
            : new Observable<TicketDto[]>(subscriber => {
              subscriber.next([]);
              subscriber.complete();
            });

          let standardRequest$ = standardCount > 0
            ? this.getAvailableStandingTickets(PriceCategory.STANDARD, standardCount)
            : new Observable<TicketDto[]>(subscriber => {
              subscriber.next([]);
              subscriber.complete();
            });

          forkJoin([vipRequest$, standardRequest$]).subscribe({
            next: ([vipTickets, standardTickets]) => {
              const standingTickets = [...vipTickets, ...standardTickets];
              const standingUpdateRequests = standingTickets.map(ticket => {
                ticket.status = 'RESERVED';
                return this.ticketService.updateTicket(ticket.ticketId, {
                  ...ticket,
                  status: 'RESERVED',
                });
              });

              // Combine all requests
              const allUpdateRequests = [...seatedUpdateRequests, ...standingUpdateRequests];
              forkJoin(allUpdateRequests).subscribe({
                next: updatedTickets => {
                  updatedTickets.forEach(ticket => {
                    this.cartService.addToCart(ticket);
                    this.cartedSeats.push(ticket.ticketId);
                  });

                  this.resetSelections();
                  this.toastr.success("Tickets successfully added to cart!", "Success");
                },
                error: err => {
                  console.error('Error reserving tickets while adding to cart:', err);
                  this.toastr.error('Failed to reserve tickets. Please try again.', 'Error');
                }
              });
            },
            error: err => {
              console.error('Error fetching standing tickets:', err);
              this.toastr.error('Failed to fetch standing tickets. Please try again.', 'Error');
            }
          });
        });
      },
      error: err => {
        console.error('Error loading tickets by performance:', err);
        this.toastr.error('Failed to check ticket availability. Please try again.', 'Error');
      }
    });
  }

}
