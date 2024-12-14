import {Component} from '@angular/core';
import {EventListDto, EventSearch} from "../../dtos/event";
import {EventService} from "../../services/event.service";
import {DatePipe, KeyValuePipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {ArtistListDto, ArtistSearch} from "../../dtos/artist";
import {ArtistService} from "../../services/artist.service";
import {LocationService} from "../../services/location.service";
import {PerformanceService} from "../../services/performance.service";
import {LocationListDto, LocationSearch} from "../../dtos/location";
import {PerformanceListDto, PerformanceSearch, PerformanceDetailDto} from "../../dtos/performance";
import {debounceTime, Subject} from "rxjs";
import {FormsModule} from "@angular/forms";
import {RouterLink} from "@angular/router";

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
    NgIf,
    FormsModule,
    KeyValuePipe,
    RouterLink
  ],
  templateUrl: './search.component.html',
  styleUrl: './search.component.scss'
})
export class SearchComponent {
  events: EventListDto[] = [];
  artists: ArtistListDto[] = [];
  performances: PerformanceDetailDto[] = [];
  locations: LocationListDto[] = [];
  advancedSearchPerformances: PerformanceListDto[] = [];

  searchQuery: string = '';

  searchChangedObservable = new Subject<void>();
  curSearchType = SearchType.event;
  artistSearchParams: ArtistSearch = {};
  eventSearchParams: EventSearch = {};
  performanceSearchParams: PerformanceSearch = {};
  locationSearchParams: LocationSearch = {};

  constructor(
    private eventService: EventService,
    private artistService: ArtistService,
    private performanceService: PerformanceService,
    private locationService: LocationService
  ) {
  }

  ngOnInit() {
    this.setupSearchListener();
    this.updateData();
  }

  changeSearchType(type: SearchType) {
    if (type !== SearchType.advanced) {
      this.advancedSearchPerformances = [];
      this.searchQuery = '';
    }
    this.curSearchType = type;
    this.updateData();
  }

  setupSearchListener() {
    this.searchChangedObservable.pipe(debounceTime(300)).subscribe(() => this.updateData());
  }

  updateData() {
    const updateActions: { [key in SearchType]: () => void } = {
      [SearchType.event]: this.updateEvents.bind(this),
      [SearchType.artist]: this.updateArtists.bind(this),
      [SearchType.location]: this.updateLocations.bind(this),
      [SearchType.performance]: this.updatePerformances.bind(this),
      [SearchType.advanced]: () => {
        if (this.searchQuery.trim() !== '') {
          this.performAdvancedSearch();
        } else {
          this.advancedSearchPerformances = [];
        }
      },
    };

    const updateAction = updateActions[this.curSearchType];
    if (updateAction) updateAction();
  }


  updateEvents() {
    this.eventService.getAllByFilter(this.eventSearchParams).subscribe({
      next: events => (this.events = events),
      error: err => console.error('Error fetching events:', err)
    });

  }

  updateArtists() {
    this.artistService.getAllByFilter(this.artistSearchParams).subscribe({
      next: artists => (this.artists = artists),
      error: err => console.error('Error fetching artists:', err)
    });

  }

  updateLocations() {
    this.locationService.getAllByFilter(this.locationSearchParams).subscribe({
      next: locations => (this.locations = locations),
      error: err => console.error('Error fetching locations:', err)
    });
  }

  updatePerformances() {
    this.performanceService.getAllByFilter(this.performanceSearchParams).subscribe({
      next: performances => (this.performances = performances),
      error: err => console.error('Error fetching artists:', err)
    });
  }

  performAdvancedSearch() {
    if (!this.searchQuery || this.searchQuery.trim() === '') {
      this.advancedSearchPerformances = [];
      return;
    }
    this.performanceService.advancedSearchPerformances(this.searchQuery).subscribe({
      next: (performances) => {
        this.advancedSearchPerformances = performances;
      },
      error: (err) => {
        console.error('Error performing advanced search:', err);
        this.advancedSearchPerformances = [];
      }
    });
  }


  searchChanged(): void {
    this.advancedSearchPerformances = [];
    this.searchChangedObservable.next();
  }

  clearSearch() {
    this.artistSearchParams = {};
    this.eventSearchParams = {};
    this.locationSearchParams = {};
    this.performanceSearchParams = {};
    this.searchQuery = '';
    this.searchChanged();
  }

  protected readonly SearchType = SearchType;
}
