import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  constructor(public authService: AuthService,
              public router: Router) {}

  currentPage: string = '';

  ngOnInit() {
  }

  setActivePage(page: string): void {
    this.currentPage = page;
    this.router.navigate([page]);
  }

  logoutUser(): void {
    this.authService.logoutUser();
    this.setActivePage('home');
  }
}
