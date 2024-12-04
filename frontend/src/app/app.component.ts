import { Component } from '@angular/core';
import {AuthService} from "./services/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'SE PR Group Phase';

  constructor(private authService: AuthService, public router: Router) {}
  ngOnInit(): void {
    this.authService.validateToken().subscribe((isValid) => {
      if (!isValid) {
        this.authService.logoutUser();
        //this.router.navigate(['/home']);
      }
    });
  }

}
