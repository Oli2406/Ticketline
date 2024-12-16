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
import {forEach} from "lodash";
import {count} from "rxjs";

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
              private router: Router,
              private global: Globals) {
  }

  ngOnInit(): void {
    this.cartItems = this.cartService.getCart();
    this.fetchAccountPoints();
    this.fetchUser();
    this.fetchAllPerformanceNames();
    this.loadInvoiceCounter();
    this.imageLocation = this.global.backendRessourceUri + '/merchandise/';
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
    this.cartItems = this.cartService.getCart();
  }

  getTotalPrice(): number {
    return this.cartItems.reduce((sum, cartItem) => sum + cartItem.item.price * cartItem.quantity, 0);
  }

  getTotalPoints(): number {
    return this.cartItems.reduce((sum, cartItem) => sum + ('points' in cartItem.item ? cartItem.item.points : 0) * cartItem.quantity, 0);
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
      // Merchandise item - return its name
      return item.name;
    } else if ('performanceId' in item) {
      // Ticket item - build ticket description
      const performanceId = item.performanceId;
      const performanceName = this.performanceCache[performanceId] || 'Loading...';

      if (item.ticketType === 'SEATED') {
        // Seated ticket - include row and seat details
        return `Ticket for ${performanceName} - Row ${item.rowNumber}, Seat ${item.seatNumber}`;
      } else if (item.ticketType === 'STANDING') {
        // Standing ticket - include standing type
        const standingType =
          item.priceCategory === 'VIP' ? 'VIP Standing' : 'Regular Standing';
        return `Ticket for ${performanceName} - ${standingType}`;
      }

      // Fallback for tickets without specific type
      return `Ticket for ${performanceName}`;
    }

    // Fallback for unknown item type
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
    if (this.selectedPaymentOption === 'points' && this.accountPoints < this.getTotalPoints()) {
      this.toastr.error('You do not have enough points.');
      return;
    }
    if (this.showPaymentDetails) {
      if (
        (this.selectedPaymentOption === 'creditCard' && !this.paymentDetails.creditCardNumber) ||
        (this.selectedPaymentOption === 'paypal' && !this.paymentDetails.paypalEmail) ||
        (this.selectedPaymentOption === 'bankTransfer' && !this.paymentDetails.bankAccount)
      ) {
        this.toastr.error('Please fill in the required payment details.');
        return;
      }
    }
    if (this.cartItems.length === 0) {
      this.toastr.error('Your cart is empty.');
      return;
    }

    try {
      const purchasePayload = this.cartItems.map(cartItem => ({
        itemId: 'merchandiseId' in cartItem.item ? cartItem.item.merchandiseId : cartItem.item.ticketId,
        quantity: cartItem.quantity,
      }));
      this.invoiceCounter++;
      this.saveInvoiceCounter();
      await this.cartService.purchaseItems(purchasePayload);
      this.toastr.success('Thank you for your purchase.');
      if (this.selectedPaymentOption == 'points'){
        this.cartService.deductPoints(this.getTotalPoints());
      }
      this.cartService.clearCart();
      this.receiptService.exportToPDF();
      await this.router.navigate(['merchandise']);
    } catch (error) {
      if (error instanceof HttpErrorResponse && error.status === 409) {
        const backendMessage = error.error?.error || 'Error processing your purchase.';
        this.toastr.error(backendMessage);
      } else {
        this.toastr.error('An unexpected error occurred. Please try again.');
      }
    }
  }
}
