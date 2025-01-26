import { Component } from '@angular/core';
import { Location as AppLocation } from '@angular/common';
import {ActivatedRoute} from "@angular/router";
import {PerformanceService} from "../../services/performance.service";
import {ToastrService} from "ngx-toastr";
import {PerformanceDetailDto} from "../../dtos/performance";
import {LocationService} from "../../services/location.service";
import {LocationListDto} from "../../dtos/location";
import {CurrencyPipe, DatePipe, NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'app-location',
  standalone: true,
  imports: [
    CurrencyPipe,
    DatePipe,
    NgForOf,
    NgIf
  ],
  templateUrl: './location.component.html',
  styleUrl: './location.component.scss'
})
export class LocationComponent {
  constructor(private route: ActivatedRoute,
              private performanceService: PerformanceService,
              private locationService: LocationService,
              private notification: ToastrService,
              private appLocation: AppLocation) {
  }

  private locationId: number | null = null;
  location: LocationListDto;
  performances: PerformanceDetailDto[] = [];


  ngOnInit() {
    this.route.params.subscribe(params => {
      this.locationId = params['id'];

      if (this.locationId) {
        this.locationService.getById(this.locationId).subscribe({
          next: location => {
            this.location = location;
          },
          error: err => {
            this.notification.error('Failed to load event details.', 'Error');
            console.error('EventService error:', err);
          }
        });
        this.performanceService.getByLocationId(this.locationId).subscribe({
          next: performances => {
            this.performances = performances;
          },
          error: err => {
            this.notification.error('Failed to load events for the artist.', 'Error');
            console.error('EventService error:', err);
          }
        });
      }
    });
  }
  goBack(): void {
    this.appLocation.back();
  }
  isPastDate(date: string | Date): boolean {
    const performanceDate = new Date(date);
    const currentDate = new Date();
    return performanceDate < currentDate;
  }
}
