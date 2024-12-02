import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {DatePipe, NgForOf, NgIf} from '@angular/common';
import { ArtistService } from 'src/app/services/artist.service';
import { LocationService } from 'src/app/services/location.service';
import { PerformanceService } from 'src/app/services/performance.service';
import { EventService } from 'src/app/services/event.service';
import { Artist, ArtistListDto } from 'src/app/dtos/artist';
import { Location, LocationListDto } from 'src/app/dtos/location';
import { Performance, PerformanceListDto } from 'src/app/dtos/performance';
import { Event } from 'src/app/dtos/event';
import { ToastrService } from 'ngx-toastr';
import {catchError} from "rxjs";

@Component({
  selector: 'app-event-create',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    NgForOf
  ],
  templateUrl: './event-create.component.html',
  styleUrls: ['./event-create.component.scss'],
  providers: [DatePipe]
})
export class EventCreateComponent implements OnInit {
  eventData: Event = { title: '', description: '', dateOfEvent: null, category: '', duration: 0, performanceIds: [] };
  newPerformance: Performance = { name: '', date: null, price: 0, hall: '', artistId: null, locationId: null, ticketNumber: null };

  newArtist: Artist = { firstName: '', surname: '', artistName: '' };
  newLocation: Location = { name: '', street: '', city: '', postalCode: '', country: '' };

  showPerformanceForm = false;
  showArtistForm = false;
  showLocationForm = false;

  artists: ArtistListDto[] = [];
  locations: LocationListDto[] = [];
  performances: PerformanceListDto[] = [];

  selectedArtist = null;
  selectedLocation = null;

  constructor(
    private datePipe: DatePipe,
    private artistService: ArtistService,
    private locationService: LocationService,
    private performanceService: PerformanceService,
    private eventService: EventService,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.loadArtists();
    this.loadLocations();
    this.loadPerformances();
  }

  loadArtists() {
    this.artistService.getArtists().subscribe({
      next: (artists: ArtistListDto[]) => {
        this.artists = artists;
      },
      error: (err) => {
        console.error('Error during registration:', err.message);
        const errors = Array.isArray(err.message)
          ? err.message
          : err.message.split(/\n/);
        const errorList = errors
          .map((error) => `<li>${error.trim()}</li>`)
          .join('');
        this.toastr.error(`<ul>${errorList}</ul>`, 'Error loading artists', {
          enableHtml: true,
        });
      },
    });
  }

  createArtist() {
    this.artistService.createArtist(this.newArtist).subscribe({
      next: () => {
        this.toastr.success('Artist created successfully!', 'Success');
        this.newArtist = { firstName: '', surname: '', artistName: '' };
        this.showArtistForm = false;
        this.loadArtists();
      },
      error: (err) => {
        console.error('Error during registration:', err.message);
        const errors = Array.isArray(err.message)
          ? err.message
          : err.message.split(/\n/);
        const errorList = errors
          .map((error) => `<li>${error.trim()}</li>`)
          .join('');
        this.toastr.error(`<ul>${errorList}</ul>`, 'Error creating artist', {
          enableHtml: true,
        });
      },
    });
  }

  loadLocations() {
    this.locationService.getLocations().subscribe({
      next: (locations: LocationListDto[]) => {
        this.locations = locations;
      },
      error: (err) => {
        console.error('Error during registration:', err.message);
        const errors = Array.isArray(err.message)
          ? err.message
          : err.message.split(/\n/);
        const errorList = errors
          .map((error) => `<li>${error.trim()}</li>`)
          .join('');
        this.toastr.error(`<ul>${errorList}</ul>`, 'Error loading locations', {
          enableHtml: true,
        });
      },
    });
  }

  createLocation() {
    this.locationService.createLocation(this.newLocation).subscribe({
      next: () => {
        this.toastr.success('Location created successfully!', 'Success');
        this.newLocation = { name: '', street: '', city: '', postalCode: '', country: '' };
        this.showLocationForm = false;
        this.loadLocations();
      },
      error: (err) => {
        console.error('Error during registration:', err.message);
        const errors = Array.isArray(err.message)
          ? err.message
          : err.message.split(/\n/);
        const errorList = errors
          .map((error) => `<li>${error.trim()}</li>`)
          .join('');
        this.toastr.error(`<ul>${errorList}</ul>`, 'Error creating location', {
          enableHtml: true,
        });
      },
    });
  }

  loadPerformances() {
    console.log('Current performances:', this.performances);
  }

  onArtistSelect(artistName: string): void {
    this.artistService.getArtists().subscribe(artists => {
      const selectedArtist: ArtistListDto = artists.find(artist => artist.artistName === artistName);
      if (selectedArtist) {
        console.log('Selected artist details:', selectedArtist);
        console.log('Selected artist id:', selectedArtist.artistId);
        this.newPerformance.artistId = selectedArtist.artistId;
        console.log('Updated newPerformance with artistId:', this.newPerformance);
      } else {
        console.log('Artist not found for name:', artistName);
      }
    }, error => {
      console.error('Error fetching artist details:', error);
    });
  }

  onLocationSelect(locationName: string): void {
    this.locationService.getLocations().subscribe(locations => {
      const selectedLocation = locations.find(location => location.name === locationName);
      if (selectedLocation) {
        console.log('Selected location details:', selectedLocation);
        console.log('Selected artist id:', selectedLocation.locationId);
        this.newPerformance.locationId = selectedLocation.locationId;
        console.log('Updated newPerformance with locationId:', this.newPerformance);
      } else {
        console.log('Location not found for name:', locationName);
      }
    }, error => {
      console.error('Error fetching location details:', error);
    });
  }

  createPerformance() {
    console.log('Sending performance data to backend:', this.newPerformance);
    this.performanceService.createPerformance(this.newPerformance).subscribe({
      next: (performance: PerformanceListDto) => {
        console.log('Created performance:', performance);
        this.performances.push(performance);
        if (performance.performanceId) {
          this.eventData.performanceIds?.push(performance.performanceId);
        }
        this.toastr.success('Performance created successfully!', 'Success');
        this.newPerformance = { name: '', date: null, price: 0, hall: '', artistId: null, locationId: null, ticketNumber: null };
        this.showPerformanceForm = false;
      },
      error: (err) => {
        //err = this.eventService.handleErrorAndRethrow(err);
        console.error('Error during registration:', err.message);
        const errors = Array.isArray(err.message)
          ? err.message
          : err.message.split(/\n/);
        const errorList = errors
          .map((error) => `<li>${error.trim()}</li>`)
          .join('');
        this.toastr.error(`<ul>${err.message}</ul>`, 'Error creating performance', {
          enableHtml: true,
        });
      },
    });
  }

  onSubmit() {
    this.eventService.createEvent(this.eventData).subscribe({
      next: (event: Event) => {
        console.log('Event created:', event);
        this.toastr.success('Event created successfully!', 'Success');
        this.eventData = { title: '', description: '', dateOfEvent: null, category: '', duration: 0, performanceIds: [] };
        this.performances = [];
        console.log('Cleared performances list after event creation:', this.performances);
      },
      error: (err) => {
        console.error('Error during registration:', err.message);
        const errors = Array.isArray(err.message)
          ? err.message
          : err.message.split(/\n/);
        const errorList = errors
          .map((error) => `<li>${error.trim()}</li>`)
          .join('');
        this.toastr.error(`<ul>${errorList}</ul>`, 'Error creating event', {
          enableHtml: true,
        });
      },
    });
  }

  toggleArtistForm() {
    this.showArtistForm = !this.showArtistForm;
  }

  toggleLocationForm() {
    this.showLocationForm = !this.showLocationForm;
  }

  togglePerformanceForm() {
    this.showPerformanceForm = !this.showPerformanceForm;
  }
}
