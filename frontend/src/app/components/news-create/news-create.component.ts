import {Component, OnInit} from '@angular/core';
import {NewsData} from "../../dtos/news-data";
import {NewsService} from "../../services/news.service";
import {ActivatedRoute, Router} from "@angular/router";
import {FormsModule, NgForm} from "@angular/forms";
import {Observable} from "rxjs";
import {formatDate} from "@angular/common";
// @ts-ignore
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-news-create',
  standalone: true,
  imports: [
    FormsModule
  ],
  templateUrl: './news-create.component.html',
  styleUrl: './news-create.component.scss'
})
export class NewsCreateComponent implements OnInit {

  news: NewsData = {
    title: '',
    summary: '',
    content: '',
    imageUrl: null,
    dateOfNews: new Date()
  };
  private selectedFile: File;

  constructor(private service: NewsService,
              private route: ActivatedRoute,
              //private toastr: ToastrService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.route.data.subscribe();
  }

  public onSubmit(form: NgForm): void {
    if (form.valid) {
      if (this.validateNews(this.news)) {
        const newsFormData = this.createFormData(this.news);
        //create a form date?
        let observable: Observable<NewsData>;
        observable = this.service.createNews(newsFormData);
        observable.subscribe({
          next: () => {
            //this.toastr.success('News successfully submitted');
            this.router.navigate([''])
          }
        })
      }
    }
  }

  /*private methods for validation*/

  private createFormData(news: NewsData): FormData {

    const newsData = new FormData;

    if (news.id != null) {
      newsData.append('id', news.id.toString())
    }

    newsData.append('title', news.title);
    newsData.append('summary', news.summary);
    newsData.append('content', news.content);
    newsData.append('dateOfNews', news.dateOfNews.toString());

    for (var imgUrl of news.imageUrl) {
      if (this.selectedFile) {
        try {
          newsData.append('images', new Blob([this.selectedFile], {type: "application/octet-stream"}), this.selectedFile.name);
        } catch (error) {
          console.error('Error while file uploading or reading', error);
        }
      }
    }
    return newsData;
  }

  private validateNews(news: NewsData) {
    let errorMessage = '';

    if (this.empty(news.title) || (this.isEmpty(news.title))) {
      errorMessage += 'The title cannot be blank\n';
    }

    if (this.toLongText(news.title, 255)) {
      errorMessage += 'The title cannot exceed the limit of 255 characters\n';
    }

    if (this.empty(news.summary) || (this.isEmpty(news.summary))) {
      errorMessage += 'The summary cannot be blank\n';
    }

    if (this.toLongText(news.summary, 1024)) {
      errorMessage += 'The summary cannot exceed the limit of 1024 characters\n';
    }

    if (this.empty(news.content) || (this.isEmpty(news.content))) {
      errorMessage += 'The content cannot be blank\n';
    }

    if (this.toLongText(news.content, 4096)) {
      errorMessage += 'The content cannot exceed the limit of 4096 characters\n';
    }

    if (formatDate(news.dateOfNews, 'yyyy-MM-dd', 'en_US') === formatDate(new Date(0), 'yyyy-MM-dd', 'en_US')) {
      errorMessage += 'No date of news f was given\n';
    }
    if (news.dateOfNews !== undefined &&
      formatDate(this.news.dateOfNews, 'yyyy-MM-dd', 'en_US') > formatDate(new Date(), 'yyyy-MM-dd', 'en_US')) {
      errorMessage += 'The given date of news lies in the future\n';
    }

    return this.empty(errorMessage);
  }

  empty(str: string | undefined) {
    return !str || !/[^\s]+/.test(str);
  }

  isEmpty(data: object | string) {
    if (typeof (data) === 'object') {
      if (JSON.stringify(data) === '{}' || JSON.stringify(data) === '[]') {
        return true;
      } else if (!data) {
        return true;
      }
      return false;
    } else if (typeof (data) === 'string') {
      if (!data.trim()) {
        return true;
      }
      return false;
    } else {
      return false;
    }
  }

  private toLongText(name: string | undefined, length: number) {
    if (name !== undefined && (name.length > length || name.split(' ').length > length)) {
      return true;
    } else {
      return false;
    }
  }

  readImage(event: Event) {

    const imageInput = event.target as HTMLInputElement;
    const reader = new FileReader();

    if (imageInput.files != null && imageInput.files.length > 0) {
      const file = imageInput.files.item(0);
      if (file != null) {
        this.selectedFile = file;

        reader.onload = (e: ProgressEvent<FileReader>) => {
          const result = e.target?.result as string;
          this.news.imageUrl.push(result);
        };
        reader.readAsDataURL(file);
      }
    }
  }
}
