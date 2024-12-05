import {Component, OnInit} from '@angular/core';
import {CartService} from '../../services/cart.service';
import {Merchandise} from "../../dtos/merchandise";
import {FormsModule} from "@angular/forms";
import {CommonModule, DecimalPipe} from "@angular/common";
import {AuthService} from "../../services/auth.service";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  standalone: true,
  imports: [
    FormsModule,
    DecimalPipe,
    CommonModule
  ],
  styleUrls: ['./cart.component.scss']
})
export class CartComponent implements OnInit {
  cartItems: { item: Merchandise; quantity: number }[] = [];

  selectedPaymentOption: string = 'creditCard'
  protected accountPoints: number;

  constructor(private cartService: CartService,
              private authService: AuthService,
              private toastr: ToastrService,
              private router: Router) {}

  ngOnInit(): void {
    this.cartItems = this.cartService.getCart();
    this.fetchAccountPoints()
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

  async buy(): Promise<void> {
    if (!this.selectedPaymentOption) {
      this.toastr.error('Please select a payment option.');
      return;
    }

    if (this.cartItems.length === 0) {
      this.toastr.error('Your cart is empty.');
      return;
    }

    if (this.selectedPaymentOption === 'points') {
      try {
        await this.fetchAccountPoints();
        if (this.getTotalPoints() > this.accountPoints) {
          this.toastr.error('You do not have enough points to buy this item.');
          return;
        }
        await this.cartService.deductPoints(this.getTotalPoints());
        this.toastr.success('Thank you for your purchase.');
        this.cartService.clearCart();
        this.router.navigate(['merchandise']);
      } catch (error) {
        console.error('Error during purchase:', error);
        this.toastr.error('An error occurred while processing your purchase. Please try again.');
      }
    } else if (this.selectedPaymentOption !== 'points') {
      try {
        const pointsToAdd = this.getPointsForMoney();
        await this.cartService.addPoints(pointsToAdd);
        this.toastr.success('Thank you for your purchase. Points have been added to your account.');
        this.cartService.clearCart();
        this.router.navigate(['merchandise']);
      } catch (error) {
        console.error('Error during purchase:', error);
        this.toastr.error('An error occurred while processing your purchase. Please try again.');
      }
    } else {
      this.toastr.success('Thank you for your purchase.');
      this.cartService.clearCart();
      this.router.navigate(['merchandise']);
    }
  }
}
