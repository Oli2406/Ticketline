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
import {catchError, forkJoin, map, Observable} from "rxjs";


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
  performances: Performance[] = [];

  selectedArtist = null;
  selectedLocation = null;

  constructor(
    private datePipe: DatePipe,
    private artistService: ArtistService,
    private locationService: LocationService,
    private performanceService: PerformanceService,
    private eventService: EventService,
    private toastr: ToastrService,
    private localStorageService: LocalStorageService,
    private ticketService: TicketService,
  ) {}

  ngOnInit() {
    this.loadFromLocalStorage();
    this.loadArtists();
    this.loadLocations();
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
    this.performances.push(this.newPerformance);
    this.toastr.success('Performance added locally!', 'Success');
    this.newPerformance = { name: '', date: null, price: null, hall: '', artistId: null, locationId: null, ticketNumber: null, duration: null };
    this.showPerformanceForm = false;
  }

  generateTicketsForPerformance(performanceId: number, hall: string, date: Date): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      const tickets: Ticket[] = [];

      if (hall === 'A') {
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

        for (let row = 1; row <= 9; row++) {
          let seatsInRow = 14 + row;
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
      this.createTicketsInBackend(tickets).subscribe({
        next: () => {
          resolve();
        },
        error: (err) => {
          console.error('Error creating tickets in backend:', err);
          reject(err);
        }
      });
    });
  }


  createTicketsInBackend(tickets: Ticket[]): Observable<void> {
    const createRequests = tickets.map((ticket) =>
      this.ticketService.createTicket(ticket)
    );

    return forkJoin(createRequests).pipe(
      catchError((error) => {
        this.toastr.error('Error creating some tickets.', 'Error');
        console.error('Ticket creation errors:', error);
        throw error;
      }),
      map(() => void 0)
    );
  }

  deletePerformance50(performanceId: number): void {
    this.performanceService.deletePerformance(performanceId).subscribe({
      next: () => {
        console.log(`Performance with ID ${performanceId} deleted successfully.`);
        this.toastr.success(`Performance with ID ${performanceId} deleted successfully!`);
      },
      error: (err) => {
        console.error(`Error deleting performance with ID ${performanceId}:`, err);
        this.toastr.error(`Failed to delete performance with ID ${performanceId}.`, 'Error');
      },
    });
  }


  onSubmit() {
    this.sendPerformancesToBackend()
      .then(() => {
        this.eventService.createEvent(this.eventData).subscribe({
          next: (event: Event) => {
            this.toastr.success('Event created successfully!', 'Success');
            this.eventData = { title: '', description: '', dateFrom: null, dateTo: null, category: '', performanceIds: [] };
            this.performances = [];
          },
          error: (err) => {
            console.error('Error during event creation:', err);
            if (this.eventData.performanceIds.length > 0) {
              const deletePromises = this.eventData.performanceIds.map((performanceId) => {
                return this.performanceService.deletePerformance(performanceId).toPromise();
              });

              Promise.all(deletePromises)
                .then(() => {
                  console.log('All created performances and Tickets have been deleted due to event creation error.');
                })
                .catch(deleteErr => {
                  console.error('Error deleting performances or their tickets:', deleteErr);
                });
            }
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
      })
      .catch(() => {
        console.error('Failed to save all performances.');
        this.toastr.error('Cannot create event because performance saving failed.', 'Error');
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
      B: 353,
    };
    this.newPerformance.ticketNumber = hallCapacity[this.newPerformance.hall] || 0;
  }

  async sendPerformancesToBackend(): Promise<void> {
    if (this.performances.length === 0) {
      this.toastr.warning('No performances to send.', 'Warning');
      return;
    }

    const performancePromises = this.performances.map((performance) => {
      return new Promise<void>((resolve, reject) => {
        this.performanceService.createPerformance(performance).subscribe({
          next: (createdPerformance: PerformanceListDto) => {
            if (createdPerformance.performanceId) {
              this.eventData.performanceIds.push(createdPerformance.performanceId);
              this.generateTicketsForPerformance(
                createdPerformance.performanceId,
                createdPerformance.hall,
                createdPerformance.date
              ).then(() => {
                resolve();
              }).catch((ticketError) => {
                this.toastr.error('Error generating tickets.', 'Error');
                reject(ticketError);
              });
            } else {
              this.toastr.error('Performance creation failed.', 'Error');
              reject(new Error('Invalid performance response'));
            }
          },
          error: (err) => {
            this.toastr.error(
              `Error creating performance: ${err.message || 'Unknown error'}`,
              'Error'
            );
            reject(err);
          }
        });
      });
    });

    try {
      await Promise.all(performancePromises);
    } catch (error) {
      this.toastr.error(
        'One or more performances failed to process. Check errors for details.',
        'Error'
      );
    }
  }

  deletePerformance(index: number): void {
    this.performances = this.performances.filter((_, i) => i !== index);
    this.eventData.performanceIds.splice(index, 1);
  }
}

