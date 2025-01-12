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
import {forkJoin, map, Observable} from "rxjs";
import { CartService } from "../../services/cart.service";
import { AuthService } from "../../services/auth.service";
import { ActivatedRoute } from '@angular/router';
import {TicketExpirationDialogComponent} from "../ticket-expiration-dialog/ticket-expiration-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {Reservation} from "../../dtos/reservation";
import {ReservationService} from "../../services/reservation.service";

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
  selectedStanding: { vip: number; standard: number } = { vip: 0, standard: 0 };

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
  seatedBackC: TicketDto[] = [];
  seatedBackB: TicketDto[] = [];

  performanceID: number = 0;

  // Inject dependencies
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
  }

  /**
   * Loads tickets for the given performance ID and filters them into
   * seatedBackC (Sector C), seatedBackB (Sector B), and standing tickets for Sector A.
   * @param performanceId - ID of the performance to fetch tickets for.
   */
  loadTicketsByPerformance(performanceId: number): void {
    this.ticketService.getTicketsByPerformanceId(performanceId).subscribe({
      next: (tickets: TicketDto[]) => {
        // Filter and sort tickets for Sector C
        this.seatedBackC = tickets
          .filter(ticket => ticket.sectorType === SectorType.C)
          .sort((a, b) => a.rowNumber - b.rowNumber || a.seatNumber - b.seatNumber);

        // Filter and sort tickets for Sector B
        this.seatedBackB = tickets
          .filter(ticket => ticket.sectorType === SectorType.B)
          .sort((a, b) => a.rowNumber - b.rowNumber || a.seatNumber - b.seatNumber);

        // Filter standing tickets for Sector A
        const standingTickets = tickets.filter(
          ticket => ticket.sectorType === SectorType.A && ticket.ticketType === TicketType.STANDING
        );

        // Count VIP and regular standing tickets
        this.vipStandingTickets = standingTickets.filter(
          ticket => ticket.priceCategory === PriceCategory.VIP && ticket.status === 'AVAILABLE'
        ).length;

        this.standingTickets = standingTickets.filter(
          ticket => ticket.priceCategory === PriceCategory.STANDARD && ticket.status === 'AVAILABLE'
        ).length;

        // Extract prices for VIP and regular standing tickets
        const firstVipStanding = standingTickets.find(ticket => ticket.priceCategory === PriceCategory.VIP);
        const firstRegularStanding = standingTickets.find(ticket => ticket.priceCategory === PriceCategory.STANDARD);

        this.vipStandingPrice = firstVipStanding?.price || 0; // Get the price or fallback to 0
        this.regularStandingPrice = firstRegularStanding?.price || 0; // Get the price or fallback to 0
      },
      error: (err) => {
        console.error('Error fetching tickets:', err);
        this.toastr.error('Failed to load tickets. Please try again.', 'Error');
      }
    });
  }

  getPerformanceDetails(id: number): void {
    this.performanceService.getPerformanceById(id).subscribe({
      next: (performance) => {
        this.performanceDetails = performance;

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

        // Fetch location details
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
    const index = this.selectedTickets.findIndex((t) => t.ticketId === ticket.ticketId);

    if (index > -1) {
      this.selectedTickets.splice(index, 1);
      this.updateTotalPrice();
      return;
    }

    const totalSelected = this.selectedTickets.length + this.selectedStanding.vip + this.selectedStanding.standard;
    if (totalSelected >= 5) {
      this.toastr.error('You cannot select more than 5 tickets.', 'Error');
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
    const totalSelected = this.selectedTickets.length + this.selectedStanding.vip + this.selectedStanding.standard;

    if (priceCategory === this.priceCategory.VIP) {
      if (this.selectedStanding.vip > 0) {
        this.selectedStanding.vip = 0;
      } else {
        if (totalSelected >= 5) {
          this.toastr.error('You cannot select more than 5 tickets.', 'Error');
          return;
        }
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
        if (totalSelected >= 5) {
          this.toastr.error('You cannot select more than 5 tickets.', 'Error');
          return;
        }
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

    if (totalSelected > 5) {
      this.toastr.error('You cannot select more than 5 tickets.', 'Error');
      if (type === 'vip') {
        this.selectedStanding.vip = Math.max(0, 5 - this.selectedTickets.length - this.selectedStanding.standard);
      } else if (type === 'standard') {
        this.selectedStanding.standard = Math.max(0, 5 - this.selectedTickets.length - this.selectedStanding.vip);
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
    this.selectedStanding = { vip: 0, standard: 0 };
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

    // Handle standing tickets (VIP)
    if (this.selectedStanding.vip > 0) {
      this.getAvailableStandingTickets(PriceCategory.VIP, this.selectedStanding.vip).subscribe({
        next: vipTickets => {
          vipTickets.forEach(ticket => {
            reservationDto.ticketIds.push(ticket.ticketId);
          });

          // Handle standing tickets (Regular) after VIP tickets are handled
          if (this.selectedStanding.standard > 0) {
            this.getAvailableStandingTickets(PriceCategory.STANDARD, this.selectedStanding.standard).subscribe({
              next: standardTickets => {
                standardTickets.forEach(ticket => {
                  reservationDto.ticketIds.push(ticket.ticketId);
                });

                // Send the reservation to the backend
                this.sendReservation(reservationDto);
              },
              error: err => {
                console.error('Error fetching regular standing tickets:', err);
                this.toastr.error('Failed to reserve regular standing tickets. Please try again.', 'Error');
              }
            });
          } else {
            // Send the reservation to the backend if no regular standing tickets are selected
            this.sendReservation(reservationDto);
          }
        },
        error: err => {
          console.error('Error fetching VIP standing tickets:', err);
          this.toastr.error('Failed to reserve VIP standing tickets. Please try again.', 'Error');
        }
      });
    } else if (this.selectedStanding.standard > 0) {
      // Handle only regular standing tickets if no VIP tickets are selected
      this.getAvailableStandingTickets(PriceCategory.STANDARD, this.selectedStanding.standard).subscribe({
        next: standardTickets => {
          standardTickets.forEach(ticket => {
            reservationDto.ticketIds.push(ticket.ticketId);
          });

          // Send the reservation to the backend
          this.sendReservation(reservationDto);
        },
        error: err => {
          console.error('Error fetching regular standing tickets:', err);
          this.toastr.error('Failed to reserve regular standing tickets. Please try again.', 'Error');
        }
      });
    } else {
      // Send the reservation to the backend if no standing tickets are selected
      this.sendReservation(reservationDto);
    }
  }

  private sendReservation(reservationDto: Reservation): void {
    this.reservedService.createReservation(reservationDto).subscribe({
      next: (response) => {
        this.toastr.success('Tickets successfully reserved!', 'Success');
        console.log('Reservation response:', response);
        // Clear the local reservation DTO
        reservationDto.ticketIds = [];
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

        return result; // Return the filtered tickets
      })
    );
  }
  private executeUpdates(updateRequests: Observable<any>[]): void {
    forkJoin(updateRequests).subscribe({
      next: () => {
        this.toastr.success(`Successfully reserved ${this.totalTickets} tickets!`, 'Reservation Successful');
        this.resetSelections();
        this.loadTicketsByPerformance(this.performanceID);
      },
      error: err => {
        console.error('Error reserving tickets:', err);
        this.toastr.error('Failed to reserve tickets. Please try again.', 'Error');
      }
    });
  }


  getClass(ticket: TicketDto): { [key: string]: boolean } {
    return {
      available: ticket.status === 'AVAILABLE',
      reserved: ticket.status === 'RESERVED',
      sold: ticket.status === 'SOLD',
      'selected-seat': this.selectedTickets.includes(ticket),
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
      panelClass: 'custom-dialog-container', // Add custom panel class
      backdropClass: 'custom-dialog-backdrop', // Add custom backdrop class
    });

    dialogRef.afterClosed().subscribe(() => {
      const updateRequests = [];

      this.selectedTickets.forEach(ticket => {
        ticket.status = 'RESERVED';
        this.cartService.addToCart(ticket);
        updateRequests.push(this.ticketService.updateTicket(ticket));
      });

      if (this.selectedStanding.vip > 0) {
        this.getAvailableStandingTickets(PriceCategory.VIP, this.selectedStanding.vip).subscribe({
          next: vipTickets => {
            vipTickets.forEach(ticket => {
              ticket.status = 'RESERVED';
              this.cartService.addToCart(ticket);
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

      if (this.selectedStanding.standard > 0) {
        this.getAvailableStandingTickets(PriceCategory.STANDARD, this.selectedStanding.standard).subscribe({
          next: standardTickets => {
            standardTickets.forEach(ticket => {
              ticket.status = 'RESERVED';
              this.cartService.addToCart(ticket);
              updateRequests.push(this.ticketService.updateTicket(ticket));
            });
            this.resetSelections();
            this.toastr.success(`${this.selectedStanding.standard} Standard standing tickets added to cart!`, "Success");
          },
          error: err => {
            console.error('Error fetching Standard standing tickets:', err);
            this.toastr.error('Failed to add Standard standing tickets to the cart.', 'Error');
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
