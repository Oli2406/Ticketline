import {Component, OnInit} from '@angular/core';
import {CartService} from '../../services/cart.service';
import {Merchandise} from "../../dtos/merchandise";
import {FormsModule} from "@angular/forms";
import {CommonModule, DecimalPipe, NgOptimizedImage} from "@angular/common";
import {AuthService} from "../../services/auth.service";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {Globals} from "../../global/globals";
import {ReceiptService} from "../../services/receipt.service";
import {HttpErrorResponse} from "@angular/common/http";

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
  cartItems: { item: Merchandise; quantity: number }[] = [];
  userFirstName: string;
  userLastName: string;
  userEmail: string;

  selectedPaymentOption: string = 'creditCard'
  protected accountPoints: number;


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

  get showPaymentDetails(): boolean {
    return this.selectedPaymentOption !== 'points';
  }

  constructor(private cartService: CartService,
              private authService: AuthService,
              private toastr: ToastrService,
              private receiptService: ReceiptService,
              private router: Router,
              private global: Globals) {
  }

  ngOnInit(): void {
    this.cartItems = this.cartService.getCart();
    this.fetchAccountPoints();
    this.fetchUser();
    this.imageLocation = this.global.backendRessourceUri + '/merchandise/'
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
    this.userEmail = this.authService.getUserEmailFromToken()
  }

  updateQuantity(item: Merchandise, quantity: number): void {
    this.cartService.updateCartItem(item, quantity);
  }

  removeFromCart(item: Merchandise): void {
    this.cartService.removeFromCart(item);
    this.cartItems = this.cartService.getCart();
  }

  getTotalPrice(): number {
    return this.cartItems.reduce((sum, cartItem) => sum + cartItem.item.price * cartItem.quantity, 0);
  }

  getTotalPoints(): number {
    return this.cartItems.reduce((sum, cartItem) => sum + cartItem.item.points * cartItem.quantity, 0);
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

  public generatePDF() {
    this.receiptService.exportToPDF();
  }

  public setInvoiceDate() {
    return new Date();
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
        itemId: cartItem.item.merchandiseId,
        quantity: cartItem.quantity,
      }));
      await this.cartService.purchaseItems(purchasePayload);
      this.toastr.success('Thank you for your purchase.');
      this.cartService.deductPoints(this.getTotalPoints());
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
