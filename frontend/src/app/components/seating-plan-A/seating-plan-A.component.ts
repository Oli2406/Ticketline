import {Component} from '@angular/core';
import {Hall, PriceCategory, SectorType, TicketDto, TicketType} from "../../dtos/ticket";
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-seating-plan-A',
  templateUrl: './seating-plan-A.component.html',
  styleUrls: ['./seating-plan-A.component.scss'],
})
export class SeatingPlanAComponent {
  // Standing Tickets
  standingTickets: number = 100;
  vipStandingTickets: number = 80;

  // Selected Tickets and Info
  selectedTickets: TicketDto[] = [];
  selectedStanding: { vip: number; regular: number } = { vip: 0, regular: 0 };

  // Enums for easier reference
  priceCategory = PriceCategory;
  ticketType = TicketType;
  sectorType = SectorType;

  // Total tickets and price
  totalTickets: number = 0;
  totalPrice: number = 0;

  // Inject ToastrService
  constructor(private toastr: ToastrService) {}

  // Tickets for seated sections
  seatedBackA: TicketDto[] = this.generateSeatedTickets(12, 20, 40, SectorType.A); // Sector A
  seatedBackB: TicketDto[] = this.generateSeatedTickets(12, 20, 40, SectorType.B); // Sector B


  private generateSeatedTickets(rows: number, seatsPerRow: number, price: number, sector: SectorType): TicketDto[] {
    const tickets: TicketDto[] = [];
    let ticketId = 1;
    for (let row = 1; row <= rows; row++) {
      for (let seat = 1; seat <= seatsPerRow; seat++) {
        tickets.push({
          ticketId: ticketId++,
          rowNumber: row,
          seatNumber: seat,
          priceCategory: PriceCategory.STANDARD,
          ticketType: TicketType.SEATED,
          sectorType: sector,
          price,
          status: Math.random() > 0.8 ? 'RESERVED' : 'AVAILABLE', // Randomly assign reserved/available
          performanceId: 1,
          hall: Hall.A,
          date: new Date(),
        });
      }
    }
    return tickets;
  }


  toggleTicketSelection(ticket: TicketDto): void {
    const index = this.selectedTickets.findIndex((t) => t.ticketId === ticket.ticketId);

    if (index > -1) {
      // Allow deselecting tickets even if the cap is reached
      this.selectedTickets.splice(index, 1);
      this.updateTotalPrice();
      return;
    }

    // Prevent selecting more than 5 tickets
    const totalSelected = this.selectedTickets.length + this.selectedStanding.vip + this.selectedStanding.regular;
    if (totalSelected >= 5) {
      this.toastr.error('You cannot select more than 5 tickets.', 'Error');
      return;
    }

    // Select the ticket if available
    if (ticket.status !== 'AVAILABLE') {
      this.toastr.error('This ticket is not available.', 'Error');
      return;
    }

    this.selectedTickets.push(ticket);
    this.updateTotalPrice();
  }

  // Toggle standing sector selection
  toggleStandingSector(ticketType: TicketType, priceCategory: PriceCategory, price: number): void {
    const totalSelected = this.selectedTickets.length + this.selectedStanding.vip + this.selectedStanding.regular;

    if (priceCategory === this.priceCategory.VIP) {
      if (this.selectedStanding.vip > 0) {
        // Allow deselecting VIP tickets
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
        // Allow deselecting regular tickets
        this.selectedStanding.regular = 0;
      } else {
        if (totalSelected >= 5) {
          this.toastr.error('You cannot select more than 5 tickets.', 'Error');
          return;
        }
        if (this.standingTickets <= 0) {
          this.toastr.warning('No Standing tickets available!', 'Warning');
          return;
        }
        this.selectedStanding.regular = 1;
      }
    }
    this.updateTotalPrice();
  }
  validateStandingTickets(type: 'vip' | 'regular'): void {
    const totalSelected = this.selectedTickets.length + this.selectedStanding.vip + this.selectedStanding.regular;

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

  // Update total price and total tickets
  updateTotalPrice(): void {
    const seatedPrice = this.selectedTickets.reduce((sum, ticket) => sum + ticket.price, 0);
    const standingVipPrice = this.selectedStanding.vip * 100;
    const standingRegularPrice = this.selectedStanding.regular * 70;

    this.totalTickets = this.selectedTickets.length + this.selectedStanding.vip + this.selectedStanding.regular;
    this.totalPrice = seatedPrice + standingVipPrice + standingRegularPrice;
  }

  // Reset selections
  public resetSelections(): void {
    this.selectedTickets = [];
    this.selectedStanding = { vip: 0, regular: 0 };
    this.totalTickets = 0;
    this.totalPrice = 0;
  }

  // Reserve tickets
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

  // Buy tickets
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

  // Get ticket CSS class
  getClass(ticket: TicketDto): { [key: string]: boolean } {
    return {
      available: ticket.status === 'AVAILABLE',
      reserved: ticket.status === 'RESERVED',
      sold: ticket.status === 'SOLD',
      'selected-seat': this.selectedTickets.includes(ticket),
    };
  }
}
