import { AfterViewInit, Component, ViewChild, ElementRef } from '@angular/core';
import { Chart } from 'chart.js/auto';
import { EventService } from "../../services/event.service";
import { EventSalesDto } from "../../dtos/event";
import { ToastrService } from "ngx-toastr";
import {FormsModule} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'app-top-events',
  standalone: true,
  imports: [
    FormsModule,
    NgForOf,
    NgIf
  ],
  templateUrl: './top-events.component.html',
  styleUrl: './top-events.component.scss'
})
export class TopEventsComponent implements AfterViewInit {
  @ViewChild('barCanvas') barCanvas: ElementRef;

  data: EventSalesDto[] = [];
  categories: string[] = [];
  selectedCategory: string = '';
  selectedMonth: string = '';
  isEmptyData: boolean = false; // Flag for empty data
  private chart: Chart | null = null; // Keep track of the chart instance

  constructor(private eventService: EventService,
              private notification: ToastrService) { }

  ngOnInit() {
    this.eventService.getAllCategories().subscribe({
      next: categories => {
        this.categories = categories;
        this.selectedCategory = this.categories[0];
      },
      error: err => {
        this.notification.error('Failed to load categories.', 'Error');
        console.error('EventService error:', err);
      }
    });

    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    this.selectedMonth = `${year}-${month}`;
  }

  ngAfterViewInit() {
    this.fetchTopEvents();
  }

  fetchTopEvents() {
    this.eventService.getTop10Events(this.selectedMonth, this.selectedCategory).subscribe({
      next: data => {
        this.data = data;
        this.isEmptyData = this.data.length === 0; // Set flag based on data
        this.renderChart();
      },
      error: err => {
        this.notification.error('Failed to load top ten events.', 'Error');
        console.error('EventService error:', err);
      }
    });
  }

  onCategoryChange(event: Event) {
    this.fetchTopEvents();
  }

  onMonthChange(event: Event) {
    this.fetchTopEvents();
  }

  private renderChart(): void {
    if (this.chart) {
      this.chart.destroy(); // Destroy existing chart instance
    }

    if (!this.data || this.data.length === 0 || !this.barCanvas?.nativeElement) {
      return;
    }

    const labels = this.data.map(event => event.eventTitle);
    const percentages = this.data.map(event =>
      parseFloat((event.soldPercentage * 100).toFixed(2))
    );

    const ctx = this.barCanvas.nativeElement.getContext('2d');
    this.chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels,
        datasets: [{
          label: 'Sold Tickets in %',
          data: percentages,
          backgroundColor: 'rgba(77, 71, 195, 0.2)',
          borderColor: 'rgba(77, 71, 195, 1)',
          borderWidth: 1
        }]
      },
      options: {
        scales: {
          y: {
            beginAtZero: true,
            max: 100
          }
        },
        responsive: true,
        plugins: {
          legend: {
            display: true,
            position: 'top'
          },
          tooltip: {
            callbacks: {
              label: (context) => `${context.raw}%`
            }
          }
        }
      }
    });
  }
}
