import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { PurchaseService } from '../../services/purchase.service';
import { ToastrService } from 'ngx-toastr';
import { TicketDto } from '../../dtos/ticket';
import { PurchaseListDto } from '../../dtos/purchase';
import { PerformanceService } from 'src/app/services/performance.service';
import {LocationService} from "../../services/location.service";
import {PerformanceDetailDto, PerformanceListDto} from "../../dtos/performance";
import {ArtistService} from "../../services/artist.service";

@Component({
  selector: 'app-order-overview',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-overview.component.html',
  styleUrls: ['./order-overview.component.scss'],
})
export class OrderOverviewComponent implements OnInit {
  reservedTickets: TicketDto[] = [];
  purchasedTickets: TicketDto[] = [];
  sortedTickets: { date: Date; reserved: TicketDto[]; purchased: TicketDto[]; showDetails: boolean }[] = [];
  pastTickets: { date: Date; purchased: TicketDto[]; showDetails: boolean }[] = [];
  performanceNames: { [performanceId: number]: PerformanceListDto } = {};
  artistCache: { [artistId: number]: string } = {};
  performanceLocations: { [locationId: number]: string } = {};

  constructor(
    private authService: AuthService,
    private purchaseService: PurchaseService,
    private toastr: ToastrService,
    private performanceService: PerformanceService,
    private locationService: LocationService,
    private artistService: ArtistService
  ) {}

  ngOnInit(): void {
    const userId = this.authService.getUserIdFromToken();
    if (userId) {
      this.loadUserPurchases(userId);
    } else {
      this.toastr.error('Unable to identify the user.', 'Error');
    }
  }

  loadUserPurchases(userId: string): void {
    this.purchaseService.getPurchasesByUser(userId).subscribe({
      next: (purchases: PurchaseListDto[]) => {
        this.processPurchases(purchases);
      },
      error: (err) => {
        console.error('Error fetching purchases:', err.message);
        this.toastr.error('Failed to load your purchases. Please try again.', 'Error');
      },
    });
  }

  private processPurchases(purchases: PurchaseListDto[]): void {
    const today = new Date();
    const currentMap: { [key: string]: { reserved: TicketDto[]; purchased: TicketDto[] } } = {};
    const pastMap: { [key: string]: { purchased: TicketDto[] } } = {};

    purchases.forEach((purchase) => {
      // Konvertiere das purchaseDate korrekt in ein JavaScript Date-Objekt
      const purchaseDate = new Date(purchase.purchaseDate);

      purchase.tickets.forEach((ticket) => {
        // Konvertiere das Datum des Tickets in ein Date-Objekt
        const eventDate = new Date(ticket.date);

        if (eventDate >= today) {
          // Aktuelle Käufe (zukünftige Veranstaltungen)
          const key = purchaseDate.toISOString(); // Verwende ISO-String als Schlüssel
          if (!currentMap[key]) {
            currentMap[key] = { reserved: [], purchased: [] };
          }
          if (ticket.status === 'RESERVED') {
            currentMap[key].reserved.push(ticket);
          } else if (ticket.status === 'SOLD') {
            currentMap[key].purchased.push(ticket);
          }
        } else if (ticket.status === 'SOLD') {
          // Vergangene Käufe (nur gekaufte Tickets)
          const key = purchaseDate.toISOString();
          if (!pastMap[key]) {
            pastMap[key] = { purchased: [] };
          }
          pastMap[key].purchased.push(ticket);
        }
      });
    });

    // Convert currentMap to sorted array (by date descending)
    this.sortedTickets = Object.entries(currentMap)
      .map(([date, tickets]) => ({
        date: new Date(date), // Konvertiere den Schlüssel (purchaseDate) zurück in ein Date-Objekt
        reserved: tickets.reserved,
        purchased: tickets.purchased,
        showDetails: false,
      }))
      .sort((a, b) => b.date.getTime() - a.date.getTime());

    // Convert pastMap to sorted array (by date descending)
    this.pastTickets = Object.entries(pastMap)
      .map(([date, tickets]) => ({
        date: new Date(date), // Konvertiere den Schlüssel (purchaseDate) zurück in ein Date-Objekt
        purchased: tickets.purchased,
        showDetails: false,
      }))
      .sort((a, b) => b.date.getTime() - a.date.getTime());
  }

  getPerformanceName(performanceId: number): string {
    if (!this.performanceNames[performanceId]) {
      this.performanceService.getPerformanceById(performanceId).subscribe({
        next: (performance) => {
          this.performanceNames[performanceId] = performance;
        },
        error: (err) => {
          console.error(`Failed to fetch performance with ID ${performanceId}:`, err);
          this.performanceNames[performanceId].name = 'Error loading name'; // Setze einen Fallback
        },
      });
      return 'Loading...'; // Platzhalter, während der Name geladen wird
    }

    return this.performanceNames[performanceId].name; // Gibt den Namen zurück, wenn er schon geladen wurde
  }

  getArtistName(performanceId: number): string {
    if (!this.performanceNames[performanceId]) {
      // Lade die Performance und speichere sie im Cache
      this.performanceService.getPerformanceById(performanceId).subscribe({
        next: (performance) => {
          this.performanceNames[performanceId] = performance;

          // Wenn Artist-ID vorhanden und nicht im Cache, lade den Künstlernamen
          if (!this.artistCache[performance.artistId]) {
            this.loadArtistName(performance.artistId);
          }
        },
        error: (err) => {
          console.error(`Failed to fetch performance with ID ${performanceId}:`, err);
        },
      });
      return 'Loading...'; // Platzhalter, während die Performance geladen wird
    }

    const artistId = this.performanceNames[performanceId].artistId;

    if (!this.artistCache[artistId]) {
      // Lade den Künstlernamen, wenn er nicht im Cache ist
      this.loadArtistName(artistId);
      return 'Loading artist...'; // Platzhalter, während der Künstlername geladen wird
    }

    return this.artistCache[artistId]; // Gibt den Künstlernamen zurück, wenn er im Cache ist
  }

  private loadArtistName(artistId: number): void {
    this.artistService.getById(artistId).subscribe({
      next: (artist) => {
        if(artist.artistName != null) {
          this.artistCache[artistId] = artist.artistName;
        } else {
          this.artistCache[artistId] = artist.firstName + " " + artist.lastName;
        }
      },
      error: (err) => {
        console.error(`Failed to fetch artist with ID ${artistId}:`, err);
        this.artistCache[artistId] = 'Unknown Artist';
      },
    });
  }

  getPerformanceLocation(performanceId: number): string {
    if (!this.performanceLocations[performanceId]) {
      this.performanceService.getPerformanceById(performanceId).subscribe({
        next: (performance) => {
          if (performance.locationId) {
            this.locationService.getById(performance.locationId).subscribe({
              next: (location) => {
                this.performanceLocations[performanceId] = location.name;
              },
              error: (err) => {
                console.error(`Error fetching location details for ID ${performance.locationId}:`, err);
                this.performanceLocations[performanceId] = 'Error loading location';
              },
            });
          } else {
            this.performanceLocations[performanceId] = 'Location not found';
          }
        },
        error: (err) => {
          console.error(`Error fetching performance with ID ${performanceId}:`, err);
          this.performanceLocations[performanceId] = 'Error loading performance';
        },
      });
      return 'Loading...';
    }
    return this.performanceLocations[performanceId];
  }
}
