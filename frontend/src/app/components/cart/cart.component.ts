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
import {TicketService} from "../../services/ticket.service";
import cardValidator from 'card-validator';

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
  merchandiseCounter = 0;
  ticketCounter = 0;
  ticketTaxAmount = 0;
  merchandiseTaxAmount = 0;

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

  validationResult: string = '';

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

  calculateTaxAmounts() {
    for (let i = 0; i < this.cartItems.length; i++) {
      if (this.isMerchandise(this.cartItems[i].item)) {
        const mTax = ((this.cartItems[i].item.price / 120) * 100) * 0.2;
        this.merchandiseTaxAmount = this.merchandiseTaxAmount + (mTax*this.cartItems[i].quantity);
      } else if (this.isTicket(this.cartItems[i].item)) {
        const tTax = ((this.cartItems[i].item.price / 113) * 100) * 0.13;
        this.ticketTaxAmount = this.ticketTaxAmount + tTax;
      }
    }
  }

  countTicketMerchandiseInCart() {
    for (let i = 0; i < this.cartItems.length; i++) {
      if (this.isMerchandise(this.cartItems[i].item)) {
        this.merchandiseCounter = this.merchandiseCounter + 1;
      } else if (this.isTicket(this.cartItems[i].item)) {
        this.ticketCounter = this.ticketCounter + 1;
      }
    }
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
      this.ticketService.updateTicket(ticket.ticketId ,ticket).subscribe({
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

  validateCreditCard(): boolean {
    const creditCardValidation = cardValidator.number(this.paymentDetails.creditCardNumber);
    if (!creditCardValidation.isValid) {
      this.validationResult = 'Invalid credit card number';
      return false;
    }
    return true;
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
    this.countTicketMerchandiseInCart();
    this.calculateTaxAmounts();

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

    if (this.paymentDetails.bankAccount === '' && this.paymentDetails.creditCardNumber === '' &&
      this.paymentDetails.paypalEmail === '' && this.selectedPaymentOption != 'points') {
      this.toastr.error('Insufficient payment details.');
      return;
    }

    if (this.selectedPaymentOption === 'creditCard' && !this.validateCreditCard()) {
      this.toastr.error('Invalid credit card number.');
      return;
    }

    if (this.selectedPaymentOption === 'points' && this.accountPoints < this.getTotalPoints()) {
      this.toastr.error('You do not have enough points.');
      return;
    }

    const merchandise: number[] = [];
    const merchandiseQuantities: number[] = [];
    const tickets: number[] = [];

    this.cartItems.forEach(cartItem => {
      if (this.isMerchandise(cartItem.item)) {
        merchandise.push(cartItem.item.merchandiseId);
        merchandiseQuantities.push(cartItem.quantity);
      } else if (this.isTicket(cartItem.item)) {
        tickets.push(cartItem.item.ticketId);
      }
    });

    if (this.selectedPaymentOption === 'points') {
      if (tickets.length > 0) {
        this.toastr.info(
          'Only merchandise will be purchased with points. Tickets remain in the cart.',
          'Notice'
        );
      }

      if (merchandise.length === 0) {
        this.toastr.error('No merchandise in the cart to purchase with points.');
        return;
      }
    }

    const totalPrice = this.getTotalPrice();
    const today = new Date();
    const purchasePayload: Purchase = {
      userId: this.authService.getUserIdFromToken(),
      ticketIds: this.selectedPaymentOption === 'points' ? [] : tickets,
      merchandiseIds: merchandise,
      merchandiseQuantities: merchandiseQuantities,
      totalPrice: totalPrice,
      purchaseDate: today.toISOString(),
      street: this.address.street,
      postalCode: this.address.postalCode,
      city: this.address.city
    };

    this.purchaseService.createPurchase(purchasePayload).subscribe({
      next: async () => {
        this.generatePDF()
        if (this.selectedPaymentOption === 'points') {
          await this.cartService.deductPoints(this.getTotalPoints());
        } else {
          await this.cartService.addPoints(this.getTotalPointsToAdd());
        }

        // Update cart: Retain tickets if using points
        if (this.selectedPaymentOption === 'points') {
          this.cartItems = this.cartItems.filter(cartItem =>
            this.isTicket(cartItem.item)
          );
          this.cartService.saveCart(this.cartItems);
        } else {
          this.cartService.clearCart();
        }
        this.toastr.success('Thank you for your purchase.');
        await this.router.navigate(['merchandise']);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Purchase Error:', error);
        if (error.error) {
          this.toastr.error(`Error: ${error.error.message || error.message}`);
        } else {
          this.toastr.error('An unexpected error occurred. Please try again.');
        }
      }
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
