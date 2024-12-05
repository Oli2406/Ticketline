import { Injectable } from '@angular/core';
import { Merchandise } from '../dtos/merchandise';
import { AuthService } from "./auth.service";
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private currentUserId: string = '';
  private readonly CART_STORAGE_KEY_PREFIX = 'cart_';
  private readonly API_URL = 'http://localhost:8080/api/v1/users';

  constructor(private authService: AuthService,
              private http: HttpClient) {}



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

  deductPoints(points: number): Promise<void> {
    const encryptedId = this.authService.getUserIdFromToken();
    return this.http
      .post<void>(`${this.API_URL}/deduct-points`, null, {
        params: { encryptedId, points: points.toString() },
      })
      .toPromise();
  }

  addPoints(points: number): Promise<void> {
    const encryptedId = this.authService.getUserIdFromToken();
    return this.http
      .post<void>(`${this.API_URL}/add-points`, null, {
        params: { encryptedId, points: points.toString() },
      })
      .toPromise();
  }

}
