import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {NewsService} from "../../services/news.service";
import {NewsDetailDto} from "../../dtos/news-data";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {Globals} from "../../global/globals";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  constructor(private authService: AuthService,
              private newsService: NewsService,
              private globals: Globals,
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

  truncate(summary: string, maxLength: number): string {
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

          for (const n of this.news) {
            if (n.images && n.images[0]) {
              n.images[0] = this.globals.backendRessourceUri + "/newsImages/" + n.images[0];
            } else {
              n.images.push(this.globals.backendRessourceUri + "/newsImages/none.png");
            }
          }
          this.updateDisplayedNews();
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
}
