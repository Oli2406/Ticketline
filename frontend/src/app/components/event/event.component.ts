import { Component } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {EventService} from "../../services/event.service";
import {ToastrService} from "ngx-toastr";
import {EventListDto} from "../../dtos/event";
import {PerformanceService} from "../../services/performance.service";
import {PerformanceDetailDto} from "../../dtos/performance";
import {CurrencyPipe, DatePipe, NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'app-event',
  standalone: true,
  imports: [
    DatePipe,
    NgForOf,
    NgIf,
    CurrencyPipe
  ],
  templateUrl: './event.component.html',
  styleUrl: './event.component.scss'
})
export class EventComponent {
  constructor(private route: ActivatedRoute,
              private performanceService: PerformanceService,
              private eventService: EventService,
              private notification: ToastrService) {
  }

  private eventId: number | null = null;
  event: EventListDto;
  performances: PerformanceDetailDto[] = [];


  ngOnInit() {
    this.route.params.subscribe(params => {
      this.eventId = params['id'];

      if (this.eventId) {
        this.eventService.getById(this.eventId).subscribe({
          next: event => {
            this.event = event;
          },
          error: err => {
            this.notification.error('Failed to load event details.', 'Error');
            console.error('EventService error:', err);
          }
        });
        this.performanceService.getByEventId(this.eventId).subscribe({
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
}
