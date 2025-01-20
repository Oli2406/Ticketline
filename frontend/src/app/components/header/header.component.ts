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

  routeToUserCart(page: string): void {
    this.currentPage = page;
    const userId = this.authService.getUserIdFromToken();
    this.router.navigate([`/cart/${userId}`]);
  }

  logoutUser(): void {
    this.authService.isCurrentUserLoggedInInBackend().subscribe((isLoggedIn) => {
      if(!isLoggedIn){
        this.authService.clearAuthToken();
      } else {
        this.authService.logoutUser();
      }

      this.router.navigate(['/login']);
    });
  }

  isDropdownOpen: boolean = false;

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
  }
}
