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
    private eventService: EventService
  ) {}

  ngOnInit() {
    // Load initial data
    this.loadArtists();
    this.loadLocations();
    this.loadPerformances();
  }

  // Load all Artists
  loadArtists() {
    this.artistService.getArtists().subscribe((artists: ArtistListDto[]) => {
      this.artists = artists;
    });
  }

  // Create new Artist
  createArtist() {
    console.log('Sending Artist Data:', this.newArtist); // Log the artist data
    this.artistService.createArtist(this.newArtist).subscribe(() => {
      this.newArtist = { firstName: '', surname: '', artistName: '' }; // Reset form
      this.showArtistForm = false; // Hide form
      this.loadArtists(); // Reload artist list
    });
  }

  // Load all Locations
  loadLocations() {
    this.locationService.getLocations().subscribe((locations: LocationListDto[]) => {
      this.locations = locations;
    });
  }

  // Create new Location
  createLocation() {
    this.locationService.createLocation(this.newLocation).subscribe(() => {
      this.newLocation = { name: '', street: '', city: '', postalCode: '', country: '' }; // Reset form
      this.showLocationForm = false; // Hide form
      this.loadLocations(); // Reload location list
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
    // Log the performance data being sent to the backend
    console.log('Sending performance data to backend:', this.newPerformance);
    console.log('Selected Artist:', this.selectedArtist);
    console.log('Selected Location:', this.selectedLocation);
    this.performanceService.createPerformance(this.newPerformance).subscribe(
      (performance: PerformanceListDto) => {
        // Log the response from the backend
        console.log('Created performance:', performance);
        // Add new performance to the list
        this.performances.push(performance);
        if (performance.performanceId) {
          this.eventData.performanceIds?.push(performance.performanceId); // Add ID to event's performance list
        }
        // Reset form
        this.newPerformance = { name: '', date: null, price: 0, hall: '', artistId: null, locationId: null, ticketNumber: null };
        this.showPerformanceForm = false; // Hide form
      },
      (error) => {
        // Log the error response
        console.error('Error creating performance:', error);
      }
    );
  }


  // Create new Event
  onSubmit() {
    this.eventService.createEvent(this.eventData).subscribe((event: Event) => {
      console.log('Event created:', event);
      // Reset event data
      this.eventData = { title: '', description: '', dateOfEvent: null, category: '', duration: 0, performanceIds: [] };
      this.performances = [];
      console.log('Cleared performances list after event creation:', this.performances);
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
