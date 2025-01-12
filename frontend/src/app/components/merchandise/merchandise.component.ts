import { Component, OnInit } from '@angular/core';
import { Merchandise } from "../../dtos/merchandise";
import { MerchandiseService } from "../../services/merchandise.service";
import { AuthService } from "../../services/auth.service";
import { CartService } from "../../services/cart.service";
import { CommonModule } from "@angular/common";
import { ToastrService } from "ngx-toastr";
import {Globals} from "../../global/globals";

@Component({
  selector: 'app-merchandise',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './merchandise.component.html',
  styleUrl: './merchandise.component.scss'
})
export class MerchandiseComponent implements OnInit {
  merchandiseList: Merchandise[] = [];
  displayedMerchandise: Merchandise[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 8;
  totalPages: number = 0;
  accountPoints: number = 0;
  imageLocation: string = "";

  constructor(
    private merchandiseService: MerchandiseService,
    private authService: AuthService,
    private cartService: CartService,
    private toastr: ToastrService,
    private global: Globals
  ) {}

  ngOnInit(): void {
    this.fetchAccountPoints();
    this.merchandiseService.getMerchandise().subscribe(data => {
      this.merchandiseList = data;
      this.totalPages = Math.ceil(this.merchandiseList.length / this.itemsPerPage);
      this.updateDisplayedMerchandise();
      this.imageLocation = this.global.backendRessourceUri + '/merchandise/';
    });
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

  updateDisplayedMerchandise(): void {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.displayedMerchandise = this.merchandiseList.slice(startIndex, endIndex);
  }

  goToPage(page: number): void {
    if (page > 0 && page <= this.totalPages) {
      this.currentPage = page;
      this.updateDisplayedMerchandise();
    }
  }

  addToCart(item: Merchandise): void {
    this.toastr.success(item.name + " was successfully added to the cart.")
    this.cartService.addToCart(item);
  }
}
