import { Component, OnInit } from '@angular/core';
import { Merchandise } from "../../dtos/merchandise";
import { MerchandiseService } from "../../services/merchandise.service";
import { AdminService } from "../../services/admin.service";
import { AuthService } from "../../services/auth.service";
import { CommonModule } from "@angular/common";

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

  constructor(
    private merchandiseService: MerchandiseService,
    private adminService: AdminService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.fetchAccountPoints();
    this.merchandiseService.getMerchandise().subscribe(data => {
      this.merchandiseList = data;
      this.totalPages = Math.ceil(this.merchandiseList.length / this.itemsPerPage);
      this.updateDisplayedMerchandise();
    });
  }

  fetchAccountPoints(): void {
    const email = this.authService.getUserEmailFromToken();
    if (email) {
      this.authService.getUserPoints(email).subscribe({
        next: (points) => {
          this.accountPoints = points;
          console.log(`Logged-in user's points: ${this.accountPoints}`);
        },
        error: (err) => {
          console.error('Failed to fetch user points:', err);
          this.accountPoints = 0;
        }
      });
    } else {
      console.warn('No email found in token');
      this.accountPoints = 0;
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
}
