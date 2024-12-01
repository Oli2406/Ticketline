import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Observable} from "rxjs";
import {NewsDetailDto} from "../dtos/news-data";

@Injectable({
  providedIn: 'root'
})
export class NewsService {

  private baseUri: string = this.globals.backendUri + '/news'

  constructor(private httpClient: HttpClient,
              private globals: Globals) {
  }

  getUnreadNews(email: string): Observable<NewsDetailDto[]> {
    return this.httpClient.get<NewsDetailDto[]>(`${this.baseUri}?email=${email}`);
  }
}
