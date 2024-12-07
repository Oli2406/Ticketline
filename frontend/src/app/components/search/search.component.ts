import {Component} from '@angular/core';
import {EventListDto} from "../../dtos/event";
import {EventService} from "../../services/event.service";
import {DatePipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {ArtistListDto} from "../../dtos/artist";
import {ArtistService} from "../../services/artist.service";
import {LocationService} from "../../services/location.service";
import {PerformanceService} from "../../services/performance.service";
import {LocationListDto} from "../../dtos/location";
import {PerformanceWithNamesDto} from "../../dtos/performance";
import {forkJoin, map} from "rxjs";

export enum SearchType {
  event,
  artist,
  performance,
  location,
  advanced
}

@Component({
  selector: 'app-events',
  standalone: true,
  imports: [
    NgClass,
    DatePipe,
    NgForOf,
    NgIf
  ],
  templateUrl: './search.component.html',
  styleUrl: './search.component.scss'
})
export class SearchComponent {
  events: EventListDto[] = [];
  artists: ArtistListDto[] = [];
  performances: PerformanceWithNamesDto[] = [];
  locations: LocationListDto[] = [];

  searchTypes = SearchType;
  curType = this.searchTypes.event;


  constructor(private eventService: EventService,
              private artistService: ArtistService,
              private performanceService: PerformanceService,
              private locationService: LocationService) {
  }

  ngOnInit() {
    this.initData();
  }

  changeSearchType(type: SearchType) {
    this.curType = type;
    this.initData();
  }

  initData() {
    switch (this.curType) {
      case SearchType.event:
        this.updateEvents();
        break;
      case SearchType.artist:
        this.updateArtists();
        break;
      case SearchType.location:
        this.updateLocations();
        break;
      case SearchType.performance:
        this.updatePerformances();
        break;
    }
  }

  searchChanged() {

  }

  updateEvents() {
    this.eventService.get().subscribe({
      next: events => {
        this.events = events;
      }
    });
  }

  updateArtists() {
    this.artistService.getArtists().subscribe({
      next: artists => {
        this.artists = artists;
      }
    });
  }

  updateLocations() {
    this.locationService.getLocations().subscribe({
      next: locations => {
        this.locations = locations;
      }
    });
  }

  updatePerformances() {
    this.performanceService.getPerformances().subscribe({
      next: performances => {
        const performanceObservables = performances.map(p => {
          return forkJoin({
            location: this.locationService.getById(p.locationId),
            artist: this.artistService.getById(p.artistId)
          }).pipe(
            map(({ location, artist }) => ({
              name: p.name,
              date: p.date,
              performanceId: p.performanceId,
              price: p.price,
              ticketNumber: p.ticketNumber,
              hall: p.hall,
              locationName: location.name,
              artistName: `${artist.firstName} ${artist.surname}`
            }))
          );
        });

        forkJoin(performanceObservables).subscribe({
          next: performanceWithNamesArray => {
            this.performances = performanceWithNamesArray;
          },
          error: err => {
            console.error('Error loading performances:', err);
          }
        });
      },
      error: err => {
        console.error('Error fetching performances:', err);
      }
    });
  }
}

