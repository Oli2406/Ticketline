import {Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {NewsService} from '../../services/news.service';
import {AuthService} from '../../services/auth.service';
import {UserService} from '../../services/user.service';
import {NewsDto} from '../../dtos/news-data';
import {Globals} from '../../global/globals';
import {NgIf} from '@angular/common';
import {UserUpdateReadNewsDto} from "../../dtos/user-data";

@Component({
  selector: 'app-news-detail',
  standalone: true,
  templateUrl: './news-detail.component.html',
  imports: [
    NgIf
  ],
  styleUrls: ['./news-detail.component.scss']
})
export class NewsDetailComponent {
  newsId: number;
  news: NewsDto = {
    title: '',
    summary: '',
    content: '',
    images: [],
    date: new Date()
  };

  currentImageIndex: number = 0;

  constructor(
    private authService: AuthService,
    private newsService: NewsService,
    private userService: UserService,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private globals: Globals
  ) {
  }

  ngOnInit() {
    this.route.params.subscribe((params) => {
      this.newsId = params['id'];

      if (this.newsId) {
        this.newsService.getById(this.newsId).subscribe(
          (news) => {
            this.news.title = news.title;
            this.news.summary = news.summary;
            this.news.content = news.content;
            this.news.date = news.date;

            if (news.images != null) {
              for (let image of news.images) {
                this.news.images.push(this.globals.backendRessourceUri + '/newsImages/' + image);
              }
            }
          },
          (error) => {
            console.error(`Fetching error during GET request with articleId ${this.newsId}`, error);
            this.notification.error(error, `Could not fetch news with ID ${this.newsId}`);
          }
        );
        let dto: UserUpdateReadNewsDto = {
          newsId: this.newsId,
          email: this.authService.getUserEmailFromToken()
        };
        this.userService.updateReadNews(dto).subscribe({
          error: () => console.error('Error marking news as read')
        });
      }
    });
  }

  showNextImage() {
    if (this.currentImageIndex < this.news.images.length - 1) {
      this.currentImageIndex++;
    }
  }

  showPreviousImage() {
    if (this.currentImageIndex > 0) {
      this.currentImageIndex--;
    }
  }
}
