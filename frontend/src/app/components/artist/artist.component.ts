import {Component} from '@angular/core';
import { Location as AppLocation } from '@angular/common';
import {ActivatedRoute, RouterLink} from "@angular/router";
import {ArtistService} from "../../services/artist.service";
import {ArtistListDto} from "../../dtos/artist";
import {EventService} from "../../services/event.service";
import {EventListDto} from "../../dtos/event";
import {NgForOf, NgIf} from "@angular/common";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-artist',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    RouterLink
  ],
  templateUrl: './artist.component.html',
  styleUrl: './artist.component.scss'
})
export class ArtistComponent {

  constructor(private route: ActivatedRoute,
              private artistService: ArtistService,
              private eventService: EventService,
              private notification: ToastrService,
              private appLocation: AppLocation) {
  }

  private artistId: number | null = null;
  artist: ArtistListDto;
  events: EventListDto[] = [];


  ngOnInit() {
    this.route.params.subscribe(params => {
      this.artistId = params['id'];

      if (this.artistId) {
        this.artistService.getById(this.artistId).subscribe({
          next: artist => {
            this.artist = artist;
          },
          error: err => {
            this.notification.error('Failed to load artist details.', 'Error');
            console.error('ArtistService error:', err);
          }
        });
        this.eventService.getEventsByArtistId(this.artistId).subscribe({
          next: events => {
            this.events = events;
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
}
