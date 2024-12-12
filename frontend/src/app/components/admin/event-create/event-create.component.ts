import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {DatePipe, NgForOf, NgIf} from '@angular/common';
import {ArtistService} from 'src/app/services/artist.service';
import {LocationService} from 'src/app/services/location.service';
import {PerformanceService} from 'src/app/services/performance.service';
import {EventService} from 'src/app/services/event.service';
import {Artist, ArtistListDto} from 'src/app/dtos/artist';
import {Location, LocationListDto} from 'src/app/dtos/location';
import {Performance, PerformanceListDto} from 'src/app/dtos/performance';
import {Event} from 'src/app/dtos/event';
import {ToastrService} from 'ngx-toastr';
import {LocalStorageService} from "../../../services/LocalStorageService";
import {Hall, PriceCategory, SectorType, Ticket, TicketType} from 'src/app/dtos/ticket';
import {TicketService} from 'src/app/services/ticket.service';
import flatpickr from 'flatpickr';
import 'flatpickr/dist/flatpickr.css'; // Standard-Theme
import 'flatpickr/dist/themes/material_blue.css'; // Material Blue Theme


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
  eventData: Event = { title: '', description: '', dateFrom: null, dateTo: null, category: '', performanceIds: [] };
  newPerformance: Performance = { name: '', date: null, price: null, hall: '', artistId: null, locationId: null, ticketNumber: null, duration: null };

  newArtist: Artist = { firstName: '', lastName: '', artistName: '' };
  newLocation: Location = { name: '', street: '', city: '', postalCode: '', country: '' };

  showPerformanceForm = false;
  showArtistForm = false;
  showLocationForm = false;

  artists: ArtistListDto[] = [];
  locations: LocationListDto[] = [];
  performances: PerformanceListDto[] = [];

  selectedArtist = null;
  selectedLocation = null;
  flatpickrInstance: any;

  constructor(
    private datePipe: DatePipe,
    private artistService: ArtistService,
    private locationService: LocationService,
    private performanceService: PerformanceService,
    private eventService: EventService,
    private toastr: ToastrService,
    private localStorageService: LocalStorageService,
    private ticketService: TicketService
  ) {}

  ngOnInit() {
    this.loadFromLocalStorage();
    this.loadArtists();
    this.loadLocations();

    flatpickr("#dateRange", {
      mode: "range",
      dateFormat: "Y-m-d",
      onClose: (selectedDates: Date[], dateStr: string, instance: any) => {
        const [dateFrom, dateTo] = selectedDates;
        this.eventData.dateFrom = dateFrom;
        this.eventData.dateTo = dateTo;
        console.log("Date From:", this.eventData.dateFrom);
        console.log("Date To:", this.eventData.dateTo);
      },
      onReady: (selectedDates, dateStr, instance) => {
        this.flatpickrInstance = instance; // Speichere die Instanz
      }
    });
  }

  saveToLocalStorage() {
    this.localStorageService.saveData('eventData', this.eventData);
    //this.localStorageService.saveData('newPerformance', this.newPerformance);
    this.localStorageService.saveData('performances', this.performances);
    this.localStorageService.saveData('newArtist', this.newArtist);
    this.localStorageService.saveData('newLocation', this.newLocation);
  }

  loadFromLocalStorage() {
    const savedEventData = this.localStorageService.getData<Event>('eventData');
    //const savedNewPerformance = this.localStorageService.getData<Performance>('newPerformance');
    const savedPerformances = this.localStorageService.getData<PerformanceListDto[]>('performances');
    //const savedNewArtist = this.localStorageService.getData<Artist>('newArtist');
    //const savedNewLocation = this.localStorageService.getData<Location>('newLocation');

    if (savedEventData) this.eventData = savedEventData;
    //if (savedNewPerformance) this.newPerformance = savedNewPerformance;
    if (savedPerformances) this.performances = savedPerformances;
    //if (savedNewArtist) this.newArtist = savedNewArtist;
    //if (savedNewLocation) this.newLocation = savedNewLocation;
  }

  clearLocalStorage() {
    this.localStorageService.clearAll();
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
        this.newArtist = { firstName: '', lastName: '', artistName: '' };
        this.showArtistForm = false;
        //this.saveToLocalStorage();
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
        //this.saveToLocalStorage();
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

  onArtistSelect(artistName: string): void {
    this.artistService.getArtists().subscribe(artists => {
      const selectedArtist: ArtistListDto = artists.find(artist => artist.artistName === artistName);
      if (selectedArtist) {
        this.newPerformance.artistId = selectedArtist.artistId;

      }
    }, error => {
      console.error('Error fetching artist details:', error);
    });
  }

  onLocationSelect(locationName: string): void {
    this.locationService.getLocations().subscribe(locations => {
      const selectedLocation = locations.find(location => location.name === locationName);
      if (selectedLocation) {
        this.newPerformance.locationId = selectedLocation.locationId;
      }
    }, error => {
      console.error('Error fetching location details:', error);
    });
  }

  createPerformance() {
    this.performanceService.createPerformance(this.newPerformance).subscribe({
      next: (performance: PerformanceListDto) => {
        this.performances.push(performance);
        if (performance.performanceId) {
          this.eventData.performanceIds?.push(performance.performanceId);
          this.generateTicketsForPerformance(performance.performanceId, this.newPerformance.hall, this.newPerformance.date);
        }
        this.toastr.success('Performance created successfully!', 'Success');
        //this.saveToLocalStorage();
        this.newPerformance = { name: '', date: null, price: null, hall: '', artistId: null, locationId: null, ticketNumber: null, duration: null };
        this.showPerformanceForm = false;
      },
      error: (err) => {
        //err = this.eventService.handleErrorAndRethrow(err);
        const errors = Array.isArray(err.message)
          ? err.message
          : err.message.split(/\n/);
        const errorList = errors
          .map((error) => `<li>${error.trim()}</li>`)
          .join('');
        this.toastr.error(
          `<ul>${errorList}</ul>`,
          'Error creating performance',
          { enableHtml: true }
        );
      },
    });
  }

  generateTicketsForPerformance(performanceId: number, hall: string, date: Date) {
    const tickets: Ticket[] = [];

    if (hall === 'A') {
      // Sitzplätze in Sektor B
      for (let row = 1; row <= 12; row++) {
        for (let seat = 1; seat <= 20; seat++) {
          tickets.push({
            rowNumber: row,
            seatNumber: seat,
            priceCategory: PriceCategory.PREMIUM,
            ticketType: TicketType.SEATED,
            sectorType: SectorType.B,
            price: 120,
            status: 'AVAILABLE',
            performanceId,
            reservationNumber: 0,
            hall: Hall.A,
            date: date,
          });
        }
      }

      // Sitzplätze in Sektor C
      for (let row = 1; row <= 12; row++) {
        for (let seat = 1; seat <= 20; seat++) {
          tickets.push({
            rowNumber: row,
            seatNumber: seat,
            priceCategory: PriceCategory.PREMIUM,
            ticketType: TicketType.SEATED,
            sectorType: SectorType.C,
            price: 120,
            status: 'AVAILABLE',
            performanceId,
            reservationNumber: 0,
            hall: Hall.A,
            date: date,
          });
        }
      }

      for (let i = 1; i <= 80; i++) {
        tickets.push({
          rowNumber: 0,
          seatNumber: 0,
          priceCategory: PriceCategory.VIP,
          ticketType: TicketType.STANDING,
          sectorType: SectorType.A,
          price: 150,
          status: 'AVAILABLE',
          performanceId,
          reservationNumber: 0,
          hall: Hall.A,
          date: date,
        });
      }

      for (let i = 1; i <= 100; i++) {
        tickets.push({
          rowNumber: 0,
          seatNumber: 0,
          priceCategory: PriceCategory.STANDARD,
          ticketType: TicketType.STANDING,
          sectorType: SectorType.A,
          price: 80,
          status: 'AVAILABLE',
          performanceId,
          reservationNumber: 0,
          hall: Hall.A,
          date: date,
        });
      }
    } else if (hall === 'B') {
      // Sitzplätze in Sektor B (3 x 14)
      for (let row = 1; row <= 3; row++) {
        for (let seat = 1; seat <= 14; seat++) {
          tickets.push({
            rowNumber: row,
            seatNumber: seat,
            priceCategory: PriceCategory.PREMIUM,
            ticketType: TicketType.SEATED,
            sectorType: SectorType.B,
            price: 80,
            status: 'AVAILABLE',
            performanceId,
            reservationNumber: 0,
            hall: Hall.B,
            date: date,
          });
        }
      }

      // Sitzplätze in Sektor C (Reihe 1: 15 Sitze, Reihe 2: 16 Sitze, ... Reihe 9: 23 Sitze)
      for (let row = 1; row <= 9; row++) {
        let seatsInRow = 14 + row; // Reihe 1 hat 15 Sitze, Reihe 2 hat 16 Sitze, usw.
        for (let seat = 1; seat <= seatsInRow; seat++) {
          tickets.push({
            rowNumber: row,
            seatNumber: seat,
            priceCategory: PriceCategory.STANDARD,
            ticketType: TicketType.SEATED,
            sectorType: SectorType.C,
            price: 60,
            status: 'AVAILABLE',
            performanceId,
            reservationNumber: 0,
            hall: Hall.B,
            date: date,
          });
        }
      }

      // Standing tickets (Standard: 80)
      for (let i = 1; i <= 80; i++) {
        tickets.push({
          rowNumber: 0,
          seatNumber: 0,
          priceCategory: PriceCategory.PREMIUM,
          ticketType: TicketType.STANDING,
          sectorType: SectorType.A,
          price: 70,
          status: 'AVAILABLE',
          performanceId,
          reservationNumber: 0,
          hall: Hall.B,
          date: date,
        });
      }

      // Standing tickets (VIP: 60)
      for (let i = 1; i <= 60; i++) {
        tickets.push({
          rowNumber: 0,
          seatNumber: 0,
          priceCategory: PriceCategory.VIP,
          ticketType: TicketType.STANDING,
          sectorType: SectorType.A,
          price: 100,
          status: 'AVAILABLE',
          performanceId,
          reservationNumber: 0,
          hall: Hall.B,
          date: date,
        });
      }
    }

    // Add logic for other halls (if needed)
    this.createTicketsInBackend(tickets);
  }

  createTicketsInBackend(tickets: Ticket[]) {
    const createRequests = tickets.map((ticket) =>
      this.ticketService.createTicket(ticket)
    );

    Promise.all(createRequests.map((req) => req.toPromise()))
      .then(() => {
        this.toastr.success('All tickets created successfully!', 'Success');
      })
      .catch((error) => {
        this.toastr.error('Error creating some tickets.', 'Error');
        console.error('Ticket creation errors:', error);
      });
  }

  onSubmit() {
    this.eventService.createEvent(this.eventData).subscribe({
      next: (event: Event) => {
        this.toastr.success('Event created successfully!', 'Success');
        this.eventData = { title: '', description: '', dateFrom: null, dateTo: null, category: '', performanceIds: [] };
        if (this.flatpickrInstance) {
          this.flatpickrInstance.clear();
        }
        this.performances = [];
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

  updateTicketNumber() {
    const hallCapacity = {
      A: 660,
      B: 317,
    };
    this.newPerformance.ticketNumber = hallCapacity[this.newPerformance.hall] || 0;
  }
}
