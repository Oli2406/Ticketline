import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {UserUpdateReadNewsDto} from "../dtos/user-data";
import {ToastrService} from "ngx-toastr";
import {Observable} from "rxjs";


@Injectable({
  providedIn: 'root'
})
export class UserService {

  private baseUri: string = this.globals.backendUri + '/users' //globals.backendUri = http://localhost:8080/api/v1

  constructor(private httpClient: HttpClient,
              private globals: Globals,
              private notification: ToastrService) {
  }

  updateReadNews(dto: UserUpdateReadNewsDto):Observable<boolean> {
    return this.httpClient.put<boolean>(`${this.baseUri}`, dto);
  }

  /*updateReadNews(dto: UserUpdateReadNewsDto): Observable<string> {


    this.notification.success(this.baseUri); // Verifies the URL is correct

    const dtoTest: UserUpdateReadNewsDto = {
      newsId: 2,
      email: 'karl.admin@email.com',
    }
    console.log('DTO being sent:', dto);
    console.log('Endpoint being hit:', this.baseUri);
    return this.httpClient.put<string>(`${this.baseUri}`, dto, {
      headers: { 'Content-Type': 'application/json' },
    });
  }*/
}
