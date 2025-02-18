import { Injectable } from '@angular/core';
import { Merchandise } from '../dtos/merchandise';
import { TicketDto } from '../dtos/ticket';
import { AuthService } from './auth.service';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';
import {Purchase} from "../dtos/purchase";

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private currentUserId: string = '';
  private readonly CART_STORAGE_KEY_PREFIX = 'cart_';
  private API_URL = '';

  constructor(
    private authService: AuthService,
    private http: HttpClient,
    private globals: Globals
  ) {}

  private getCartKey(): string {
    this.currentUserId = this.authService.getUserIdFromToken();
    return `${this.CART_STORAGE_KEY_PREFIX}${this.currentUserId}`;
  }

  getCart(): { item: Merchandise | TicketDto; quantity: number }[] {
    const storedCart = localStorage.getItem(this.getCartKey());
    return storedCart ? JSON.parse(storedCart) : [];
  }

  saveCart(cartItems: { item: Merchandise | TicketDto; quantity: number }[]): void {
    localStorage.setItem(this.getCartKey(), JSON.stringify(cartItems));
  }

  addToCart(item: Merchandise | TicketDto): void {
    const cartItems = this.getCart();
    let existingItem;

    if ("merchandiseId" in item) {
      existingItem = cartItems.find(cartItem =>
        'merchandiseId' in cartItem.item
          ? cartItem.item.merchandiseId === item.merchandiseId
          : false
      );
    } else {
      existingItem = cartItems.find(cartItem =>
        'ticketId' in cartItem.item
          ? cartItem.item.ticketId === item.ticketId
          : false
      );
    }

    if (existingItem) {
      existingItem.quantity++;
    } else {
      const reservedUntil = "performanceId" in item
        ? new Date(Date.now() + 10 * 60 * 1000).toISOString()
        : null;

      cartItems.push({ item: { ...item, reservedUntil }, quantity: 1 });
    }

    this.saveCart(cartItems);
  }

  updateCartItem(item: Merchandise | TicketDto, quantity: number): void {
    const cartItems = this.getCart();
    let cartItem;

    if ("merchandiseId" in item) {
      cartItem = cartItems.find(cartItem =>
        'merchandiseId' in cartItem.item
          ? cartItem.item.merchandiseId === item.merchandiseId
          : false
      );
    } else {
      cartItem = cartItems.find(cartItem =>
        'ticketId' in cartItem.item
          ? cartItem.item.ticketId === item.ticketId
          : false
      );
    }

    if (cartItem) {
      cartItem.quantity = quantity;
      this.saveCart(cartItems);
    }
  }

  removeFromCart(item: Merchandise | TicketDto): void {
    let cartItems = this.getCart();

    if ("merchandiseId" in item) {
      cartItems = cartItems.filter(cartItem =>
        'merchandiseId' in cartItem.item
          ? cartItem.item.merchandiseId !== item.merchandiseId
          : true
      );
    } else {
      cartItems = cartItems.filter(cartItem =>
        'ticketId' in cartItem.item
          ? cartItem.item.ticketId !== item.ticketId
          : true
      );
    }

    this.saveCart(cartItems);
  }

  clearCart(): void {
    localStorage.removeItem(this.getCartKey());
  }

  deductPoints(points: number): Promise<void> {
    this.API_URL = this.globals.backendUri + '/users/deduct-points';
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

  purchase(purchasePayload: Purchase): Promise<void> {
    const API_URL = this.globals.backendUri + '/purchase';
    return this.http
      .post<void>(API_URL, purchasePayload, {
        headers: { 'Content-Type': 'application/json' },
      })
      .toPromise();
  }

}
