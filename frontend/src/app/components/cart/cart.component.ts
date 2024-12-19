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
    if (this.cartItems.length === 0) {
      this.toastr.error('Your cart is empty.');
      return;
    }
    if (this.selectedPaymentOption === 'points' && this.accountPoints < this.getTotalPoints()) {
      this.toastr.error('You do not have enough points.');
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
      purchaseDate: today.toISOString().split('T')[0],
    };

    console.log('Purchase Payload:', JSON.stringify(purchasePayload));

    this.purchaseService.createPurchase(purchasePayload).subscribe({
      next: async () => {
        try {
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
}
