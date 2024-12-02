import { Component } from '@angular/core';
import {Merchandise} from "../../dtos/merchandise";
import {MerchandiseService} from "../../services/merchandise.service";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-merchandise',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './merchandise.component.html',
  styleUrl: './merchandise.component.scss'
})
export class MerchandiseComponent {
  merchandiseList: Merchandise[] = [];
  displayedMerchandise: Merchandise[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 8;
  totalPages: number = 0;

  constructor(private merchandiseService: MerchandiseService) {}

  ngOnInit(): void {
    this.merchandiseService.getMerchandise().subscribe(data => {
      this.merchandiseList = data;
      this.totalPages = Math.ceil(this.merchandiseList.length / this.itemsPerPage);
      this.updateDisplayedMerchandise();
    });
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
