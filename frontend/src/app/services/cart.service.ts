import { Injectable } from '@angular/core';
import { Merchandise } from '../dtos/merchandise';
import { TicketDto } from '../dtos/ticket';
import { AuthService } from './auth.service';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';

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

  getCart(): { item: Merchandise | TicketDto; quantity: number }[] {
    const storedCart = localStorage.getItem(this.getCartKey());
    return storedCart ? JSON.parse(storedCart) : [];
  }

  private saveCart(cartItems: { item: Merchandise | TicketDto; quantity: number }[]): void {
    localStorage.setItem(this.getCartKey(), JSON.stringify(cartItems));
  }

  addToCart(item: Merchandise | TicketDto): void {
    const cartItems = this.getCart();
    const existingItem = cartItems.find(cartItem =>
      'merchandiseId' in item
        ? 'merchandiseId' in cartItem.item && cartItem.item.merchandiseId === item.merchandiseId
        : 'ticketId' in cartItem.item && cartItem.item.ticketId === item.ticketId
    );

    if (existingItem) {
      existingItem.quantity++;
    } else {
      cartItems.push({ item, quantity: 1 });
    }

    this.saveCart(cartItems);
  }

  updateCartItem(item: Merchandise | TicketDto, quantity: number): void {
    const cartItems = this.getCart();
    const cartItem = cartItems.find(cartItem =>
      'merchandiseId' in item
        ? 'merchandiseId' in cartItem.item && cartItem.item.merchandiseId === item.merchandiseId
        : 'ticketId' in cartItem.item && cartItem.item.ticketId === item.ticketId
    );

    if (cartItem) {
      cartItem.quantity = quantity;
      this.saveCart(cartItems);
    }
  }

  removeFromCart(item: Merchandise | TicketDto): void {
    let cartItems = this.getCart();
    cartItems = cartItems.filter(cartItem =>
      'merchandiseId' in item
        ? !('merchandiseId' in cartItem.item && cartItem.item.merchandiseId === item.merchandiseId)
        : !('ticketId' in cartItem.item && cartItem.item.ticketId === item.ticketId)
    );
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

  purchaseItems(purchasePayload: { itemId: number; quantity: number }[]): Promise<void> {
    this.API_URL = this.globals.backendUri + '/users/purchase';
    return this.http
      .post<void>(`${this.API_URL}`, purchasePayload, {
        headers: { 'Content-Type': 'application/json' }
      })
      .toPromise();
  }
}
