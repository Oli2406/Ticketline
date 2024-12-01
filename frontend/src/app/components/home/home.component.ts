import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {NewsService} from "../../services/news.service";
import {NewsDetailDto} from "../../dtos/news-data";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  constructor(private authService: AuthService,
              private newsService: NewsService,
              private notification: ToastrService,
              private router: Router) {

  }

  ngOnInit() {
    if (this.isLoggedIn()) {
      this.initNews()
    }

  }

  news: NewsDetailDto[] = [];

  currentIndex = 0;
  displayedNews: NewsDetailDto[] = [];

  updateDisplayedNews() {
    this.displayedNews = this.news.slice(this.currentIndex, this.currentIndex + 3);
  }

  nextNews() {
    this.currentIndex += 3;
    this.updateDisplayedNews();
  }

  previousNews() {
    this.currentIndex -= 3;
    this.updateDisplayedNews();
  }

  truncateSummary(summary: string, maxLength: number): string {
    if (summary.length > maxLength) {
      return summary.substring(0, maxLength) + '...';
    } else {
      return summary;
    }
  }

  initNews() {
    this.newsService.getUnreadNews(this.authService.getUserEmailFromToken())
      .subscribe({
        next: news => {
          this.news = news;
          this.updateDisplayedNews()
        },
        error: error => {
          console.error('Error fetching news', error);
          this.notification.error('Could not fetch news');
        }
      });
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  navigateToNewsDetails(id: number) {
    this.router.navigate(['/news/details', id]);
  }

  trackByNewsId(index: number, news: NewsDetailDto): number {
    return news.id;
  }
}
