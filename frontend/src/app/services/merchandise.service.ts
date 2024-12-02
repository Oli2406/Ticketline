import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class MerchandiseService {
  private apiUrl = 'http://localhost:8080/api/v1/merchandise/create';

  constructor(private http: HttpClient) {}

  createMerchandise(merchandiseData: any, imageFile: File): Observable<any> {
    const formData = new FormData();
    formData.append(
      'merchandise',
      new Blob([JSON.stringify(merchandiseData)], { type: 'application/json' })
    );
    if (imageFile) {
      formData.append('image', imageFile);
    }
    return this.http.post(this.apiUrl, formData);
  }
}
