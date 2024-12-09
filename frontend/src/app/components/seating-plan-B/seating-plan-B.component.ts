import { Component } from '@angular/core';

@Component({
  selector: 'app-seating-plan-B',
  templateUrl: './seating-plan-B.component.html',
  styleUrls: ['./seating-plan-B.component.scss'],
})
export class SeatingPlanBComponent {
  frontSeats = this.generateSeats(5, 10, 'Front'); // Front Section
  backSeats = this.generateSeatsBack(5, 10, 'Back'); // Back Section (Correct Row Order)
  leftSeats = this.generateSeatsRotated(5, 10, 'Left'); // Left Section (Rotated)
  rightSeats = this.generateSeatsRotated(5, 10, 'Right'); // Right Section (Rotated)

  selectedTickets = [];
  totalTickets = 0;
  totalPrice = 0;

  // Generate standard grid seats
  generateSeats(rows: number, cols: number, sector: string) {
    const seats = [];
    for (let row = 1; row <= rows; row++) {
      for (let col = 1; col <= cols; col++) {
        seats.push({
          sector,
          row,
          col,
          price: 50,
          status: Math.random() > 0.8 ? 'RESERVED' : 'AVAILABLE',
        });
      }
    }
    return seats;
  }

  // Generate Back Grid with Correct Row Order (Row 1 becomes Row 5, etc.)
  generateSeatsBack(rows: number, cols: number, sector: string) {
    const seats = [];
    for (let row = rows; row >= 1; row--) {
      for (let col = 1; col <= cols; col++) {
        seats.push({
          sector,
          row: rows - row + 1, // Reverse row numbering
          col,
          price: 50,
          status: Math.random() > 0.8 ? 'RESERVED' : 'AVAILABLE',
        });
      }
    }
    return seats;
  }

  // Generate rotated grid seats for left and right
  generateSeatsRotated(rows: number, cols: number, sector: string) {
    const seats = [];
    for (let col = 1; col <= cols; col++) {
      for (let row = rows; row >= 1; row--) {
        seats.push({
          sector,
          row,
          col,
          price: 50,
          status: Math.random() > 0.8 ? 'RESERVED' : 'AVAILABLE',
        });
      }
    }
    return seats;
  }

  toggleTicketSelection(seat: any) {
    if (seat.status !== 'AVAILABLE') return;

    const index = this.selectedTickets.indexOf(seat);
    if (index > -1) {
      this.selectedTickets.splice(index, 1);
    } else {
      this.selectedTickets.push(seat);
    }

    this.updateTotal();
  }

  updateTotal() {
    this.totalTickets = this.selectedTickets.length;
    this.totalPrice = this.selectedTickets.reduce((sum, seat) => sum + seat.price, 0);
  }

  resetSelections() {
    this.selectedTickets = [];
    this.updateTotal();
  }

  reserveTickets() {
    if (this.totalTickets === 0) return;

    this.selectedTickets.forEach((seat) => (seat.status = 'RESERVED'));
    this.resetSelections();
  }

  buyTickets() {
    if (this.totalTickets === 0) return;

    this.selectedTickets.forEach((seat) => (seat.status = 'SOLD'));
    this.resetSelections();
  }

  getClass(seat: any) {
    return {
      available: seat.status === 'AVAILABLE',
      reserved: seat.status === 'RESERVED',
      'selected-seat': this.selectedTickets.includes(seat),
    };
  }
}
