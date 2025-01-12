import {Component} from '@angular/core';
import {NewsService} from "../../services/news.service";
import {Globals} from "../../global/globals";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {NewsDetailDto} from "../../dtos/news-data";
import {NgForOf, NgClass} from "@angular/common";

@Component({
  selector: 'app-news',
  standalone: true,
  imports: [
    NgClass,
    NgForOf
  ],
  templateUrl: './news.component.html',
  styleUrl: './news.component.scss'
})
export class NewsComponent {

  constructor(private newsService: NewsService,
              private globals: Globals,
              private notification: ToastrService,
              private router: Router) {
  }

  news: NewsDetailDto[] = [];
  displayedNews: NewsDetailDto[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 6;
  totalPages: number = 0;


  ngOnInit() {
    this.initNews();
  }

  private initNews() {
    this.newsService.getNews().subscribe({
      next: news => {
        this.news = news;

        for (const n of this.news) {
          if (n.images && n.images[0]) {
            n.images[0] = this.globals.backendRessourceUri + "/newsImages/" + n.images[0];
          } else {
            n.images.push(this.globals.backendRessourceUri + "/newsImages/none.png");
          }
        }

        this.totalPages = Math.ceil(this.news.length / this.itemsPerPage);
        this.updateDisplayedNews();
      }
    })
  }

  updateDisplayedNews(): void {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.displayedNews = this.news.slice(startIndex, endIndex);
  }

  goToPage(page: number): void {
    if (page > 0 && page <= this.totalPages) {
      this.currentPage = page;
      this.updateDisplayedNews();
    }
  }

  truncate(summary: string, maxLength: number): string {
    if (summary.length > maxLength) {
      return summary.substring(0, maxLength) + '...';
    } else {
      return summary;
    }
  }

  navigateToNewsDetails(id: number) {
    this.router.navigate(['/news/details', id]);
  }
}
