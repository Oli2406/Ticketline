import {Component, OnInit} from '@angular/core';
import {CartService} from '../../services/cart.service';
import {Merchandise} from "../../dtos/merchandise";
import {TicketDto} from "../../dtos/ticket";
import {PerformanceListDto} from "../../dtos/performance";
import {FormsModule} from "@angular/forms";
import {CommonModule, DecimalPipe, NgOptimizedImage} from "@angular/common";
import {AuthService} from "../../services/auth.service";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {Globals} from "../../global/globals";
import {ReceiptService} from "../../services/receipt.service";
import {HttpErrorResponse} from "@angular/common/http";
import {PerformanceService} from 'src/app/services/performance.service';
import {Purchase} from "../../dtos/purchase";
import {PurchaseService} from "../../services/purchase.service";
import {DatePipe} from "@angular/common";
import {TicketService} from "../../services/ticket.service";

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  standalone: true,
  imports: [
    FormsModule,
    DecimalPipe,
    CommonModule,
    NgOptimizedImage
  ],
  styleUrls: ['./cart.component.scss']
})
export class CartComponent implements OnInit {
  cartItems: { item: Merchandise | TicketDto; quantity: number }[] = [];
  userFirstName: string;
  userLastName: string;
  userEmail: string;

  selectedPaymentOption: string = 'creditCard';
  protected accountPoints: number;
  invoiceCounter: number = 1;

  imageLocation: string = this.global.backendRessourceUri + '/merchandise/';

  address = {
    street: '',
    postalCode: '',
    city: '',
  };

  paymentDetails = {
    creditCardNumber: '',
    paypalEmail: '',
    bankAccount: '',
  };

  performanceDetails: PerformanceListDto = null;
  performanceCache: { [id: number]: string } = {};
  isLoading: boolean = false;

  get showPaymentDetails(): boolean {
    return this.selectedPaymentOption !== 'points';
  }

  constructor(private cartService: CartService,
              private authService: AuthService,
              private toastr: ToastrService,
              private receiptService: ReceiptService,
              private performanceService: PerformanceService,
              private purchaseService: PurchaseService,
              private router: Router,
              private global: Globals,
              private ticketService: TicketService) {
  }

  ngOnInit(): void {
    this.checkAndRemoveExpiredItems()
    this.cartItems = this.cartService.getCart();
    this.fetchAccountPoints();
    this.fetchUser();
    this.fetchAllPerformanceNames();
    this.loadInvoiceCounter();
    this.imageLocation = this.global.backendRessourceUri + '/merchandise/';
    this.startPeriodicExpirationCheck();
    this.startPeriodicCountdown();
  }

  startPeriodicCountdown(): void {
    setInterval(() => {
      this.cartItems.forEach(cartItem => {
        if (this.isTicket(cartItem.item)) {
          const remainingTime = this.getTimeRemaining(cartItem.item as TicketDto);
          if (remainingTime === 'Expired') {
            this.removeFromCart(cartItem.item);
          }
        }
      });
    }, 1000);
  }

  getTimeRemaining(item: TicketDto): string {
    if (!item.reservedUntil) return 'N/A';
    const now = new Date().getTime();
    const timeUntil = new Date(item.reservedUntil).getTime();
    const diff = timeUntil - now;
    if (diff <= 0) {
      return 'Expired';
    }
    const minutes = Math.floor(diff / (1000 * 60));
    const seconds = Math.floor((diff % (1000 * 60)) / 1000);
    return `${minutes}m ${seconds}s`;
  }


  startPeriodicExpirationCheck(): void {
    setInterval(() => {
      this.checkAndRemoveExpiredItems();
    }, 60000);
  }

  get hasTicketsInCart(): boolean {
    return this.cartItems.some(cartItem => this.isTicket(cartItem.item));
  }

  get hasMerchInCart(): boolean {
    return this.cartItems.some(cartItem => this.isMerchandise(cartItem.item));
  }

  fetchAccountPoints(): void {
    const email = this.authService.getUserEmailFromToken();
    if (email) {
      this.authService.getUserPoints(email).subscribe({
        next: (points) => {
          this.accountPoints = points;
        },
        error: () => {
          this.accountPoints = 0;
        }
      });
    }
  }

  fetchUser(): void {
    this.userFirstName = this.authService.getUserFirstNameFromToken();
    this.userLastName = this.authService.getUserLastNameFromToken();
    this.userEmail = this.authService.getUserEmailFromToken();
  }

  private fetchAllPerformanceNames(): void {
    const performanceIds = new Set(
      this.cartItems
        .map((item) => ('performanceId' in item.item ? item.item.performanceId : null))
        .filter((id) => id !== null)
    );

    const fetchRequests = Array.from(performanceIds).map((id) =>
      this.performanceService.getPerformanceById(id).toPromise()
    );

    Promise.all(fetchRequests)
      .then((performances) => {
        performances.forEach((performance) => {
          this.performanceCache[performance.performanceId] = performance.name;
        });
      })
      .catch((error) => {
        console.error('Error fetching performances:', error);
        this.toastr.error('Failed to load performance names.');
      })
      .finally(() => {
        this.isLoading = false;
      });
  }

  updateQuantity(item: Merchandise | TicketDto, quantity: number): void {
    this.cartService.updateCartItem(item, quantity);
  }

  removeFromCart(item: Merchandise | TicketDto): void {
    this.cartService.removeFromCart(item);
    this.checkAndRemoveExpiredItems();
    this.cartItems = this.cartService.getCart();

    if (this.isTicket(item)) {
      const ticket = item as TicketDto;
      ticket.status = 'AVAILABLE';
      this.ticketService.updateTicket(ticket).subscribe({
        next: () => {
          this.toastr.success('Ticket successfully removed and marked as available.', 'Success');
        },
        error: (err) => {
          console.error('Error updating ticket status to AVAILABLE:', err);
          this.toastr.error('Failed to mark ticket as available. Please try again.', 'Error');
        }
      });
    }
  }

  getTotalPrice(): number {
    return this.cartItems.reduce((sum, cartItem) => sum + cartItem.item.price * cartItem.quantity, 0);
  }

  getTotalPoints(): number {
    return this.cartItems.reduce((sum, cartItem) => sum + ('points' in cartItem.item ? cartItem.item.points : 0) * cartItem.quantity, 0);
  }

  getTotalPointsToAdd(): number {
    const total = this.cartItems.reduce((sum, cartItem) => {
      return sum + cartItem.item.price * cartItem.quantity;
    }, 0);

    return Math.round(total);
  }

  formatCreditCardNumber(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/\D/g, '').replace(/(\d{4})/g, '$1-').replace(/-$/, '');
    this.paymentDetails.creditCardNumber = input.value;
  }

  formatBankAccountNumber(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/\D/g, '').replace(/(\d{4})/g, '$1-').replace(/-$/, '');
    this.paymentDetails.bankAccount = input.value;
  }

  public generatePDF(): void {
    this.receiptService.exportToPDF();
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

  isMerchandise(item: Merchandise | TicketDto): item is Merchandise {
    return (item as Merchandise).merchandiseId !== undefined;
  }

  getItemDisplayName(item: Merchandise | TicketDto): string {
    if ('name' in item && item.name) {
      return item.name;
    } else if ('performanceId' in item) {
      const performanceId = item.performanceId;
      const performanceName = this.performanceCache[performanceId] || 'Loading...';
      if (item.ticketType === 'SEATED') {
        return `Ticket for ${performanceName} - Row ${item.rowNumber}, Seat ${item.seatNumber}`;
      } else if (item.ticketType === 'STANDING') {
        const standingType =
          item.priceCategory === 'VIP' ? 'VIP Standing' : 'Regular Standing';
        return `Ticket for ${performanceName} - ${standingType}`;
      }
      return `Ticket for ${performanceName}`;
    }
    return 'Unknown Item';
  }

  isTicket(item: Merchandise | TicketDto): boolean {
    return 'performanceId' in item;
  }

  async buy(): Promise<void> {
    if (!this.selectedPaymentOption) {
      this.toastr.error('Please select a payment option.');
      return;
    }
    if (!this.address.street || !this.address.postalCode || !this.address.city) {
      this.toastr.error('Please fill in all address fields.');
      return;
    }
    if (this.cartItems.length === 0) {
      this.toastr.error('Your cart is empty.');
      return;
    }
    if (this.selectedPaymentOption === 'points' && this.accountPoints < this.getTotalPoints()) {
      this.toastr.error('You do not have enough points.');
      return;
    }

    const hasTickets = this.cartItems.some(cartItem => 'performanceId' in cartItem.item);
    if (this.selectedPaymentOption === 'points' && hasTickets) {
      this.toastr.error('You cannot buy tickets with points.');
      this.selectedPaymentOption = '';
      return;
    }

    const tickets: number[] = [];
    const merchandise: number[] = [];
    const merchandiseQuantities: number[] = [];

    this.cartItems.forEach(cartItem => {
      if ('ticketId' in cartItem.item) {
        tickets.push(cartItem.item.ticketId);
      } else if ('merchandiseId' in cartItem.item) {
        merchandise.push(cartItem.item.merchandiseId);
        merchandiseQuantities.push(cartItem.quantity);
      }
    });

    const totalPrice = this.getTotalPrice();
    const today = new Date();
    const purchasePayload: Purchase = {
      userId: this.authService.getUserIdFromToken(),
      ticketIds: tickets,
      merchandiseIds: merchandise,
      merchandiseQuantities: merchandiseQuantities,
      totalPrice: totalPrice,
      purchaseDate: today.toISOString(),
    };

    console.log('Purchase Payload:', JSON.stringify(purchasePayload));

    this.purchaseService.createPurchase(purchasePayload).subscribe({
      next: async () => {
        try {
          this.generatePDF();
          if (this.selectedPaymentOption === 'points') {
            await this.cartService.deductPoints(this.getTotalPoints());
          } else {
            await this.cartService.addPoints(this.getTotalPointsToAdd());
          }

          this.cartService.clearCart();
          this.toastr.success('Thank you for your purchase.');
          await this.router.navigate(['merchandise']);
        } catch (error) {
          console.error('Post-Purchase Error:', error);
          this.toastr.error('An unexpected error occurred. Please try again.');
        }
      },
      error: (error: HttpErrorResponse) => {
        console.error('Purchase Error:', error);
        if (error.error) {
          this.toastr.error(`Error: ${error.error.message || error.message}`);
        } else {
          this.toastr.error('An unexpected error occurred. Please try again.');
        }
      },
    });
  }

  updatePaymentOption(option: string): void {
    const hasTickets = this.cartItems.some(cartItem => 'performanceId' in cartItem.item);
    if (option === 'points' && hasTickets) {
      this.toastr.error('You cannot select points as a payment option when tickets are in the cart.');
      return;
    }
    this.selectedPaymentOption = option;
  }

  checkAndRemoveExpiredItems(): void {
    const cart = this.cartService.getCart();
    const now = new Date();

    const validCartItems = cart.filter(cartItem => {
      if (cartItem.item.reservedUntil) {
        const reservedUntil = new Date(cartItem.item.reservedUntil);
        return reservedUntil > now;
      }
      return true;
    });

    if (validCartItems.length !== cart.length) {
      this.cartService.saveCart(validCartItems);
      this.toastr.warning('Expired tickets have been removed from your cart.', 'Warning');
    }

    this.cartItems = validCartItems;
  }
}
