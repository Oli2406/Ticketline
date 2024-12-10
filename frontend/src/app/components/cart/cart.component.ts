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

  imageLocation: string = "";

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

  getPointsForMoney(): number {
    const PointsToAdd = this.getTotalPrice();
    return Math.trunc(PointsToAdd);
  }

  public generatePDF() {
    this.receiptService.exportToPDF();
  }

  public setInvoiceDate() {
    return new Date();
  }

  async buy(): Promise<void> {
    this.receiptService.exportToPDF();
    if (!this.selectedPaymentOption) {
      this.toastr.error('Please select a payment option.');
      return;
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
      if (this.selectedPaymentOption === 'points') {
        this.fetchAccountPoints();
        if (this.getTotalPoints() > this.accountPoints) {
          this.toastr.error('You do not have enough points to buy this item.');
          return;
        }
        await this.cartService.deductPoints(this.getTotalPoints());
      } else {
        const pointsToAdd = this.getPointsForMoney();
        await this.cartService.addPoints(pointsToAdd);
      }
      this.toastr.success('Thank you for your purchase.');
      this.cartService.clearCart();
      await this.router.navigate(['merchandise']);
    } catch (error) {
      console.log(error);
      this.toastr.error('Not enough stock left');
    }
  }
}
