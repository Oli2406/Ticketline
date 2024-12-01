import { Component } from '@angular/core';
import {ActivatedRoute, RouterOutlet} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {NewsService} from "../../services/news.service";
import {AuthService} from "../../services/auth.service";
import {UserService} from "../../services/user.service";
import {UserUpdateReadNewsDto} from "../../dtos/user-data";

@Component({
  selector: 'app-news-detail',
  standalone: true,
  imports: [
    RouterOutlet
  ],
  templateUrl: './news-detail.component.html',
  styleUrl: './news-detail.component.scss'
})
export class NewsDetailComponent {
  constructor(private authService: AuthService,
              private newsService: NewsService,
              private userService: UserService,
              private route: ActivatedRoute,
              private notification: ToastrService) {
  }

  newsId: number;

    ngOnInit() {
      this.route.params.subscribe(params => {
        this.newsId = params['id'];


        if (this.newsId) {
          // TODO: get news with newIds from newsService
          let dto: UserUpdateReadNewsDto = {
            newsId: this.newsId,
            email: this.authService.getUserEmailFromToken()
          };
          this.userService.updateReadNews(dto).subscribe({
            next: () => console.log('Successfully marked as read'),
            error: () => console.error('Error marking news as read')
          });
        }
      });
    }
}
