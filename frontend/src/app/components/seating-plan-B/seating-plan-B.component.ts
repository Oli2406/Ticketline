import {Component} from '@angular/core';
import {Hall, PriceCategory, SectorType, TicketDto, TicketType} from "../../dtos/ticket";
import {ToastrService} from 'ngx-toastr';
import { PerformanceService } from 'src/app/services/performance.service';
import {PerformanceListDto, PerformanceWithNamesDto} from 'src/app/dtos/performance';
import { LocationService } from 'src/app/services/location.service';
import { ArtistService } from 'src/app/services/artist.service';
import {Artist, ArtistListDto} from "../../dtos/artist";
import {Location, LocationListDto} from "../../dtos/location";


@Component({
  selector: 'app-seating-plan-B',
  templateUrl: './seating-plan-B.component.html',
  styleUrls: ['./seating-plan-B.component.scss'],
})
export class SeatingPlanBComponent {
  standingTickets: number = 80;
  vipStandingTickets: number = 60;

  selectedTickets: TicketDto[] = [];
  selectedStanding: { vip: number; regular: number } = { vip: 0, regular: 0 };

  priceCategory = PriceCategory;
  ticketType = TicketType;
  sectorType = SectorType;

  totalTickets: number = 0;
  totalPrice: number = 0;

  performanceDetails: PerformanceListDto = null;
  artistDetails: Artist = null;
  locationDetails: Location = null;

  seatedFront: TicketDto[] = this.generateSeatedTickets(3, 14, 50, SectorType.A); // Front Section (Sector A)
  seatedBackRows: TicketDto[][] = this.generateSeatedRows(9, 15, 40, SectorType.B); // Back Section (Sector B)

  constructor(private toastr: ToastrService, private performanceService: PerformanceService, private locationService: LocationService, private artistService: ArtistService) {}

  ngOnInit(): void {
    this.getPerformanceDetails(3); // todo: change hardcoded

  }

  getPerformanceDetails(id: number): void {
    this.performanceService.getPerformanceById(id).subscribe({
      next: (performance) => {
        this.performanceDetails = performance;

        // Fetch artist details only after performanceDetails is populated
        if (this.performanceDetails.artistId) {
          this.artistService.getById(this.performanceDetails.artistId).subscribe({
            next: (artist) => {
              this.artistDetails = artist;
              console.log('Artist details:', this.artistDetails);
            },
            error: (err) => {
              console.error('Error fetching artist details:', err);
            },
          });
        }

        // Fetch location details only after performanceDetails is populated
        if (this.performanceDetails.locationId) {
          this.locationService.getById(this.performanceDetails.locationId).subscribe({
            next: (location) => {
              this.locationDetails = location;
              console.log('Location details:', this.locationDetails);
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
  // Generate tickets for Sector A
  private generateSeatedTickets(rows: number, initialSeats: number, price: number, sector: SectorType): TicketDto[] {
    const tickets: TicketDto[] = [];
    let ticketId = 1;

    for (let row = 1; row <= rows; row++) {
      for (let seat = 1; seat <= initialSeats; seat++) {
        tickets.push({
          ticketId: ticketId++,
          rowNumber: row,
          seatNumber: seat,
          priceCategory: PriceCategory.STANDARD,
          ticketType: TicketType.SEATED,
          sectorType: sector,
          price,
          status: Math.random() > 0.8 ? 'RESERVED' : 'AVAILABLE',
          performanceId: 1,
          hall: Hall.B,
          date: new Date()
        });
      }
    }
    return tickets;
  }

  // Generate tickets for Sector B with dynamic rows
  private generateSeatedRows(rows: number, initialSeats: number, price: number, sector: SectorType): TicketDto[][] {
    const ticketRows: TicketDto[][] = [];
    let ticketId = 1;

    for (let row = 1; row <= rows; row++) {
      const seatsPerRow = initialSeats + (row - 1) ; // Increase seats per row dynamically
      const rowTickets: TicketDto[] = [];

      for (let seat = 1; seat <= seatsPerRow; seat++) {
        rowTickets.push({
          ticketId: ticketId++,
          rowNumber: row,
          seatNumber: seat,
          priceCategory: PriceCategory.STANDARD,
          ticketType: TicketType.SEATED,
          sectorType: sector,
          price,
          status: Math.random() > 0.8 ? 'RESERVED' : 'AVAILABLE',
          performanceId: 1,
          hall: Hall.B,
          date: new Date()
        });
      }

      ticketRows.push(rowTickets);
    }

    return ticketRows;
  }

  toggleTicketSelection(ticket: TicketDto): void {
    const index = this.selectedTickets.findIndex((t) => t.ticketId === ticket.ticketId);

    if (index > -1) {
      // Deselecting the ticket
      this.selectedTickets.splice(index, 1);
      this.updateTotalPrice();
      return;
    }

    // Prevent selection if total tickets exceed 5
    const totalSelected = this.selectedTickets.length + this.selectedStanding.vip + this.selectedStanding.regular;
    if (totalSelected >= 5) {
      this.toastr.error('You cannot select more than 5 tickets.', 'Error');
      return;
    }

    // Select the ticket
    if (ticket.status !== 'AVAILABLE') {
      this.toastr.error('This ticket is not available.', 'Error');
      return;
    }

    this.selectedTickets.push(ticket);
    this.updateTotalPrice();
  }



  toggleStandingSector(ticketType: TicketType, priceCategory: PriceCategory, price: number): void {
    const totalSelected = this.selectedTickets.length + this.selectedStanding.vip + this.selectedStanding.regular;

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
      if (this.selectedStanding.regular > 0) {
        this.selectedStanding.regular = 0;
      } else {
        if (totalSelected >= 5) {
          this.toastr.error('You cannot select more than 5 tickets.', 'Error');
          return;
        }
        if (this.standingTickets <= 0) {
          this.toastr.warning('No Regular Standing tickets available!', 'Warning');
          return;
        }
        this.selectedStanding.regular = 1;
      }
    }

    this.updateTotalPrice();
  }



  updateTotalPrice(): void {
    const seatedPrice = this.selectedTickets.reduce((sum, ticket) => sum + ticket.price, 0);
    const standingVipPrice = this.selectedStanding.vip * 100;
    const standingRegularPrice = this.selectedStanding.regular * 70;

    this.totalTickets = this.selectedTickets.length + this.selectedStanding.vip + this.selectedStanding.regular;
    this.totalPrice = seatedPrice + standingVipPrice + standingRegularPrice;
  }

  resetSelections(): void {
    this.selectedTickets = [];
    this.selectedStanding = { vip: 0, regular: 0 };
    this.totalTickets = 0;
    this.totalPrice = 0;
  }

  reserveTickets(): void {
    if (this.totalTickets === 0) {
      this.toastr.error('No tickets selected to reserve!', 'Warning');
      return;
    }

    this.selectedTickets.forEach((ticket) => (ticket.status = 'RESERVED'));
    this.vipStandingTickets -= this.selectedStanding.vip;
    this.standingTickets -= this.selectedStanding.regular;

    this.toastr.success(`Successfully reserved ${this.totalTickets} tickets!`, 'Reservation Successful');
    this.resetSelections();
  }

  buyTickets(): void {
    if (this.totalTickets === 0) {
      this.toastr.error('No tickets selected to buy!', 'Cannot buy tickets:');
      return;
    }

    this.selectedTickets.forEach((ticket) => (ticket.status = 'SOLD'));
    this.vipStandingTickets -= this.selectedStanding.vip;
    this.standingTickets -= this.selectedStanding.regular;

    this.toastr.success(`Successfully purchased ${this.totalTickets} tickets for ${this.totalPrice}€!`, 'Purchase Successful');
    this.resetSelections();
  }

  getClass(ticket: TicketDto): { [key: string]: boolean } {
    return {
      available: ticket.status === 'AVAILABLE',
      reserved: ticket.status === 'RESERVED',
      sold: ticket.status === 'SOLD',
      'selected-seat': this.selectedTickets.includes(ticket),
    };
  }
  validateStandingTickets(type: 'vip' | 'regular'): void {
    const totalSelected = this.selectedTickets.length + this.selectedStanding.vip + this.selectedStanding.regular;

    // Check if the total exceeds the limit
    if (totalSelected > 5) {
      this.toastr.error('You cannot select more than 5 tickets.', 'Error');
      if (type === 'vip') {
        this.selectedStanding.vip = Math.max(0, 5 - this.selectedTickets.length - this.selectedStanding.regular);
      } else if (type === 'regular') {
        this.selectedStanding.regular = Math.max(0, 5 - this.selectedTickets.length - this.selectedStanding.vip);
      }
    }

    this.updateTotalPrice();
  }

}

