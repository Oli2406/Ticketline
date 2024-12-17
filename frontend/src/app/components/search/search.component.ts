import {AfterViewInit, Component, Pipe, PipeTransform} from '@angular/core';
import {EventListDto, EventSearch} from "../../dtos/event";
import {EventService} from "../../services/event.service";
import {CurrencyPipe, DatePipe, KeyValuePipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {ArtistListDto, ArtistSearch} from "../../dtos/artist";
import {ArtistService} from "../../services/artist.service";
import {LocationService} from "../../services/location.service";
import {PerformanceService} from "../../services/performance.service";
import {LocationListDto, LocationSearch} from "../../dtos/location";
import {PerformanceSearch, PerformanceDetailDto} from "../../dtos/performance";
import {debounceTime, forkJoin, map, Subject} from "rxjs";
import {FormsModule} from "@angular/forms";
import {RouterLink} from "@angular/router";
import {TicketService} from "../../services/ticket.service";

export enum SearchType {
  event,
  artist,
  performance,
  location,
  advanced
}

@Pipe({standalone: true, name: 'duration'})
export class DurationPipe implements PipeTransform {
  transform(value: number): string {
    const hours = Math.floor(value / 60);
    const minutes = value % 60;

    return `${hours}:${minutes.toString().padStart(2, '0')}`;
  }
}

declare var bootstrap: any;

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
    RouterLink,
    CurrencyPipe,
    DurationPipe
  ],
  templateUrl: './search.component.html',
  styleUrl: './search.component.scss'
})
export class SearchComponent implements AfterViewInit {
  events: EventListDto[] = [];
  artists: ArtistListDto[] = [];
  performances: PerformanceDetailDto[] = [];
  locations: LocationListDto[] = [];
  advancedSearchPerformances: PerformanceDetailDto[] = [];

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
    private locationService: LocationService,
    private ticketService: TicketService
  ) {
  }

  ngOnInit() {
    this.setupSearchListener();
    this.loadSearchType();
    this.updateData();
  }

  ngAfterViewInit() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map((tooltipTriggerEl) => new bootstrap.Tooltip(tooltipTriggerEl));
  }

  changeSearchType(type: SearchType) {
    if (type !== SearchType.advanced) {
      this.advancedSearchPerformances = [];
      this.searchQuery = '';
    }
    this.curSearchType = type;
    this.updateData();
    this.saveSearchType(type);
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
    this.updateTicketNumbers(); this.updateTicketNumbers();
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

  updateTicketNumbers() {
    this.performanceService.get().subscribe({
      next: (performances) => {
        const updateRequests = performances.map((performance) =>
          this.ticketService.getTicketsByPerformanceId(performance.performanceId).pipe(
            map((tickets) => {
              const availableTickets = tickets.filter(ticket => ticket.status === 'AVAILABLE').length;
              return this.performanceService.updateTicketNumber(performance.performanceId, availableTickets).subscribe();

            })
          )
        );
        forkJoin(updateRequests).subscribe({
          error: (err) => console.error('Error updating ticket numbers:', err),
        });
      },
      error: (err) => console.error('Error fetching performances for ticket update:', err),
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

  private saveSearchType(type: SearchType): void {
    localStorage.setItem('curSearchType', type.toString());
  }

  private loadSearchType(): void {
    const storedType = localStorage.getItem('curSearchType');
    if (storedType) {
      this.curSearchType = parseInt(storedType, 10) as SearchType;
    }
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

  truncate(text: string, maxLength: number): string {
    if (text.length > maxLength) {
      return text.substring(0, maxLength) + '...';
    } else {
      return text;
    }
  }

  protected readonly SearchType = SearchType;
}
