import { Injectable } from '@angular/core';
import { Merchandise } from '../dtos/merchandise';
import { AuthService } from "./auth.service";
import { HttpClient } from "@angular/common/http";
import {Globals} from "../global/globals";

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private currentUserId: string = '';
  private readonly CART_STORAGE_KEY_PREFIX = 'cart_';
  private API_URL = '';

  constructor(private authService: AuthService,
              private http: HttpClient,
              private globals: Globals) {}



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
    this.API_URL = this.globals.backendUri + '/users/deduct-points'
    console.log(this.API_URL)
    const encryptedId = this.authService.getUserIdFromToken();
    return this.http
      .post<void>(`${this.API_URL}`, null, {
        params: { encryptedId, points: points.toString() },
      })
      .toPromise();
  }

  addPoints(points: number): Promise<void> {
    this.API_URL = this.globals.backendUri + '/users/add-points';
    const encryptedId = this.authService.getUserIdFromToken();
    return this.http
      .post<void>(`${this.API_URL}`, null, {
        params: { encryptedId, points: points.toString() },
      })
      .toPromise();
  }

  purchaseItems(purchasePayload: { itemId: number; quantity: number }[]): Promise<void> {
    this.API_URL = this.globals.backendUri + '/users/purchase'
    return this.http
      .post<void>(`${this.API_URL}`, purchasePayload, {
        headers: { 'Content-Type': 'application/json' }
      })
      .toPromise();
  }

}

