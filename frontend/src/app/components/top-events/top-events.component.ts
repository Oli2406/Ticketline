import {AfterViewInit, Component, ViewChild, ElementRef} from '@angular/core';
import {Chart} from 'chart.js/auto';
import {EventService} from "../../services/event.service";
import {EventSalesDto} from "../../dtos/event";
import {ToastrService} from "ngx-toastr";
import {FormsModule} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";
import {Router} from '@angular/router';
import {catchError} from 'rxjs/operators';
import {of} from 'rxjs';

@Component({
  selector: 'app-top-events',
  standalone: true,
  imports: [FormsModule, NgForOf, NgIf],
  templateUrl: './top-events.component.html',
  styleUrl: './top-events.component.scss',
})
export class TopEventsComponent implements AfterViewInit {
  @ViewChild('barCanvas') barCanvas: ElementRef<HTMLCanvasElement>;

  data: EventSalesDto[] = [];
  categories: string[] = [];
  selectedCategory: string = 'All categories';
  selectedMonth: string = '';
  maxMonth: string = '';
  minMonth: string = '';
  isEmptyData: boolean = false;
  private chart: Chart | null = null;

  constructor(
    private eventService: EventService,
    private notification: ToastrService,
    private router: Router
  ) {
  }

  setMaxMinMonth() {
    const today = new Date();
    const thisMonth = (today.getMonth() + 1).toString().padStart(2, '0');
    this.minMonth = `${today.getFullYear()}-${thisMonth}`;

    const nextYear = new Date(today.setMonth(today.getMonth() + 11));
    const year = nextYear.getFullYear();
    const month = (nextYear.getMonth() + 1).toString().padStart(2, '0'); // Ensure 2-digit month format
    this.maxMonth = `${year}-${month}`;
  }

  ngOnInit(): void {
    this.loadCategories();
    this.setMaxMinMonth();
  }

  ngAfterViewInit(): void {
    this.loadTopEvents();
  }

  private loadCategories(): void {
    this.eventService.getAllCategories().subscribe({
      next: (categories) => {
        this.categories = ['All categories', ...categories];
      },
      error: (err) => {
        this.handleError('Failed to load categories.', err);
      },
    });
  }

  loadTopEvents(): void {
    const category = this.selectedCategory === 'All categories' ? '' : this.selectedCategory;
    this.eventService
      .getTop10Events(this.selectedMonth, category)
      .pipe(
        catchError((err) => {
          this.handleError('Failed to load top ten events.', err);
          return of([]);
        })
      )
      .subscribe((data) => {
        this.data = data;
        this.isEmptyData = this.data.length === 0;
        this.renderChart();
      });
  }

  onFilterChange(): void {
    this.loadTopEvents();
  }

  private renderChart(): void {
    if (this.chart) {
      this.chart.destroy();
    }
    if (!this.data.length || !this.barCanvas?.nativeElement) {
      return;
    }

    const ctx = this.barCanvas.nativeElement.getContext('2d');
    this.chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: this.data.map((event) => event.eventTitle),
        datasets: [
          {
            label: 'Sold Tickets',
            data: this.data.map((event) => (event.soldTickets)),
            backgroundColor: 'rgba(77, 71, 195, 0.2)',
            borderColor: 'rgba(77, 71, 195, 1)',
            borderWidth: 1,
          },
        ],
      },
      options: {
        responsive: true,
        plugins: {
          legend: {display: true, position: 'top'},
          tooltip: {callbacks: {label: (context) => `${context.raw}`}},
        },
        onClick: (event, elements) => {
          if (elements.length > 0) {
            const index = elements[0].index;
            const clickedEvent = this.data[index];
            this.router.navigate(['/event', clickedEvent.eventId]);
          }
        },
      },
    });
  }

  private handleError(message: string, error: any): void {
    this.notification.error(message, 'Error');
    console.error('Error:', error);
  }
}
