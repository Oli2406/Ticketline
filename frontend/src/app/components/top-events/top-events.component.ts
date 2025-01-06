import { AfterViewInit, Component, ViewChild, ElementRef } from '@angular/core';
import { Chart } from 'chart.js/auto';
import { EventService } from "../../services/event.service";
import { EventSalesDto } from "../../dtos/event";
import { ToastrService } from "ngx-toastr";

@Component({
  selector: 'app-top-events',
  standalone: true,
  imports: [],
  templateUrl: './top-events.component.html',
  styleUrl: './top-events.component.scss'
})
export class TopEventsComponent implements AfterViewInit {
  @ViewChild('barCanvas') barCanvas: ElementRef;

  constructor(private eventService: EventService,
              private notification: ToastrService) { }

  data: EventSalesDto[] = [];

  ngAfterViewInit() {
    this.eventService.getTop10Events().subscribe({
      next: data => {
        this.data = data;
        this.renderChart();
      },
      error: err => {
        this.notification.error('Failed to load top ten events.', 'Error');
        console.error('EventService error:', err);
      }
    });
  }

  private renderChart(): void {
    if (!this.data || this.data.length === 0) {
      return;
    }

    const labels = this.data.map(event => event.eventTitle);
    const percentages = this.data.map(event =>
      parseFloat((event.soldPercentage*100).toFixed(2))
    );

    const ctx = this.barCanvas.nativeElement.getContext('2d');
    new Chart(ctx, {
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
