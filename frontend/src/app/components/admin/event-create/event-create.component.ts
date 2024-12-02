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
  // Event and Performance Data
  eventData: Event = { title: '', description: '', dateOfEvent: null, category: '', duration: 0, performanceIds: [] };
  newPerformance: Performance = { name: '', date: null, price: 0, hall: '', artistId: null, locationId: null, ticketNumber: null };

  // New Artist and Location
  newArtist: Artist = { firstName: '', surname: '', artistName: '' };
  newLocation: Location = { name: '', street: '', city: '', postalCode: '', country: '' };

  // Show/Hide Form Controls
  showPerformanceForm = false;
  showArtistForm = false;
  showLocationForm = false;

  // Lists for Artists, Locations, and Performances
  artists: ArtistListDto[] = [];
  locations: LocationListDto[] = [];
  performances: PerformanceListDto[] = [];

  // Selected Artist and Location Variables
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
    // Load initial data
    this.loadArtists();
    this.loadLocations();
    this.loadPerformances();
  }

  // Load all Artists
  loadArtists() {
    this.artistService.getArtists().subscribe({
      next: (artists: ArtistListDto[]) => {
        this.artists = artists;
      },
      error: (err) => {
        console.error('Error fetching artists:', err.message);
        this.toastr.error('Failed to load artists. Please try again.', 'Error');
      },
    });
  }

  // Create new Artist
  createArtist() {
    this.artistService.createArtist(this.newArtist).subscribe({
      next: () => {
        this.toastr.success('Artist created successfully!', 'Success');
        this.newArtist = { firstName: '', surname: '', artistName: '' }; // Reset form
        this.showArtistForm = false; // Hide form
        this.loadArtists(); // Reload artist list
      },
      error: (err) => {
        console.error('Error creating artist:', err.message);
        this.toastr.error('Failed to create artist. Please try again.', 'Error');
      },
    });
  }

  // Load all Locations
  loadLocations() {
    this.locationService.getLocations().subscribe({
      next: (locations: LocationListDto[]) => {
        this.locations = locations;
      },
      error: (err) => {
        console.error('Error fetching locations:', err.message);
        this.toastr.error('Failed to load locations. Please try again.', 'Error');
      },
    });
  }

  // Create new Location
  createLocation() {
    this.locationService.createLocation(this.newLocation).subscribe({
      next: () => {
        this.toastr.success('Location created successfully!', 'Success');
        this.newLocation = { name: '', street: '', city: '', postalCode: '', country: '' }; // Reset form
        this.showLocationForm = false; // Hide form
        this.loadLocations(); // Reload location list
      },
      error: (err) => {
        console.error('Error creating location:', err.message);
        this.toastr.error('Failed to create location. Please try again.', 'Error');
      },
    });
  }

  // Load all Performances
  loadPerformances() {
    console.log('Current performances:', this.performances);
  }

  onArtistSelect(artistName: string): void {
    this.artistService.getArtists().subscribe(artists => {
      const selectedArtist: ArtistListDto = artists.find(artist => artist.artistName === artistName);
      if (selectedArtist) {
        console.log('Selected artist details:', selectedArtist);
        console.log('Selected artist id:', selectedArtist.artistId);
        this.newPerformance.artistId = selectedArtist.artistId; // Artist ID wird gesetzt
        console.log('Updated newPerformance with artistId:', this.newPerformance);
      } else {
        console.log('Artist not found for name:', artistName);
      }
    }, error => {
      console.error('Error fetching artist details:', error);
    });
  }

// Log selected location
  onLocationSelect(locationName: string): void {
    this.locationService.getLocations().subscribe(locations => {
      const selectedLocation = locations.find(location => location.name === locationName);
      if (selectedLocation) {
        console.log('Selected location details:', selectedLocation);
        console.log('Selected artist id:', selectedLocation.locationId);
        this.newPerformance.locationId = selectedLocation.locationId; // Location ID wird gesetzt
        console.log('Updated newPerformance with locationId:', this.newPerformance);
      } else {
        console.log('Location not found for name:', locationName);
      }
    }, error => {
      console.error('Error fetching location details:', error);
    });
  }


  // Create new Performance
  createPerformance() {
    console.log('Sending performance data to backend:', this.newPerformance);
    this.performanceService.createPerformance(this.newPerformance).subscribe({
      next: (performance: PerformanceListDto) => {
        console.log('Created performance:', performance);
        this.performances.push(performance); // Add new performance to the list
        if (performance.performanceId) {
          this.eventData.performanceIds?.push(performance.performanceId); // Add ID to event's performance list
        }
        this.toastr.success('Performance created successfully!', 'Success');
        // Reset form
        this.newPerformance = { name: '', date: null, price: 0, hall: '', artistId: null, locationId: null, ticketNumber: null };
        this.showPerformanceForm = false; // Hide form
      },
      error: (err) => {
        console.error('Error creating performance:', err.message);
        const errors = Array.isArray(err.message) ? err.message : err.message.split(/\n/);
        const errorList = errors.map((error) => `<li>${error.trim()}</li>`).join('');
        this.toastr.error(`<ul>${errorList}</ul>`, 'Error creating performance', { enableHtml: true });
      },
    });
  }


  // Create new Event
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
        console.error('Error creating event:', err.message);
        const errors = Array.isArray(err.message) ? err.message : err.message.split(/\n/);
        const errorList = errors.map((error) => `<li>${error.trim()}</li>`).join('');
        this.toastr.error(`<ul>${errorList}</ul>`, 'Error creating event', { enableHtml: true });
      },
    });
  }

  // Toggle forms
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
