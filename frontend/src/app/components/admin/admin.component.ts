import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AdminService } from '../../services/admin.service';
import { RegisterUser } from '../../dtos/register-data';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss'],
})
export class AdminComponent implements OnInit {
  isAdminRoute: boolean = true;
  users: RegisterUser[] = [];

  constructor(private router: Router, private adminService: AdminService) {}

  ngOnInit(): void {
    this.router.events.subscribe(() => {
      this.isAdminRoute = this.router.url === '/admin';
    });

    this.fetchUsers();
  }

  fetchUsers(): void {
    this.adminService.getUsers().subscribe((users: RegisterUser[]) => {
      this.users = users;
    });
  }

  navigateToCreateUser(): void {
    this.router.navigate(['/admin/createUser']);
    console.log('Navigating to Create User');
  }

  navigateToCreateNews(): void {
    console.log('Create News functionality will be implemented.');
  }

  navigateToCreateShow(): void {
    console.log('Create Show functionality will be implemented.');
  }

  navigateToCreateEvent(): void {
    console.log('Create Event functionality will be implemented.');
  }
  //TODO: toggle locked/unlocked user accounts
}
