import { Injectable } from '@angular/core';
import { Merchandise } from '../dtos/merchandise';
import { AuthService } from "./auth.service";

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private currentUserId: string = '';
  private readonly CART_STORAGE_KEY_PREFIX = 'cart_';

  constructor(private authService: AuthService) {}



  private getCartKey(): string {
    this.currentUserId = this.authService.getUserIdFromToken();
    return `${this.CART_STORAGE_KEY_PREFIX}${this.currentUserId}`;
  }

  getCart(): { item: Merchandise; quantity: number }[] {
    const storedCart = localStorage.getItem(this.getCartKey());
    return storedCart ? JSON.parse(storedCart) : [];
  }

  private saveCart(cartItems: { item: Merchandise; quantity: number }[]): void {
    localStorage.setItem(this.getCartKey(), JSON.stringify(cartItems));
  }

  addToCart(item: Merchandise): void {
    const cartItems = this.getCart();
    const existingItem = cartItems.find(cartItem => cartItem.item.merchandiseId === item.merchandiseId);

    if (existingItem) {
      existingItem.quantity++;
    } else {
      cartItems.push({ item, quantity: 1 });
    }

    this.saveCart(cartItems);
  }

  updateCartItem(item: Merchandise, quantity: number): void {
    const cartItems = this.getCart();
    const cartItem = cartItems.find(cartItem => cartItem.item.merchandiseId === item.merchandiseId);

    if (cartItem) {
      cartItem.quantity = quantity;
      this.saveCart(cartItems);
    }
  }

  removeFromCart(item: Merchandise): void {
    let cartItems = this.getCart();
    cartItems = cartItems.filter(cartItem => cartItem.item.merchandiseId !== item.merchandiseId);
    this.saveCart(cartItems);
  }

  clearCart(): void {
    localStorage.removeItem(this.getCartKey());
  }
}
