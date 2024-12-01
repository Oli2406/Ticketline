import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgForOf, NgIf } from '@angular/common';
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
  styleUrls: ['./event-create.component.scss']
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

  constructor(
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
    this.performanceService.getPerformances().subscribe(
      (performances: PerformanceListDto[]) => {
        console.log('Fetched performances:', performances);
        this.performances = performances;
      },
      (error) => {
        console.error('Error fetching performances:', error);
      }
    );
  }

  // Create new Performance
  createPerformance() {
    this.performanceService.createPerformance(this.newPerformance).subscribe((performance: PerformanceListDto) => {
      console.log('Created performance:', performance); // Logge die Antwort
      this.performances.push(performance); // Add new performance to list
      if (performance.performanceId) {
        this.eventData.performanceIds?.push(performance.performanceId); // Add ID to event's performance list
      }
      // Reset form
      this.newPerformance = { name: '', date: null, price: 0, hall: '', artistId: null, locationId: null, ticketNumber: null };
      this.showPerformanceForm = false; // Hide form
    },
      (error) => {
        console.error('Error creating performance:', error);
      });
  }

  // Create new Event
  onSubmit() {
    this.eventService.createEvent(this.eventData).subscribe((event: Event) => {
      console.log('Event created:', event);
      // Reset event data
      this.eventData = { title: '', description: '', dateOfEvent: null, category: '', duration: 0, performanceIds: [] };
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
