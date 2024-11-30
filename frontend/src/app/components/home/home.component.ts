import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  constructor(public authService: AuthService) { }

  ngOnInit() {
    this.updateDisplayedEvents()
  }
  events = [
    {
      imageUrl: '/assets/images/pathToImage',
      title: 'News 1 Title',
      date: new Date('2025-01-01'),
      summary: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores '
    },
    {
      imageUrl: '/assets/images/pathToImage',
      title: 'News 2 slightly longer Title',
      date: new Date('2025-01-01'),
      summary: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea tak'
    },
    {
      imageUrl: '/assets/images/pathToImage',
      title: 'News 3 much much longer Title to test behaviour',
      date: new Date('2025-01-01'),
      summary: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut l.'
    },
    {
      imageUrl: '/assets/images/pathToImage',
      title: 'News 4 Title',
      date: new Date('2025-01-01'),
      summary: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam volu.'
    },
  ];

  currentIndex = 0;
  displayedEvents: any[] = [];

  updateDisplayedEvents() {
    this.displayedEvents = this.events.slice(this.currentIndex, this.currentIndex + 3);
  }

  nextEvents() {
    this.currentIndex += 3;
    this.updateDisplayedEvents();
  }

  previousEvents() {
    this.currentIndex -= 3;
    this.updateDisplayedEvents();
  }

  truncateSummary(summary: string, maxLength: number): string {
    if (summary.length > maxLength) {
      return summary.substring(0, maxLength) + '...';
    } else {
      return summary;
    }
  }
}
