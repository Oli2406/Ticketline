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

  // Inject dependencies
  constructor(
    private toastr: ToastrService,
    private performanceService: PerformanceService,
    private locationService: LocationService,
    private artistService: ArtistService,
    private ticketService: TicketService,
    private cartService: CartService,
    private route: ActivatedRoute
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

  loadTicketsByPerformance(performanceId: number): void {
    this.ticketService.getTicketsByPerformanceId(performanceId).subscribe({
      next: (tickets: TicketDto[]) => {
        // Initialize seatedBackC arrays for each row
        const seatedBackCRows: { [key: number]: TicketDto[] } = {};

        // Group tickets for sector C by row number
        tickets.forEach((ticket) => {
          if (ticket.sectorType === SectorType.C) {
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

        // Filter and sort tickets for Sector B
        this.seatedBackB = tickets
          .filter(ticket => ticket.sectorType === SectorType.B)
          .sort((a, b) => a.rowNumber - b.rowNumber || a.seatNumber - b.seatNumber);

        // Filter standing tickets for Sector A
        const standingTickets = tickets.filter(
          ticket => ticket.sectorType === SectorType.A && ticket.ticketType === TicketType.STANDING
        );

        // Count VIP and Premium standing tickets
        this.vipStandingTickets = standingTickets.filter(
          ticket => ticket.priceCategory === PriceCategory.VIP && ticket.status === 'AVAILABLE'
        ).length;

        this.standingTickets = standingTickets.filter(
          ticket => ticket.priceCategory === PriceCategory.PREMIUM && ticket.status === 'AVAILABLE'
        ).length;

        // Extract prices for VIP and Premium standing tickets
        const firstVipStanding = standingTickets.find(ticket => ticket.priceCategory === PriceCategory.VIP);
        const firstRegularStanding = standingTickets.find(ticket => ticket.priceCategory === PriceCategory.PREMIUM);

        this.vipStandingPrice = firstVipStanding?.price || 0;
        this.regularStandingPrice = firstRegularStanding?.price || 0;
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
    if (!ticket) return; // Ensure ticket is valid

    // Find the index of the ticket in the selectedTickets array
    const index = this.selectedTickets.findIndex(
      (t) =>
        t.rowNumber === ticket.rowNumber &&
        t.seatNumber === ticket.seatNumber &&
        t.sectorType === ticket.sectorType
    );

    if (index > -1) {
      // Deselect the ticket if it's already selected
      this.selectedTickets.splice(index, 1);
      this.updateTotalPrice();
      return;
    }

    if (ticket.status !== 'AVAILABLE') {
      this.toastr.error('This ticket is not available.', 'Error');
      return;
    }

    // Check if the max ticket limit is reached
    const totalSelected = this.selectedTickets.length + this.selectedStanding.vip + this.selectedStanding.premium;
    if (totalSelected >= 5) {
      this.toastr.error('You cannot select more than 5 tickets.', 'Error');
      return;
    }

    // Add the ticket to the selection
    this.selectedTickets.push(ticket);
    this.updateTotalPrice();
  }


  toggleStandingSector(priceCategory: PriceCategory): void {
    const totalSelected = this.selectedTickets.length + this.selectedStanding.vip + this.selectedStanding.premium;

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
    } else if (priceCategory === this.priceCategory.PREMIUM) {
      if (this.selectedStanding.premium > 0) {
        this.selectedStanding.premium = 0;
      } else {
        if (totalSelected >= 5) {
          this.toastr.error('You cannot select more than 5 tickets.', 'Error');
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

    if (totalSelected > 5) {
      this.toastr.error('You cannot select more than 5 tickets.', 'Error');
      if (type === 'vip') {
        this.selectedStanding.vip = Math.max(0, 5 - this.selectedTickets.length - this.selectedStanding.premium);
      } else if (type === 'premium') {
        this.selectedStanding.premium = Math.max(0, 5 - this.selectedTickets.length - this.selectedStanding.vip);
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

    const updateRequests = [];

    // Handle seated tickets
    this.selectedTickets.forEach(ticket => {
      ticket.status = 'RESERVED';
      updateRequests.push(this.ticketService.updateTicket(ticket));
    });

    // Handle standing tickets (VIP and Standard)
    forkJoin([
      this.getAvailableStandingTickets(PriceCategory.VIP, this.selectedStanding.vip),
      this.getAvailableStandingTickets(PriceCategory.PREMIUM, this.selectedStanding.premium)
    ]).subscribe({
      next: ([vipTickets, standardTickets]) => {
        [...vipTickets, ...standardTickets].forEach(ticket => {
          ticket.status = 'RESERVED';
          updateRequests.push(this.ticketService.updateTicket(ticket));
        });

        forkJoin(updateRequests).subscribe({
          next: () => {
            this.toastr.success(`Successfully reserved ${this.totalTickets} tickets!`, 'Reservation Successful');
            this.resetSelections();
            this.loadTicketsByPerformance(this.performanceID);
          },
          error: (err) => {
            console.error('Error reserving tickets:', err);
            this.toastr.error('Failed to reserve tickets. Please try again.', 'Error');
          }
        });
      },
      error: (err) => {
        console.error('Error fetching standing tickets:', err);
        this.toastr.error('Failed to fetch standing tickets. Please try again.', 'Error');
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

    const updateRequests = [];

    this.selectedTickets.forEach(ticket => {
      ticket.status = 'RESERVED';
      this.cartService.addToCart(ticket);
      updateRequests.push(this.ticketService.updateTicket(ticket));
    });

    if (this.selectedStanding.vip > 0) {
      const remainingVipTickets = this.vipStandingTickets - this.selectedStanding.vip;

      if (remainingVipTickets >= 0) {
        this.vipStandingTickets = remainingVipTickets;

        this.getAvailableStandingTickets(PriceCategory.VIP, this.selectedStanding.vip).subscribe({
          next: vipTickets => {
            vipTickets.forEach(ticket => {
              ticket.status = 'RESERVED';
              this.cartService.addToCart(ticket);
              updateRequests.push(this.ticketService.updateTicket(ticket));
            });
            this.toastr.success(`${this.selectedStanding.vip} VIP standing tickets added to cart!`, "Success");
          },
          error: err => {
            console.error('Error fetching VIP standing tickets:', err);
            this.toastr.error('Failed to add VIP standing tickets to the cart.', 'Error');
            this.vipStandingTickets += this.selectedStanding.vip;
          }
        });
      } else {
        this.toastr.error('Not enough VIP standing tickets available.', 'Error');
      }
    }

    if (this.selectedStanding.premium > 0) {
      const remainingPremiumTickets = this.standingTickets - this.selectedStanding.premium;

      if (remainingPremiumTickets >= 0) {
        this.standingTickets = remainingPremiumTickets;

        this.getAvailableStandingTickets(PriceCategory.PREMIUM, this.selectedStanding.premium).subscribe({
          next: premiumTickets => {
            premiumTickets.forEach(ticket => {
              ticket.status = 'RESERVED';
              this.cartService.addToCart(ticket);
              updateRequests.push(this.ticketService.updateTicket(ticket));
            });
            this.toastr.success(`${this.selectedStanding.premium} Premium standing tickets added to cart!`, "Success");
          },
          error: err => {
            console.error('Error fetching Premium standing tickets:', err);
            this.toastr.error('Failed to add Premium standing tickets to the cart.', 'Error');
            this.standingTickets += this.selectedStanding.premium;
          }
        });
      } else {
        this.toastr.error('Not enough Premium standing tickets available.', 'Error');
      }
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
  }
}
