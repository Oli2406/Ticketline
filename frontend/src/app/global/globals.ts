import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class Globals {
  readonly backendUri: string = this.findBackendUrl();
  readonly backendRessourceUri: string = this.findBackendRessourceUrl();

  private findBackendUrl(): string {
    if (window.location.port === '4200') { // local `ng serve`, backend at localhost:8080
      return 'http://localhost:8080/api/v1';
    } else {
      // assume deployed somewhere and backend is available at same host/port as frontend
      return window.location.protocol + '//' + window.location.host + window.location.pathname + 'api/v1';
    }
  }

  private findBackendRessourceUrl() {
    if (window.location.port === '4200') {
      return 'http://localhost:8080';
    } else {
      return window.location.protocol + '//' + window.location.host + window.location.pathname;
    }
  }
}


