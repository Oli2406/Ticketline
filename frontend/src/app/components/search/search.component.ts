import {Component} from '@angular/core';
import {EventListDto} from "../../dtos/event";
import {EventService} from "../../services/event.service";
import {DatePipe, KeyValuePipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {ArtistListDto, ArtistSearch} from "../../dtos/artist";
import {ArtistService} from "../../services/artist.service";
import {LocationService} from "../../services/location.service";
import {PerformanceService} from "../../services/performance.service";
import {LocationListDto} from "../../dtos/location";
import {PerformanceListDto, PerformanceWithNamesDto} from "../../dtos/performance";
import {debounceTime, forkJoin, map, Subject} from "rxjs";
import {FormsModule} from "@angular/forms";

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
    KeyValuePipe
  ],
  templateUrl: './search.component.html',
  styleUrl: './search.component.scss'
})
export class SearchComponent {
  events: EventListDto[] = [];
  artists: ArtistListDto[] = [];
  performances: PerformanceWithNamesDto[] = [];
  locations: LocationListDto[] = [];
  advancedSearchPerformances: PerformanceListDto[] = [];

  searchQuery: string = '';

  searchChangedObservable = new Subject<void>();
  curSearchType = SearchType.event;
  artistSearchParams: ArtistSearch = {};

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
    this.eventService.get().subscribe({
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
    this.locationService.getLocations().subscribe({
      next: locations => (this.locations = locations),
      error: err => console.error('Error fetching locations:', err)
    });
  }

  updatePerformances() {
    this.performanceService.getPerformances().subscribe({
      next: performances => {
        const performanceObservables = performances.map(p =>
          forkJoin({
            location: this.locationService.getById(p.locationId),
            artist: this.artistService.getById(p.artistId)
          }).pipe(
            map(({location, artist}) => ({
              ...p,
              locationName: location.name,
              artistName: `${artist.firstName} ${artist.surname}`
            }))
          )
        );

        forkJoin(performanceObservables).subscribe({
          next: performanceWithNamesArray => (this.performances = performanceWithNamesArray),
          error: err => console.error('Error loading performances:', err)
        });
      },
      error: err => console.error('Error fetching performances:', err)
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
    this.artistSearchParams.firstName = '';
    this.artistSearchParams.surname = '';
    this.artistSearchParams.artistName = '';
    this.searchQuery = '';
    this.searchChanged();
  }

  protected readonly SearchType = SearchType;
}
